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

import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;

public class GoogleDriveExportFileSelection {

  private GoogleDriveFileType fileType = GoogleDriveFileType.SPREADSHEET;
  private GoogleDriveExportFormat exportFormat = GoogleDriveExportFormat.EXCEL;
  private String queryOptions;
  private boolean deleteSourceFile;

  public GoogleDriveExportFileSelection( GoogleDriveFileType fileType, GoogleDriveExportFormat exportFormat,
      String queryOptions, boolean deleteSourceFile ) {
    this.fileType = fileType;
    this.exportFormat = exportFormat;
    this.queryOptions = queryOptions;
    this.deleteSourceFile = deleteSourceFile;
  }

  public GoogleDriveFileType getFileType() {
    return fileType;
  }

  public void setFileType( GoogleDriveFileType fileType ) {
    this.fileType = fileType;
  }

  public GoogleDriveExportFormat getExportFormat() {
    return exportFormat;
  }

  public void setExportFormat( GoogleDriveExportFormat exportFormat ) {
    this.exportFormat = exportFormat;
  }

  public String getQueryOptions() {
    return queryOptions;
  }

  public void setQueryOptions( String queryOptions ) {
    this.queryOptions = queryOptions;
  }

  public boolean isDeleteSourceFile() {
    return deleteSourceFile;
  }

  public void setDeleteSourceFile( boolean deleteSourceFile ) {
    this.deleteSourceFile = deleteSourceFile;
  }

  String getFinalQueryOptions() {
    StringBuilder retval = new StringBuilder();
    retval.append( "mimeType = '" + getFileType().getMimeType() + "'" );
    if ( !Utils.isEmpty( Const.trim( getQueryOptions() ) ) ) {
      retval.append( " and ( " ).append( getQueryOptions() ).append( " )" );
    }
    return retval.toString();
  }
}
