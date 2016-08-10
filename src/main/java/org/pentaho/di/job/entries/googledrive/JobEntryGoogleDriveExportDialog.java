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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryDialogInterface;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.LabelTextVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.job.dialog.JobDialog;
import org.pentaho.di.ui.job.entry.JobEntryDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class JobEntryGoogleDriveExportDialog extends JobEntryDialog implements JobEntryDialogInterface {

  private static final Class<?> PKG = JobEntryAbstractGoogleDrive.class;

  private static final String[] YES_NO_COMBO = new String[] {
    BaseMessages.getString( PKG, "System.Combo.No" ), BaseMessages.getString( PKG, "System.Combo.Yes" ), };

  private JobEntryGoogleDriveExport jobEntry;
  private boolean changed;

  private Text wName;
  private LabelTextVar wApplicationName;
  private LabelTextVar wServiceAccountId;
  private LabelTextVar wPrivateKeyFile;
  private LabelTextVar wTargetDirectory;
  private Button wCreateTargetDirectory;
  private Button wAddFilesToResult;
  private TableView wFilenameList;
  private Button wOK, wCancel;

  public JobEntryGoogleDriveExportDialog( Shell parent, JobEntryInterface jobEntry, Repository rep, JobMeta jobMeta ) {
    super( parent, jobEntry, rep, jobMeta );
    this.jobEntry = (JobEntryGoogleDriveExport) jobEntry;
    if ( this.jobEntry.getName() == null ) {
      this.jobEntry.setName( BaseMessages.getString( PKG, "GoogleDriveExport.Name" ) );
    }
  }

  @Override
  public JobEntryInterface open() {
    // SWT code for setting up the dialog
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, props.getJobsDialogStyle() );
    props.setLook( shell );
    JobDialog.setShellImage( shell, jobEntry );

    // save the job entry's changed flag
    changed = jobEntry.hasChanged();

    // The ModifyListener used on all controls. It will update the meta object to
    // indicate that changes are being made.
    ModifyListener lsMod = new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent e ) {
        jobEntry.setChanged();
      }
    };
    SelectionListener lsSel = new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent e ) {
        jobEntry.setChanged();
      }
    };

    // ------------------------------------------------------- //
    // SWT code for building the actual settings dialog      //
    // ------------------------------------------------------- //
    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "GoogleDriveExport.Name" ) );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // Job entry name line
    Label wlName = new Label( shell, SWT.RIGHT );
    wlName.setText( BaseMessages.getString( PKG, "GoogleDriveExportDialog.JobEntryName.Label" ) );
    props.setLook( wlName );
    FormData fdlName = new FormData();
    fdlName.left = new FormAttachment( 0, 0 );
    fdlName.right = new FormAttachment( middle, -margin );
    fdlName.top = new FormAttachment( 0, margin );
    wlName.setLayoutData( fdlName );
    wName = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    props.setLook( wName );
    wName.addModifyListener( lsMod );
    FormData fdName = new FormData();
    fdName.left = new FormAttachment( middle, 0 );
    fdName.top = new FormAttachment( 0, margin );
    fdName.right = new FormAttachment( 100, 0 );
    wName.setLayoutData( fdName );

    // Service Account ID
    wApplicationName =
      new LabelTextVar( jobMeta, shell,
        BaseMessages.getString( PKG, "GoogleDriveExportDialog.ApplicationName.Label" ),
        BaseMessages.getString( PKG, "GoogleDriveExportDialog.ApplicationName.Tooltip" ) );
    props.setLook( wApplicationName );
    wApplicationName.addModifyListener( lsMod );
    FormData fdApplicationName = new FormData();
    fdApplicationName.left = new FormAttachment( 0, 0 );
    fdApplicationName.top = new FormAttachment( wName, margin );
    fdApplicationName.right = new FormAttachment( 100, 0 );
    wApplicationName.setLayoutData( fdApplicationName );

    // Service Account ID
    wServiceAccountId =
      new LabelTextVar( jobMeta, shell,
        BaseMessages.getString( PKG, "GoogleDriveExportDialog.ServiceAccountId.Label" ),
        BaseMessages.getString( PKG, "GoogleDriveExportDialog.ServiceAccountId.Tooltip" ) );
    props.setLook( wServiceAccountId );
    wServiceAccountId.addModifyListener( lsMod );
    FormData fdServiceAccountId = new FormData();
    fdServiceAccountId.left = new FormAttachment( 0, 0 );
    fdServiceAccountId.top = new FormAttachment( wApplicationName, margin );
    fdServiceAccountId.right = new FormAttachment( 100, 0 );
    wServiceAccountId.setLayoutData( fdServiceAccountId );

    // Service Account ID
    wPrivateKeyFile =
      new LabelTextVar( jobMeta, shell,
        BaseMessages.getString( PKG, "GoogleDriveExportDialog.PrivateKeyFile.Label" ),
        BaseMessages.getString( PKG, "GoogleDriveExportDialog.PrivateKeyFile.Tooltip" ) );
    props.setLook( wPrivateKeyFile );
    wPrivateKeyFile.addModifyListener( lsMod );
    FormData fdPrivateKeyFile = new FormData();
    fdPrivateKeyFile.left = new FormAttachment( 0, 0 );
    fdPrivateKeyFile.top = new FormAttachment( wServiceAccountId, margin );
    fdPrivateKeyFile.right = new FormAttachment( 100, 0 );
    wPrivateKeyFile.setLayoutData( fdPrivateKeyFile );

    // Target Directory
    wTargetDirectory =
      new LabelTextVar( jobMeta, shell,
        BaseMessages.getString( PKG, "GoogleDriveExportDialog.TargetDirectory.Label" ),
        BaseMessages.getString( PKG, "GoogleDriveExportDialog.TargetDirectory.Tooltip" ) );
    props.setLook( wServiceAccountId );
    wTargetDirectory.addModifyListener( lsMod );
    FormData fdTargetDirectory = new FormData();
    fdTargetDirectory.left = new FormAttachment( 0, 0 );
    fdTargetDirectory.top = new FormAttachment( wPrivateKeyFile, margin );
    fdTargetDirectory.right = new FormAttachment( 100, 0 );
    wTargetDirectory.setLayoutData( fdTargetDirectory );

    // Create Target Directory
    Label wlCreateTargetDirectory = new Label( shell, SWT.RIGHT );
    wlCreateTargetDirectory.setText( BaseMessages.getString( PKG, "GoogleDriveExportDialog.CreateTargetDirectory.Label" ) );
    wlCreateTargetDirectory.setToolTipText( BaseMessages.getString( PKG, "GoogleDriveExportDialog.CreateTargetDirectory.Tooltip" ) );
    props.setLook( wlCreateTargetDirectory );
    FormData fdlCreateTargetDirectory = new FormData();
    fdlCreateTargetDirectory.left = new FormAttachment( 0, 0 );
    fdlCreateTargetDirectory.top = new FormAttachment( wTargetDirectory, margin );
    fdlCreateTargetDirectory.right = new FormAttachment( middle, -margin );
    wlCreateTargetDirectory.setLayoutData( fdlCreateTargetDirectory );
    wCreateTargetDirectory = new Button( shell, SWT.CHECK );
    props.setLook( wCreateTargetDirectory );
    FormData fdCreateTargetDirectory = new FormData();
    fdCreateTargetDirectory.left = new FormAttachment( middle, 0 );
    fdCreateTargetDirectory.top = new FormAttachment( wTargetDirectory, margin );
    fdCreateTargetDirectory.right = new FormAttachment( 100, 0 );
    wCreateTargetDirectory.setLayoutData( fdCreateTargetDirectory );
    wCreateTargetDirectory.addSelectionListener( lsSel );

    // Add Files to Result
    Label wlAddFilesToResult = new Label( shell, SWT.RIGHT );
    wlAddFilesToResult.setText( BaseMessages.getString( PKG, "GoogleDriveExportDialog.AddFilesToResult.Label" ) );
    wlAddFilesToResult.setToolTipText( BaseMessages.getString( PKG, "GoogleDriveExportDialog.AddFilesToResult.Tooltip" ) );
    props.setLook( wlAddFilesToResult );
    FormData fdlAddFilesToResult = new FormData();
    fdlAddFilesToResult.left = new FormAttachment( 0, 0 );
    fdlAddFilesToResult.top = new FormAttachment( wCreateTargetDirectory, margin );
    fdlAddFilesToResult.right = new FormAttachment( middle, -margin );
    wlAddFilesToResult.setLayoutData( fdlAddFilesToResult );
    wAddFilesToResult = new Button( shell, SWT.CHECK );
    props.setLook( wAddFilesToResult );
    FormData fdAddFilesToResult = new FormData();
    fdAddFilesToResult.left = new FormAttachment( middle, 0 );
    fdAddFilesToResult.top = new FormAttachment( wCreateTargetDirectory, margin );
    fdCreateTargetDirectory.right = new FormAttachment( 100, 0 );
    wAddFilesToResult.setLayoutData( fdAddFilesToResult );
    wAddFilesToResult.addSelectionListener( lsSel );


    // Filename List
    ColumnInfo[] colinfo = new ColumnInfo[4];
    colinfo[0] = new ColumnInfo( BaseMessages.getString( PKG, "GoogleDriveExportDialog.Column.FileType" ),
      ColumnInfo.COLUMN_TYPE_CCOMBO, GoogleDriveFileType.getAllDescriptions().toArray( new String[0] ), false );
    colinfo[1] = new ColumnInfo( BaseMessages.getString( PKG, "GoogleDriveExportDialog.Column.ExportType" ),
      ColumnInfo.COLUMN_TYPE_CCOMBO, GoogleDriveExportFormat.getAllDescriptions().toArray( new String[0] ), false );
    colinfo[2] = new ColumnInfo( BaseMessages.getString( PKG, "GoogleDriveExportDialog.Column.Query" ),
      ColumnInfo.COLUMN_TYPE_TEXT, false );
    colinfo[3] = new ColumnInfo( BaseMessages.getString( PKG, "GoogleDriveExportDialog.Column.DeleteSource" ),
      ColumnInfo.COLUMN_TYPE_CCOMBO, YES_NO_COMBO, false );

    int rows = jobEntry.getFileSelections() == null ? 1 : jobEntry.getFileSelections().length;
    wFilenameList =
      new TableView( jobMeta, shell, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER, colinfo, rows, lsMod, props );
    props.setLook( wFilenameList );
    FormData fdFilenameList = new FormData();
    fdFilenameList.left = new FormAttachment( 0, 0 );
    fdFilenameList.right = new FormAttachment( 100, -margin );
    fdFilenameList.top = new FormAttachment( wAddFilesToResult, margin );
    wFilenameList.setLayoutData( fdFilenameList );

    // Add listeners
    Listener lsCancel = new Listener() {
      @Override
      public void handleEvent( Event e ) {
        cancel();
      }
    };
    Listener lsOK = new Listener() {
      @Override
      public void handleEvent( Event e ) {
        ok();
      }
    };

    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    BaseStepDialog.positionBottomButtons( shell, new Button[] { wOK, wCancel }, margin, wFilenameList );

    wCancel.addListener( SWT.Selection, lsCancel );
    wOK.addListener( SWT.Selection, lsOK );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      @Override
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    getData();
    BaseStepDialog.setSize( shell );
    shell.open();
    props.setDialogSize( shell, "JobEntryGoogleDriveExport" );
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return jobEntry;
  }

  public void dispose() {
    WindowProperty winprop = new WindowProperty( shell );
    props.setScreen( winprop );
    shell.dispose();
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    wName.setText( Const.NVL( jobEntry.getName(), "" ) );
    wApplicationName.setText( Const.NVL( jobEntry.getApplicationName(), "" ) );
    wServiceAccountId.setText( Const.NVL( jobEntry.getServiceAccountId(), "" ) );
    wPrivateKeyFile.setText( Const.NVL( jobEntry.getPrivateKeyFile(), "" ) );
    wTargetDirectory.setText( Const.NVL( jobEntry.getTargetFolder(), "" ) );
    wCreateTargetDirectory.setSelection( jobEntry.isCreateTargetFolder() );
    wAddFilesToResult.setSelection( jobEntry.isAddFilesToResult() );
    if ( jobEntry.getFileSelections() != null ) {
      for ( int i = 0; i < jobEntry.getFileSelections().length; i++ ) {
        TableItem item = wFilenameList.getTable().getItem( i );
        item.setText( 1, Const.NVL( jobEntry.getFileSelections()[i].getFileType().getDescription(), "" ) );
        item.setText( 2, Const.NVL( jobEntry.getFileSelections()[i].getExportFormat().getFileExtension(), "" ) );
        item.setText( 3, Const.NVL( jobEntry.getFileSelections()[i].getQueryOptions(), "" ) );
        item.setText( 4, jobEntry.getFileSelections()[i].isDeleteSourceFile() ? YES_NO_COMBO[1] : YES_NO_COMBO[0] );
      }
    }
  }

  private void cancel() {
    jobEntry.setChanged( changed );
    jobEntry = null;
    dispose();
  }

  /**
   * Update jobentry from the dialog values
   */
  protected void ok() {
    if ( Const.isEmpty( wName.getText() ) ) {
      MessageBox mb = new MessageBox( shell, SWT.OK | SWT.ICON_ERROR );
      mb.setText( BaseMessages.getString( PKG, "System.StepJobEntryNameMissing.Title" ) );
      mb.setMessage( BaseMessages.getString( PKG, "System.JobEntryNameMissing.Msg" ) );
      mb.open();
      return;
    }

    jobEntry.setName( wName.getText() );
    jobEntry.setApplicationName( wApplicationName.getText() );
    jobEntry.setServiceAccountId( wServiceAccountId.getText() );
    jobEntry.setPrivateKeyFile( wPrivateKeyFile.getText() );
    jobEntry.setTargetFolder( wTargetDirectory.getText() );
    jobEntry.setCreateTargetFolder( wCreateTargetDirectory.getSelection() );
    jobEntry.setAddFilesToResult( wAddFilesToResult.getSelection() );
    int rows = wFilenameList.nrNonEmpty();
    GoogleDriveExportFileSelection[] fileSelections = new GoogleDriveExportFileSelection[rows];
    for ( int i = 0; i < rows; i++ ) {
      TableItem row = wFilenameList.getNonEmpty( i );
      GoogleDriveFileType fileType = GoogleDriveFileType.findByDescription( row.getText( 1 ) );
      GoogleDriveExportFormat exportFormat = GoogleDriveExportFormat.findByFileExtension( row.getText( 2 ) );
      String customQuery = Const.trim( row.getText( 3 ) );
      boolean deleteSourceFile = YES_NO_COMBO[1].equalsIgnoreCase( row.getText( 4 ) );
      GoogleDriveExportFileSelection entry =
        new GoogleDriveExportFileSelection( fileType, exportFormat, customQuery, deleteSourceFile );
      fileSelections[i] = entry;
    }
    jobEntry.setFileSelections( fileSelections );
    dispose();
  }
}
