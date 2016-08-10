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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

public class GoogleDriveExportFileSelectionTest {

  @Test
  public void test() {
    GoogleDriveExportFileSelection object =
      new GoogleDriveExportFileSelection( GoogleDriveFileType.DOCUMENT, GoogleDriveExportFormat.WORD, null, false );
    assertEquals( GoogleDriveFileType.DOCUMENT, object.getFileType() );
    assertEquals( GoogleDriveExportFormat.WORD, object.getExportFormat() );
    assertNull( object.getQueryOptions() );
    assertEquals( "mimeType = '" + GoogleDriveFileType.DOCUMENT.getMimeType() + "'", object.getFinalQueryOptions() );
    assertFalse( object.isDeleteSourceFile() );

    object.setFileType( GoogleDriveFileType.SPREADSHEET );
    object.setExportFormat( GoogleDriveExportFormat.EXCEL );
    object.setDeleteSourceFile( true );
    String extraOption = UUID.randomUUID().toString();
    object.setQueryOptions( extraOption );
    assertEquals( GoogleDriveFileType.SPREADSHEET, object.getFileType() );
    assertEquals( GoogleDriveExportFormat.EXCEL, object.getExportFormat() );
    assertEquals( extraOption, object.getQueryOptions() );
    assertEquals( "mimeType = '" + GoogleDriveFileType.SPREADSHEET.getMimeType() + "' and ( " + extraOption + " )",
      object.getFinalQueryOptions() );
    assertTrue( object.isDeleteSourceFile() );
  }
}
