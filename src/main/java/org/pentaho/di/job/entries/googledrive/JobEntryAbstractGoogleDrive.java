/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2016-2016 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.job.entries.googledrive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

public abstract class JobEntryAbstractGoogleDrive extends JobEntryBase implements Cloneable, JobEntryInterface {

  protected static final Class<?> PKG = JobEntryAbstractGoogleDrive.class;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY =
    JacksonFactory.getDefaultInstance();
  private static HttpTransport HTTP_TRANSPORT;

  static {
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    } catch ( Throwable t ) {
      t.printStackTrace();
    }
  }

  protected static final List<String> SCOPE_READONLY =
    Arrays.asList( DriveScopes.DRIVE_READONLY );

  protected static final List<String> SCOPE_READWRITE =
    Arrays.asList( DriveScopes.DRIVE );

  /**
   * @see <a href="https://developers.google.com/drive/v3/web/manage-downloads#downloading_google_documents">Google Drive API</a>
   */

  protected String serviceAccountId;
  protected String privateKeyFile;
  protected String applicationName = "PDI Google Drive Plugin";

  public String getApplicationName() {
    return applicationName;
  }

  public void setApplicationName( String applicationName ) {
    this.applicationName = applicationName;
  }

  public String getServiceAccountId() {
    return serviceAccountId;
  }

  public void setServiceAccountId( String serviceAccountId ) {
    this.serviceAccountId = serviceAccountId;
  }

  public String getPrivateKeyFile() {
    return privateKeyFile;
  }

  public void setPrivateKeyFile( String privateKeyFile ) {
    this.privateKeyFile = privateKeyFile;
  }

  @Override
  public String getXML() {
    StringBuilder retval = new StringBuilder();

    retval.append( super.getXML() );
    retval.append( "      " ).append( XMLHandler.addTagValue( "applicationName", getApplicationName() ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "serviceAccountId", getServiceAccountId() ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "privateKeyFile", getPrivateKeyFile() ) );

    return retval.toString();
  }

  @Override
  public void loadXML( Node entrynode, List<DatabaseMeta> databases, List<SlaveServer> slaveServers, Repository rep, IMetaStore metaStore ) throws KettleXMLException {
    try {
      super.loadXML( entrynode, databases, slaveServers );
      setApplicationName( XMLHandler.getTagValue( entrynode, "applicationName" ) );
      setServiceAccountId( XMLHandler.getTagValue( entrynode, "serviceAccountId" ) );
      setPrivateKeyFile( XMLHandler.getTagValue( entrynode, "privateKeyFile" ) );
    } catch ( Exception e ) {
      throw new KettleXMLException( BaseMessages.getString( PKG, "Demo.Error.UnableToLoadFromXML" ), e );
    }
  }

  @Override
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_job ) throws KettleException {
    super.saveRep( rep, metaStore, id_job );
    try {
      rep.saveJobEntryAttribute( id_job, getObjectId(), "applicationName", getApplicationName() );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "serviceAccountId", getServiceAccountId() );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "privateKeyFile", getPrivateKeyFile() );
    } catch ( KettleDatabaseException dbe ) {
      throw new KettleException( BaseMessages.getString( PKG, "Demo.Error.UnableToSaveToRepository" ) + id_job, dbe );
    }
  }

  @Override
  public void loadRep( Repository rep, IMetaStore metaStore, ObjectId id_jobentry, List<DatabaseMeta> databases, List<SlaveServer> slaveServers ) throws KettleException {
    super.loadRep( rep, metaStore, id_jobentry, databases, slaveServers );
    try {
      setApplicationName( rep.getJobEntryAttributeString( id_jobentry, "applicationName" ) );
      setServiceAccountId( rep.getJobEntryAttributeString( id_jobentry, "serviceAccountId" ) );
      setPrivateKeyFile( rep.getJobEntryAttributeString( id_jobentry, "privateKeyFile" ) );
    } catch ( KettleDatabaseException dbe ) {
      throw new KettleException( BaseMessages.getString( PKG, "Demo.Error.UnableToLoadFromRepository" ) + id_jobentry, dbe );
    }
  }

  private static Credential authorize( String serviceAccountId, java.io.File p12File, List<String> scopes ) throws KettleException {
    GoogleCredential.Builder builder;
    try {
      builder = new GoogleCredential.Builder()
        .setTransport( HTTP_TRANSPORT )
        .setJsonFactory( JSON_FACTORY )
        .setServiceAccountId( serviceAccountId )
        .setServiceAccountPrivateKeyFromP12File( p12File )
        .setServiceAccountScopes( scopes );
    } catch ( GeneralSecurityException | IOException e ) {
      throw new KettleException( BaseMessages.getString( PKG, "GoogleDrive.Error.CreatingCredential" ), e );
    }
    return builder.build();
  }

  protected Drive getDriveService( List<String> scopes ) throws KettleException {
    String realKeyFile = environmentSubstitute( privateKeyFile );
    Credential credential = authorize( serviceAccountId, new java.io.File( realKeyFile ), scopes );
    return new Drive.Builder( HTTP_TRANSPORT, JSON_FACTORY, credential )
      .setApplicationName( applicationName )
      .build();
  }

  protected static List<File> getFilesInDrive( Drive driveService, String query ) throws IOException {
    List<File> files = driveService.files().list().setQ( query ).execute().getFiles();
    return files;
  }
}
