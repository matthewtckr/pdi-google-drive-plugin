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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class GoogleDriveFileTypeTest {

  @Test
  public void testGetAllDescriptions() {
    List<String> descriptions = GoogleDriveFileType.getAllDescriptions();
    assertNotNull( descriptions );
    assertEquals( 4, descriptions.size() );
    assertTrue( descriptions.contains( GoogleDriveFileType.DOCUMENT.getDescription() ) );
    assertTrue( descriptions.contains( GoogleDriveFileType.DRAWING.getDescription() ) );
    assertTrue( descriptions.contains( GoogleDriveFileType.PRESENTATION.getDescription() ) );
    assertTrue( descriptions.contains( GoogleDriveFileType.SPREADSHEET.getDescription() ) );

    // Confirm that returned list is already sorted
    List<String> sortedDescriptions = new ArrayList<>( descriptions );
    Collections.sort( sortedDescriptions );
    assertEquals( sortedDescriptions, descriptions );
  }

  @Test
  public void testFindByDescription() {
    assertNull( GoogleDriveFileType.findByDescription( null ) );
    assertNull( GoogleDriveFileType.findByDescription( "" ) );
    assertNull( GoogleDriveFileType.findByDescription( "notarealfiletype" ) );
    assertEquals( GoogleDriveFileType.DOCUMENT,
      GoogleDriveFileType.findByDescription( GoogleDriveFileType.DOCUMENT.getDescription() ) );
  }

  @Test
  public void testGetExportFormats() {
    assertNotNull( GoogleDriveFileType.DOCUMENT.getExportFormats() );
    assertEquals( 6, GoogleDriveFileType.DOCUMENT.getExportFormats().size() );
    assertTrue( GoogleDriveFileType.DOCUMENT.getExportFormats().contains( GoogleDriveExportFormat.WORD ) );
    assertTrue( GoogleDriveFileType.DOCUMENT.getExportFormats().contains( GoogleDriveExportFormat.ODT ) );
    assertTrue( GoogleDriveFileType.DOCUMENT.getExportFormats().contains( GoogleDriveExportFormat.RICHTEXT ) );
    assertTrue( GoogleDriveFileType.DOCUMENT.getExportFormats().contains( GoogleDriveExportFormat.TEXT ) );
    assertTrue( GoogleDriveFileType.DOCUMENT.getExportFormats().contains( GoogleDriveExportFormat.HTML ) );
    assertTrue( GoogleDriveFileType.DOCUMENT.getExportFormats().contains( GoogleDriveExportFormat.PDF ) );

    assertNotNull( GoogleDriveFileType.DRAWING.getExportFormats() );
    assertEquals( 4, GoogleDriveFileType.DRAWING.getExportFormats().size() );
    assertTrue( GoogleDriveFileType.DRAWING.getExportFormats().contains( GoogleDriveExportFormat.SVG ) );
    assertTrue( GoogleDriveFileType.DRAWING.getExportFormats().contains( GoogleDriveExportFormat.PNG ) );
    assertTrue( GoogleDriveFileType.DRAWING.getExportFormats().contains( GoogleDriveExportFormat.JPEG ) );
    assertTrue( GoogleDriveFileType.DRAWING.getExportFormats().contains( GoogleDriveExportFormat.PDF ) );

    assertNotNull( GoogleDriveFileType.PRESENTATION.getExportFormats() );
    assertEquals( 3, GoogleDriveFileType.PRESENTATION.getExportFormats().size() );
    assertTrue( GoogleDriveFileType.PRESENTATION.getExportFormats().contains( GoogleDriveExportFormat.POWERPOINT ) );
    assertTrue( GoogleDriveFileType.PRESENTATION.getExportFormats().contains( GoogleDriveExportFormat.PDF ) );
    assertTrue( GoogleDriveFileType.PRESENTATION.getExportFormats().contains( GoogleDriveExportFormat.TEXT ) );

    assertNotNull( GoogleDriveFileType.SPREADSHEET.getExportFormats() );
    assertEquals( 4, GoogleDriveFileType.SPREADSHEET.getExportFormats().size() );
    assertTrue( GoogleDriveFileType.SPREADSHEET.getExportFormats().contains( GoogleDriveExportFormat.EXCEL ) );
    assertTrue( GoogleDriveFileType.SPREADSHEET.getExportFormats().contains( GoogleDriveExportFormat.ODS ) );
    assertTrue( GoogleDriveFileType.SPREADSHEET.getExportFormats().contains( GoogleDriveExportFormat.PDF ) );
    assertTrue( GoogleDriveFileType.SPREADSHEET.getExportFormats().contains( GoogleDriveExportFormat.CSV ) );
  }
}
