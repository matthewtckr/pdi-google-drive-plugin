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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class GoogleDriveExportFormatTest {

  @Test
  public void testGetAllDescriptions() {
    List<String> descriptions = GoogleDriveExportFormat.getAllDescriptions();
    assertNotNull( descriptions );
    assertEquals( 13, descriptions.size() );

    // Confirm that returned list is already sorted
    List<String> sortedDescriptions = new ArrayList<>( descriptions );
    Collections.sort( sortedDescriptions );
    assertEquals( sortedDescriptions, descriptions );
  }

  @Test
  public void testGetByDescription() {
    assertNull( GoogleDriveExportFormat.findByFileExtension( null ) );
    assertNull( GoogleDriveExportFormat.findByFileExtension( "" ) );
    assertNull( GoogleDriveExportFormat.findByFileExtension( "fakeextension" ) );
    assertEquals( GoogleDriveExportFormat.EXCEL, GoogleDriveExportFormat.findByFileExtension( "xlsx" ) );
  }
}
