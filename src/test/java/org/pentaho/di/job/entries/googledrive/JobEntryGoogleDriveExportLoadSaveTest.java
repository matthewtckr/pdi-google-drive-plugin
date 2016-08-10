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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.pentaho.di.job.entry.loadSave.JobEntryLoadSaveTestSupport;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.BooleanLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.EnumLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.StringLoadSaveValidator;

public class JobEntryGoogleDriveExportLoadSaveTest extends JobEntryLoadSaveTestSupport<JobEntryGoogleDriveExport> {

  @Override
  protected Class<JobEntryGoogleDriveExport> getJobEntryClass() {
    return JobEntryGoogleDriveExport.class;
  }

  @Override
  protected List<String> listCommonAttributes() {
    return Arrays.asList( "ApplicationName", "ServiceAccountId", "PrivateKeyFile",
      "AddFilesToResult", "CreateTargetFolder", "TargetFolder", "FileSelections" );
  }

  @Override
  protected Map<String, FieldLoadSaveValidator<?>> createAttributeValidatorsMap() {
    return toMap(
      "FileSelections", new ArrayLoadSaveValidator<GoogleDriveExportFileSelection>( new GoogleDriveGetFileSelectionValidator() ) );
  }

  public static class GoogleDriveGetFileSelectionValidator implements FieldLoadSaveValidator<GoogleDriveExportFileSelection> {

    EnumLoadSaveValidator<GoogleDriveFileType> fileTypeValidator = new EnumLoadSaveValidator<>( GoogleDriveFileType.class );
    EnumLoadSaveValidator<GoogleDriveExportFormat> exportValidator = new EnumLoadSaveValidator<>( GoogleDriveExportFormat.class );
    StringLoadSaveValidator queryOptionsValidator = new StringLoadSaveValidator();
    BooleanLoadSaveValidator deleteSourceValidator = new BooleanLoadSaveValidator();

    @Override
    public GoogleDriveExportFileSelection getTestObject() {
      return new GoogleDriveExportFileSelection(
        fileTypeValidator.getTestObject(),
        exportValidator.getTestObject(),
        queryOptionsValidator.getTestObject(),
        deleteSourceValidator.getTestObject() );
    }

    @Override
    public boolean validateTestObject( GoogleDriveExportFileSelection testObject, Object actual ) {
      if ( !( actual instanceof GoogleDriveExportFileSelection ) ) {
        return false;
      }
      GoogleDriveExportFileSelection real = (GoogleDriveExportFileSelection) actual;

      if ( testObject.getFileType() == null || real.getFileType() == null ) {
        if ( testObject.getFileType() != null || real.getFileType() != null ) {
          return false;
        }
      } else {
        if ( !fileTypeValidator.validateTestObject( testObject.getFileType(), real.getFileType() ) ) {
          return false;
        }
      }
      if ( testObject.getExportFormat() == null || real.getExportFormat() == null ) {
        if ( testObject.getExportFormat() != null || real.getExportFormat() != null ) {
          return false;
        }
      } else {
        if ( !exportValidator.validateTestObject( testObject.getExportFormat(), real.getExportFormat() ) ) {
          return false;
        }
      }
      if ( testObject.getQueryOptions() == null || real.getQueryOptions() == null ) {
        if ( testObject.getQueryOptions() != null || real.getQueryOptions() != null ) {
          return false;
        }
      } else {
        if ( !queryOptionsValidator.validateTestObject( testObject.getQueryOptions(), real.getQueryOptions() ) ) {
          return false;
        }
      }
      return deleteSourceValidator.validateTestObject( testObject.isDeleteSourceFile(), real.isDeleteSourceFile() );
    }

  }
}
