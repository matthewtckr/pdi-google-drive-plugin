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
import java.util.Collections;
import java.util.List;

public enum GoogleDriveExportFormat {
  WORD( "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ),
  ODT( "odt", "application/vnd.oasis.opendocument.text" ),
  PDF( "pdf", "application/pdf" ),
  RICHTEXT( "rtf", "application/rtf" ),
  TEXT( "txt", "text/plain" ),
  HTML( "html", "text/html" ),
  EXCEL( "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ),
  ODS( "ods", "application/x-vnd.oasis.opendocument.spreadsheet" ),
  CSV( "csv", "text/csv" ),
  POWERPOINT( "pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation" ),
  //JSON( "json", "application/vnd.google-apps.script+json" ),
  JPEG( "jpg", "image/jpeg" ),
  PNG( "png", "image/png" ),
  SVG( "svg", "image/svg+xml" );

  private String mimeType;
  private String fileExtension;

  private GoogleDriveExportFormat( String fileExtension, String mimeType ) {
    this.fileExtension = fileExtension;
    this.mimeType = mimeType;
  }

  public String getMimeType() {
    return mimeType;
  }

  public String getFileExtension() {
    return fileExtension;
  }

  public static List<String> getAllDescriptions() {
    GoogleDriveExportFormat[] values = GoogleDriveExportFormat.values();
    List<String> descriptions = new ArrayList<>();
    for ( int i = 0; i < values.length; i++ ) {
      descriptions.add( values[i].getFileExtension() );
    }
    Collections.sort( descriptions );
    return descriptions;
  }

  public static GoogleDriveExportFormat findByFileExtension( String fileExtension ) {
    GoogleDriveExportFormat[] values = GoogleDriveExportFormat.values();
    if ( fileExtension != null ) {
      for ( int i = 0; i < values.length; i++ ) {
        if ( fileExtension.equalsIgnoreCase( values[i].getFileExtension() ) ) {
          return values[i];
        }
      }
    }
    return null;
  }
}
