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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.pentaho.di.i18n.BaseMessages;

public enum GoogleDriveFileType {
  /*AUDIO( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.Audio.Desc" ),
    "application/vnd.google-apps.audio" ),*/
  DOCUMENT( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.Document.Desc" ),
    "application/vnd.google-apps.document",
    GoogleDriveExportFormat.HTML, GoogleDriveExportFormat.TEXT,
    GoogleDriveExportFormat.RICHTEXT, GoogleDriveExportFormat.ODT,
    GoogleDriveExportFormat.PDF, GoogleDriveExportFormat.WORD ),
  DRAWING( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.Drawing.Desc" ),
    "application/vnd.google-apps.drawing",
    GoogleDriveExportFormat.JPEG, GoogleDriveExportFormat.PNG,
    GoogleDriveExportFormat.SVG, GoogleDriveExportFormat.PDF ),
  /*FILE( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.File.Desc" ),
    "application/vnd.google-apps.file" ),
  FOLDER( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.Folder.Desc" ),
    "application/vnd.google-apps.folder" ),
  FORM( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.Form.Desc" ),
    "application/vnd.google-apps.form" ),
  FUSIONTABLE( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.FusionTable.Desc" ),
    "application/vnd.google-apps.fusiontable" ),
  MAP( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.Map.Desc" ),
    "application/vnd.google-apps.map" ),
  PHOTO( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.Photo.Desc" ),
    "application/vnd.google-apps.photo" ),*/
  PRESENTATION( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.Presentation.Desc" ),
    "application/vnd.google-apps.presentation",
    GoogleDriveExportFormat.POWERPOINT, GoogleDriveExportFormat.PDF,
    GoogleDriveExportFormat.TEXT ),
  /*APPSCRIPT( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.AppScript.Desc" ),
    "application/vnd.google-apps.script",
    GoogleDriveExportFormat.JSON ),
  SITES( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.Sites.Desc" ),
    "application/vnd.google-apps.sites" ),*/
  SPREADSHEET( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.Spreadsheet.Desc" ),
    "application/vnd.google-apps.spreadsheet",
    GoogleDriveExportFormat.EXCEL, GoogleDriveExportFormat.ODS,
    GoogleDriveExportFormat.PDF, GoogleDriveExportFormat.CSV );
  /*UNKNOWN( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.Unknown.Desc" ),
    "application/vnd.google-apps.unknown" ),
  VIDEO( BaseMessages.getString( GoogleDriveFileType.class, "GoogleDriveFileType.Video.Desc" ),
    "application/vnd.google-apps.video" );*/

  private String description;
  private String mimeType;
  private List<GoogleDriveExportFormat> exportFormats;

  private GoogleDriveFileType( String description, String mimeType ) {
    this( description, mimeType, (GoogleDriveExportFormat[]) null );
  }

  private GoogleDriveFileType( String description, String mimeType, GoogleDriveExportFormat... exportFormats ) {
    this.description = description;
    this.mimeType = mimeType;
    if ( exportFormats != null ) {
      this.exportFormats = Collections.unmodifiableList( Arrays.asList( exportFormats ) );
    }
  }

  public String getMimeType() {
    return mimeType;
  }

  public List<GoogleDriveExportFormat> getExportFormats() {
    return exportFormats;
  }

  public String getDescription() {
    return description;
  }

  public static List<String> getAllDescriptions() {
    GoogleDriveFileType[] values = GoogleDriveFileType.values();
    List<String> descriptions = new ArrayList<>();
    for ( int i = 0; i < values.length; i++ ) {
      descriptions.add( values[i].getDescription() );
    }
    Collections.sort( descriptions );
    return descriptions;
  }

  public static GoogleDriveFileType findByDescription( String description ) {
    GoogleDriveFileType[] values = GoogleDriveFileType.values();
    if ( description != null ) {
      for ( int i = 0; i < values.length; i++ ) {
        if ( description.equalsIgnoreCase( values[i].getDescription() ) ) {
          return values[i];
        }
      }
    }
    return null;
  }
}
