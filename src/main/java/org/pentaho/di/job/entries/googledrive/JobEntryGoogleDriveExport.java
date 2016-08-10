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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.Selectors;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.annotations.JobEntry;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

@JobEntry(
    id = "GoogleDriveExport",
    name = "GoogleDriveExport.Name",
    description = "GoogleDriveExport.TooltipDesc",
    image = "org/pentaho/di/job/entries/googledrive/product32.png",
    categoryDescription = "i18n:org.pentaho.di.job:JobCategory.Category.FileTransfer",
    i18nPackageName = "org.pentaho.di.job.entries.googledrive",
    documentationUrl = "GoogleDriveExport.DocumentationURL",
    casesUrl = "GoogleDriveExport.CasesURL",
    forumUrl = "GoogleDriveExport.ForumURL"
  )
public class JobEntryGoogleDriveExport extends JobEntryAbstractGoogleDrive {

  public JobEntryGoogleDriveExport() {
    super();
  }

  protected GoogleDriveExportFileSelection[] fileSelections;
  protected String targetFolder;
  protected boolean createTargetFolder;
  protected boolean addFilesToResult;

  @Override
  public Result execute( Result prev_result, int nr ) throws KettleException {
    Result result = prev_result;

    checkFolderExists( KettleVFS.getFileObject( environmentSubstitute( getTargetFolder() ) ), createTargetFolder );

    List<String> activeScope = SCOPE_READONLY;
    for ( int i = 0; i < fileSelections.length; i++ ) {
      if ( fileSelections[i].isDeleteSourceFile() ) {
        activeScope = SCOPE_READWRITE;
      }
    }
    if ( log.isDebug() ) {
      if ( activeScope == SCOPE_READWRITE ) {
        log.logDebug( BaseMessages.getString( PKG, "GoogleDriveExport.Debug.ReadWriteApiPermissions" ) );
      } else {
        log.logDebug( BaseMessages.getString( PKG, "GoogleDriveExport.Debug.ReadOnlyApiPermissions" ) );
      }
    }

    Drive driveService = getDriveService( activeScope );

    for ( int i = 0; i < fileSelections.length; i++ ) {
      GoogleDriveExportFileSelection fileSelection = fileSelections[i];
      List<File> driveFiles;
      String queryOptions = fileSelection.getFinalQueryOptions();
      if ( log.isDebug() ) {
        log.logDebug( BaseMessages.getString( PKG, "GoogleDriveExport.Debug.QueryInfo", queryOptions ) );
      }
      try {
        driveFiles = searchFiles( driveService, queryOptions );
      } catch ( IOException e ) {
        log.logError( BaseMessages.getString( PKG, "GoogleDriveExport.Error.BadQuery" ), e );
        result.setResult( false );
        result.increaseErrors( 1 );
        return result;
      }

      if ( log.isDetailed() ) {
        int fileCount = driveFiles == null ? 0 : driveFiles.size();
        log.logDetailed( BaseMessages.getString( PKG, "GoogleDriveExport.Log.DownloadFileCount", fileCount ) );
        if ( log.isDebug() ) {
          for ( File driveFile : driveFiles ) {
            log.logDebug( BaseMessages.getString( PKG, "GoogleDriveExport.Debug.DownloadFileNames", driveFile.getName() ) );
          }
        }
      }
      String realTargetFolder = KettleVFS.getFileObject( environmentSubstitute( getTargetFolder() ) ).getPublicURIString();
      for ( File driveFile : driveFiles ) {
        if ( fileSelection.getExportFormat() != null ) {
          String exportFilename = realTargetFolder + "/"
            + driveFile.getName() + "." + fileSelection.getExportFormat().getFileExtension();
          FileObject exportFile = KettleVFS.getFileObject( exportFilename );
          if ( log.isDetailed() ) {
            log.logDetailed( BaseMessages.getString( PKG, "GoogleDriveExport.Log.DownloadingFile", driveFile.getName() ) );
          }
          try {
            exportFile( driveService, driveFile, exportFile, fileSelection.getExportFormat() );
          } catch ( KettleException e ) {
            log.logError( BaseMessages.getString( PKG, "GoogleDriveExport.Error.ExportFile", driveFile.getName() ), e );
            result.setResult( false );
            result.increaseErrors( 1 );
            return result;
          }

          if ( fileSelection.isDeleteSourceFile() ) {
            if ( log.isDetailed() ) {
              log.logDetailed( BaseMessages.getString( PKG, "GoogleDriveExport.Log.DeletingSourceFile", driveFile.getName() ) );
            }
            try {
              deleteFile( driveService, driveFile );
            } catch ( IOException e ) {
              log.logError( BaseMessages.getString( PKG, "GoogleDriveExport.Error.DeletingSourceFile", driveFile.getName() ), e );
              result.setResult( false );
              result.increaseErrors( 1 );
              return result;
            }
          }
        }
      }
    }
    return result;
  }

  protected static void deleteFile( Drive driveService, File driveFile ) throws IOException {
    driveService.files().delete( driveFile.getId() ).execute();
  }

  protected static List<File> searchFiles( Drive driveService, String queryOptions ) throws IOException {
    return driveService.files().list().setQ( queryOptions ).execute().getFiles();
  }

  protected static void exportFile( Drive driveService, File driveFile, FileObject targetFile,
      GoogleDriveExportFormat exportMapping ) throws KettleException {
    Exception savedException = null;
    if ( exportMapping != null ) {
      FileObject tempFile = KettleVFS.createTempFile( JobEntryGoogleDriveExport.class.getSimpleName(), ".tmp",
        System.getProperty( "java.io.tmpdir" ) );
      try {
        OutputStream fos = tempFile.getContent().getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream( fos );
        try {
          driveService.files().export( driveFile.getId(), exportMapping.getMimeType() ).executeMediaAndDownloadTo( bos );
        } catch ( IOException e ) {
          // Throw this later, we want to close the output stream first
          savedException = new KettleException( BaseMessages.getString( PKG, "GoogleDriveExport.Error.ExportingFile" ), e );
        }
        try {
          bos.close();
        } catch ( IOException ignore ) {
          // Ignore
        }
        try {
          fos.close();
        } catch ( IOException ignore ) {
          // Ignore
        }
      } catch ( IOException e ) {
        savedException = new KettleException( BaseMessages.getString( PKG, "GoogleDriveExport.Error.ExportingFile" ), e );
      }
      if ( tempFile != null ) {
        try {
          targetFile.copyFrom( tempFile, Selectors.SELECT_SELF );
        } catch ( FileSystemException e ) {
          savedException = new KettleException( BaseMessages.getString( PKG, "GoogleDriveExport.Error.MovingFileFromTemp" ), e );
        }
      }
      if ( savedException != null ) {
        try {
          if ( targetFile.exists() ) {
            targetFile.delete();
          }
        } catch ( FileSystemException ignore ) {
          // Ignore, couldn't delete a bad output file
        }
        throw new KettleException( savedException );
      }
    }
  }

  @Override
  public String getXML() {
    StringBuilder retval = new StringBuilder();

    retval.append( super.getXML() );
    retval.append( "      " ).append( XMLHandler.addTagValue( "addFilesToResult", isAddFilesToResult() ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "createTargetFolder", isCreateTargetFolder() ) );
    retval.append( "      " ).append( XMLHandler.addTagValue( "targetFolder", getTargetFolder() ) );
    retval.append( "      " ).append( XMLHandler.openTag( "files" ) ).append( Const.CR );
    for ( int i = 0; i < fileSelections.length; i++ ) {
      retval.append( "        " ).append( XMLHandler.openTag( "file" ) ).append( Const.CR );
      retval.append( "          " ).append(
        XMLHandler.addTagValue( "queryFileType", fileSelections[i].getFileType().name() ) );
      retval.append( "          " ).append(
        XMLHandler.addTagValue( "exportFileType", fileSelections[i].getExportFormat().name() ) );
      retval.append( "          " ).append(
        XMLHandler.addTagValue( "customQuery", fileSelections[i].getQueryOptions() ) );
      retval.append( "          " ).append(
        XMLHandler.addTagValue( "removeSourceFiles", fileSelections[i].isDeleteSourceFile() ) );
      retval.append( "        " ).append( XMLHandler.closeTag( "file" ) ).append( Const.CR );
    }
    retval.append( "      " ).append( XMLHandler.closeTag( "files" ) ).append( Const.CR );

    return retval.toString();
  }

  @Override
  public void loadXML( Node entrynode, List<DatabaseMeta> databases, List<SlaveServer> slaveServers, Repository rep, IMetaStore metaStore ) throws KettleXMLException {
    try {
      super.loadXML( entrynode, databases, slaveServers, rep, metaStore );
      setAddFilesToResult( "Y".equalsIgnoreCase( XMLHandler.getTagValue( entrynode, "addFilesToResult" ) ) );
      setCreateTargetFolder( "Y".equalsIgnoreCase( XMLHandler.getTagValue( entrynode, "createTargetFolder" ) ) );
      setTargetFolder( XMLHandler.getTagValue( entrynode, "targetFolder" ) );
      Node filesNode = XMLHandler.getSubNode( entrynode, "files" );
      if ( filesNode != null ) {
        int fileCount = XMLHandler.countNodes( filesNode, "file" );
        fileSelections = new GoogleDriveExportFileSelection[fileCount];

        for ( int i = 0; i < fileCount; i++ ) {
          Node fileNode = XMLHandler.getSubNodeByNr( filesNode, "file", i );
          GoogleDriveFileType fileType =
            Enum.valueOf( GoogleDriveFileType.class, XMLHandler.getTagValue( fileNode, "queryFileType" ) );
          GoogleDriveExportFormat exportFormat =
            Enum.valueOf( GoogleDriveExportFormat.class, XMLHandler.getTagValue( fileNode, "exportFileType" ) );
          String queryOptions = XMLHandler.getTagValue( fileNode, "customQuery" );
          boolean deleteSource = "Y".equalsIgnoreCase( XMLHandler.getTagValue( fileNode, "removeSourceFiles" ) );

          fileSelections[i] = new GoogleDriveExportFileSelection( fileType, exportFormat, queryOptions, deleteSource );
        }
      }
    } catch ( Exception e ) {
      throw new KettleXMLException( BaseMessages.getString( PKG, "Demo.Error.UnableToLoadFromXML" ), e );
    }
  }

  @Override
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_job ) throws KettleException {
    super.saveRep( rep, metaStore, id_job );
    try {
      rep.saveJobEntryAttribute( id_job, getObjectId(), "addFilesToResult", isAddFilesToResult() );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "createTargetFolder", isCreateTargetFolder() );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "targetFolder", getTargetFolder() );
      rep.saveJobEntryAttribute( id_job, getObjectId(), "fileSelectionCount", fileSelections != null ? fileSelections.length : 0 );
      if ( fileSelections != null ) {
        for ( int i = 0; i < fileSelections.length; i++ ) {
          rep.saveJobEntryAttribute( id_job, getObjectId(), i, "queryFileType", fileSelections[i].getFileType().name() );
          rep.saveJobEntryAttribute( id_job, getObjectId(), i, "exportFileType", fileSelections[i].getExportFormat().name() );
          rep.saveJobEntryAttribute( id_job, getObjectId(), i, "customQuery", fileSelections[i].getQueryOptions() );
          rep.saveJobEntryAttribute( id_job, getObjectId(), i, "removeSourceFiles", fileSelections[i].isDeleteSourceFile() );
        }
      }
    } catch ( KettleDatabaseException dbe ) {
      throw new KettleException( BaseMessages.getString( PKG, "Demo.Error.UnableToSaveToRepository" ) + id_job, dbe );
    }
  }

  @Override
  public void loadRep( Repository rep, IMetaStore metaStore, ObjectId id_jobentry, List<DatabaseMeta> databases, List<SlaveServer> slaveServers ) throws KettleException {
    super.loadRep( rep, metaStore, id_jobentry, databases, slaveServers );
    try {
      setAddFilesToResult( rep.getJobEntryAttributeBoolean( id_jobentry, "addFilesToResult" ) );
      setCreateTargetFolder( rep.getJobEntryAttributeBoolean( id_jobentry, "createTargetFolder" ) );
      setTargetFolder( rep.getJobEntryAttributeString( id_jobentry, "targetFolder" ) );
      long fileCount = rep.getJobEntryAttributeInteger( id_jobentry, "fileSelectionCount" );
      fileSelections = new GoogleDriveExportFileSelection[(int) fileCount];
      for ( int i = 0; i < fileCount; i++ ) {
        GoogleDriveFileType fileType =
          Enum.valueOf( GoogleDriveFileType.class, rep.getJobEntryAttributeString( id_jobentry, i, "queryFileType" ) );
        GoogleDriveExportFormat exportFormat =
          Enum.valueOf( GoogleDriveExportFormat.class, rep.getJobEntryAttributeString( id_jobentry, i, "exportFileType" ) );
        String queryOptions = rep.getJobEntryAttributeString( id_jobentry, i, "customQuery" );
        boolean deleteSource = rep.getJobEntryAttributeBoolean( id_jobentry, i, "removeSourceFiles" );
        fileSelections[i] = new GoogleDriveExportFileSelection( fileType, exportFormat, queryOptions, deleteSource );
      }
    } catch ( KettleDatabaseException dbe ) {
      throw new KettleException( BaseMessages.getString( PKG, "Demo.Error.UnableToLoadFromRepository" ) + id_jobentry, dbe );
    }
  }

  @Override
  public String getDialogClassName() {
    return JobEntryGoogleDriveExportDialog.class.getName();
  }

  @Override
  public boolean evaluates() {
    return true;
  }

  @Override
  public boolean isUnconditional() {
    return false;
  }

  protected void checkFolderExists( FileObject folder, boolean createIfNot ) throws KettleException {
    if ( folder == null ) {
      return;
    }
    try {
      if ( !folder.exists() && createIfNot ) {
        if ( log.isDetailed() ) {
          log.logDetailed( BaseMessages.getString( PKG, "GoogleDriveExport.Log.CreatingTargetFolder" ) );
        }
        folder.createFolder();
        return;
      } else {
        if ( !folder.exists() ) {
          throw new KettleException( BaseMessages.getString( PKG, "GoogleDriveExport.Error.FolderNotExist", folder.getName() ) );
        } else if ( !folder.isFolder() ) {
          throw new KettleException( BaseMessages.getString( PKG, "GoogleDriveExport.Error.NotAFolder", folder.getName() ) );
        }
      }
    } catch ( FileSystemException e ) {
      throw new KettleException( e );
    }
  }

  public String getTargetFolder() {
    return targetFolder;
  }

  public void setTargetFolder( String targetFolder ) {
    this.targetFolder = targetFolder;
  }

  public boolean isCreateTargetFolder() {
    return createTargetFolder;
  }

  public void setCreateTargetFolder( boolean createTargetFolder ) {
    this.createTargetFolder = createTargetFolder;
  }

  public GoogleDriveExportFileSelection[] getFileSelections() {
    return fileSelections;
  }

  public void setFileSelections( GoogleDriveExportFileSelection[] fileSelections ) {
    this.fileSelections = fileSelections;
  }

  public boolean isAddFilesToResult() {
    return addFilesToResult;
  }

  public void setAddFilesToResult( boolean addFilesToResult ) {
    this.addFilesToResult = addFilesToResult;
  }
}
