import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import EmberObject from '@ember/object';

module('Integration | Component | endpoint/file-actionbar', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders', async function(assert) {
    this.set('itemList', []);
    await render(hbs`{{endpoint/file-actionbar itemList=itemList}}`);
    assert.equal(findAll('.file-actionbar').length, 1, 'file-actionbar component has rendered.');
    assert.equal(findAll('.file-actionbar .rsa-form-button').length, 4, 'five buttons have been rendered.');
  });

  test('presence of priority buttons', async function(assert) {
    this.set('itemList', []);
    await render(hbs`{{endpoint/file-actionbar itemList=itemList}}`);
    assert.equal(findAll('.file-actionbar .file-status-button')[0].textContent.trim(), 'Change File Status', 'Edit file status button is present.');
    assert.equal(findAll('.file-actionbar .event-analysis ')[0].textContent.trim(), 'Analyze Events', 'Analyze Events button is present.');
  });

  test('when noFiles selected', async function(assert) {
    this.set('itemList', []);
    await render(hbs`{{endpoint/file-actionbar itemList=itemList selectedFileCount=0}}`);
    assert.equal(findAll('.file-actionbar .file-status-button')[0].classList.contains('is-disabled'), true, 'Edit file status Button is disabled when no files are selected');
    assert.equal(findAll('.file-actionbar .event-analysis')[0].classList.contains('is-disabled'), true, 'Pivot-to-investigate Button is disabled when no files are selected');
  });

  test('Buttons enabling/disabling for multiple files selection', async function(assert) {
    this.set('itemList', [{ machineOSType: 'windows' }, { machineOSType: 'windows' }]);
    await render(hbs`{{endpoint/file-actionbar itemList=itemList}}`);
    assert.equal(findAll('.file-actionbar .file-status-button')[0].classList.contains('is-disabled'), false, 'Edit file status Button is enabled when multiple files are selected.');
    assert.equal(findAll('.file-actionbar .event-analysis')[0].classList.contains('is-disabled'), true, 'Pivot-to-investigate Button is disabled when multiple files are selected.');
    assert.equal(findAll('.file-actionbar .event-analysis')[0].title, 'Select a single file to analyze.', 'Pivot-to-investigate Button is disabled tooltip should be Select a single file to analyze.');

  });

  test('More action external lookup for google', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.endpointCanManageFiles', true);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });
    await render(hbs`
      <div id='modalDestination'></div>
      {{endpoint/file-actionbar
        itemList=itemList
        showIcons=false
        selectedFileCount=2
        accessControl=accessControl
        showResetRiskScore=true
        fileDownloadButtonStatus=fileDownloadButtonStatus
      }}
    `);
    assert.equal(findAll('.more-action-button')[0].classList.contains('is-disabled'), false, 'More action button should enable.');
    await click('.more-action-button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 6, 'All the list options should render.');
    await triggerEvent('.panel2', 'mouseover');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 9, 'All the list options should render.');
    await click(document.querySelectorAll('.file-action-selector-panel ul li')[7]);
    await click(findAll('.rsa-dropdown-action-list li')[5]);
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 0, 'on click of Reset, the file action button is closed ');
  });

  test('More action, is disabled when no rows are selected', async function(assert) {
    this.set('itemList', []);

    await render(hbs`{{endpoint/file-actionbar itemList=itemList showIcons=false}}`);

    assert.equal(findAll('.more-action-button')[0].classList.contains('is-disabled'), true, 'More action button is disabled.');
  });

  test('More action external lookup for sha1, will call fileAction method', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.endpointCanManageFiles', true);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });
    await render(hbs`{{endpoint/file-actionbar
      itemList=itemList
      showIcons=false
      selectedFileCount=2
      accessControl=accessControl
      showResetRiskScore=true
      fileDownloadButtonStatus=fileDownloadButtonStatus}}`);
    assert.equal(findAll('.more-action-button')[0].classList.contains('is-disabled'), false, 'More action button should enable.');
    await click('.more-action-button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 6, 'All the list options should render.');
    await triggerEvent('.panel2', 'mouseover');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 9, 'All the list options should render.');
    await click(document.querySelectorAll('.file-action-selector-panel ul li')[7]);
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 6, 'on click of lookup sha1, the dropdown is closed ');
  });

  test('More action, is enabled when rows are selected', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    await render(hbs`{{endpoint/file-actionbar itemList=itemList showIcons=false}}`);

    assert.equal(findAll('.more-action-button')[0].classList.contains('is-disabled'), false, 'More action button should enabled.');
  });

  test('File download buttons not added when fileDownloadButtonStatus is not present', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    await render(hbs`{{endpoint/file-actionbar
      itemList=itemList
      showIcons=false
      showResetRiskScore=true
      selectedFileCount=2}}`);

    await click('.more-action-button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 3, '3 list options should render as Download Files options are not present.');

  });

  test('File download buttons not added when accessControl.endpointCanManageFiles is false', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);
    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.endpointCanManageFiles', false);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });

    await render(hbs`{{endpoint/file-actionbar
      itemList=itemList
      showIcons=false
      selectedFileCount=2
      accessControl=accessControl
      showResetRiskScore=true
      fileDownloadButtonStatus=fileDownloadButtonStatus}}`);

    await click('.more-action-button');

    assert.equal(findAll('.rsa-dropdown-action-list li').length, 3, '3 list options should render as File permissions are not present.');

  });

  test('File download buttons are added when accessControl.endpointCanManageFiles is true', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.endpointCanManageFiles', true);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });

    await render(hbs`{{endpoint/file-actionbar
      itemList=itemList
      showIcons=false
      selectedFileCount=2
      showResetRiskScore=true
      accessControl=accessControl
      fileDownloadButtonStatus=fileDownloadButtonStatus}}`);

    await click('.more-action-button');

    assert.equal(findAll('.rsa-dropdown-action-list li').length, 6, '6 list options should render as File permissions are present.');

  });

  test('More action, Download to server', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.endpointCanManageFiles', true);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });

    this.set('downloadFiles', function() {
      assert.ok('External function called on click of button');
    });
    await render(hbs`{{endpoint/file-actionbar
      itemList=itemList
      showIcons=false
      selectedFileCount=2
      downloadFiles=downloadFiles
      accessControl=accessControl
      showResetRiskScore=true
      fileDownloadButtonStatus=fileDownloadButtonStatus}}`);

    await click('.more-action-button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 6, 'All the list options should render.');
    await click('.rsa-dropdown-action-list .panel3');
  });

  test('More action, Download to server disable tooltip', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.endpointCanManageFiles', true);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: true, isSaveLocalAndFileAnalysisDisabled: true });
    this.set('downloadDisabledTooltip', 'Download to server test tool tip');

    this.set('downloadFiles', function() {
      assert.ok('External function called on click of button');
    });
    await render(hbs`{{endpoint/file-actionbar
      itemList=itemList
      showIcons=false
      selectedFileCount=2
      downloadFiles=downloadFiles
      accessControl=accessControl
      showResetRiskScore=true
      fileDownloadButtonStatus=fileDownloadButtonStatus
      downloadDisabledTooltip=downloadDisabledTooltip
    }}`);

    await click('.more-action-button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 6, 'All the list options should render.');
    assert.equal(findAll('.rsa-dropdown-action-list .panel3')[0].title, 'Download to server test tool tip', 'DownloadtoServer disabled tooltip should present');
    assert.equal(findAll('.rsa-dropdown-action-list .panel4')[0].title, 'Download the file to server to save a local copy.', 'Save a local copy tooltip should present');
    assert.equal(findAll('.rsa-dropdown-action-list .panel5')[0].title, 'Download the file to server to analyze.', 'Analyze file tooltip should present');

  });
  test('More action, Save local copy', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.endpointCanManageFiles', true);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: true, isSaveLocalAndFileAnalysisDisabled: false });
    this.set('saveLocalCopy', function() {
      assert.ok('External function called on click of button');
    });
    await render(hbs`{{endpoint/file-actionbar
      itemList=itemList
      showIcons=false
      selectedFileCount=2
      saveLocalCopy=saveLocalCopy
      accessControl=accessControl
      showResetRiskScore=true
      fileDownloadButtonStatus=fileDownloadButtonStatus}}`);

    await click('.more-action-button');
    await click('.rsa-dropdown-action-list .panel4');
  });

  test('More action, Analyze File', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.endpointCanManageFiles', true);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: true, isSaveLocalAndFileAnalysisDisabled: false });
    this.set('analyzeFile', function() {
      assert.ok('External function called on click of button');
    });
    await render(hbs`{{endpoint/file-actionbar
      itemList=itemList
      showIcons=false
      selectedFileCount=2
      showResetRiskScore=true
      analyzeFile=analyzeFile
      accessControl=accessControl
      fileDownloadButtonStatus=fileDownloadButtonStatus}}`);

    await click('.more-action-button');
    await click('.rsa-dropdown-action-list .panel5');
  });

  test('More action, disabled buttons dont get triggered', async function(assert) {
    assert.expect(0);
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.endpointCanManageFiles', true);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: true, isSaveLocalAndFileAnalysisDisabled: false });
    this.set('downloadFiles', function() {
      assert.ok('External function called on click of button');
    });

    await render(hbs`{{endpoint/file-actionbar
      itemList=itemList
      showIcons=false
      selectedFileCount=2
      downloadFiles=downloadFiles
      accessControl=accessControl
      fileDownloadButtonStatus=fileDownloadButtonStatus}}`);

    await click('.more-action-button');
    await click('.rsa-dropdown-action-list .panel3');
  });

  test('Reset Risk score confirmation dialog is opened on click of action', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.endpointCanManageFiles', true);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });
    await render(hbs`
      <div id='modalDestination'></div>
      {{endpoint/file-actionbar
        itemList=itemList
        showIcons=false
        selectedFileCount=2
        showResetRiskScore=true
        accessControl=accessControl
        fileDownloadButtonStatus=fileDownloadButtonStatus
      }}
    `);
    assert.equal(findAll('.more-action-button')[0].classList.contains('is-disabled'), false, 'More action button should enable.');
    await click('.more-action-button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 6, 'All the list options should render.');
    await click(findAll('.rsa-dropdown-action-list li')[5]);
    assert.equal(findAll('#modalDestination .modal-content.reset-risk-score').length, 1, 'Reset confirmation dialog is opened');
  });

  test('Reset Risk score confirmation dialog is closed on cancel', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });
    await render(hbs`
      <div id='modalDestination'></div>
      {{endpoint/file-actionbar
        itemList=itemList
        showIcons=false
        selectedFileCount=2
        fileDownloadButtonStatus=fileDownloadButtonStatus
        showResetRiskScore=true
      }}
    `);
    await click('.more-action-button');
    await click(findAll('.rsa-dropdown-action-list li')[2]);
    assert.equal(findAll('#modalDestination .modal-content.reset-risk-score').length, 1, 'Reset confirmation dialog is opened');
    await click('.closeReset');
    assert.equal(findAll('.modal-content.reset-risk-score').length, 0, 'Reset confirmation dialog is closed');
  });

  test('Reset Risk score is disabled', async function(assert) {
    this.set('itemList', [ { firstFileName: 'abc' } ]);
    await render(hbs`{{endpoint/file-actionbar itemList=itemList showIcons=false showResetRiskScore=true}}`);
    await click('.more-action-button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 3, 'All the list options should render.');
    assert.equal(findAll('.rsa-dropdown-action-list hr')[0].className, 'divider actionSeperator', 'Seperator is available for reset Risk score button');
  });

  test('Info message is present in reset confirmation dialog box', async function(assert) {
    // const done = assert.async();
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.endpointCanManageFiles', true);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });
    this.set('isMaxResetRiskScoreLimit', true);
    await render(hbs`
      <div id='modalDestination'></div>
      {{endpoint/file-actionbar
        itemList=itemList
        showIcons=false
        selectedFileCount=2
        showResetRiskScore=true
        accessControl=accessControl
        fileDownloadButtonStatus=fileDownloadButtonStatus
        isMaxResetRiskScoreLimit=isMaxResetRiskScoreLimit
      }}
    `);
    assert.equal(findAll('.more-action-button')[0].classList.contains('is-disabled'), false, 'More action button should enable.');
    await click('.more-action-button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 6, 'All the list options should render.');
    await click(findAll('.rsa-dropdown-action-list li')[5]);
    assert.equal(findAll('#modalDestination .modal-content.reset-risk-score .max-limit-info').length, 1, 'Info message is present in reset confirmation dialog box');
  });

  test('No info message is present in reset confirmation dialog box', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.endpointCanManageFiles', true);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });
    this.set('isMaxResetRiskScoreLimit', false);
    await render(hbs`
      <div id='modalDestination'></div>
      {{endpoint/file-actionbar
        itemList=itemList
        showIcons=false
        showResetRiskScore=true
        selectedFileCount=2
        accessControl=accessControl
        fileDownloadButtonStatus=fileDownloadButtonStatus
        isMaxResetRiskScoreLimit=isMaxResetRiskScoreLimit
      }}
    `);
    assert.equal(findAll('.more-action-button')[0].classList.contains('is-disabled'), false, 'More action button should enable.');
    await click('.more-action-button');
    await click(findAll('.rsa-dropdown-action-list li')[5]);
    assert.equal(findAll('.modal-content.reset-risk-score .max-limit-info').length, 0, 'Info message is not present in reset confirmation dialog box');
  });

  test('Tab label will diplay in the action bar', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.endpointCanManageFiles', true);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });
    this.set('isMaxResetRiskScoreLimit', false);
    this.set('isDisplayTabLabel', true);
    this.set('tabLabel', 'test label');
    await render(hbs`{{endpoint/file-actionbar
      itemList=itemList
      showIcons=false
      selectedFileCount=2
      accessControl=accessControl
      fileDownloadButtonStatus=fileDownloadButtonStatus
      isMaxResetRiskScoreLimit=isMaxResetRiskScoreLimit
      isDisplayTabLabel=isDisplayTabLabel
      tabLabel=tabLabel
    }}`);
    assert.equal(findAll('.tab-label')[0].textContent.trim(), 'test label', 'label displayed in the file action bar');
  });

  test('Reset risk score button is not present', async function(assert) {
    this.set('itemList', [
      { machineOSType: 'windows', fileName: 'abc', checksumSha256: 'abc1', checksumSha1: 'abc2', checksumMd5: 'abcmd5' },
      { machineOSType: 'windows', fileName: 'xyz', checksumSha256: 'xyz1', checksumSha1: 'xyz2', checksumMd5: 'xyzmd5' }
    ]);

    this.set('accessControl', EmberObject.create({}));
    this.set('accessControl.endpointCanManageFiles', true);
    this.set('fileDownloadButtonStatus', { isDownloadToServerDisabled: false, isSaveLocalAndFileAnalysisDisabled: true });
    this.set('isMaxResetRiskScoreLimit', true);
    await render(hbs`{{endpoint/file-actionbar
      itemList=itemList
      showIcons=false
      selectedFileCount=2
      showResetRiskScore=false
      accessControl=accessControl
      fileDownloadButtonStatus=fileDownloadButtonStatus
    }}`);
    assert.equal(findAll('.more-action-button')[0].classList.contains('is-disabled'), false, 'More action button should enable.');
    await click('.more-action-button');
    assert.equal(findAll('.rsa-dropdown-action-list li').length, 5, 'All the list options should render except reset risk score.');
  });

  test('List being null', async function(assert) {
    this.set('itemList', undefined);
    await render(hbs`{{endpoint/file-actionbar itemList=itemList selectedFileCount=0}}`);
    assert.equal(findAll('.file-actionbar .file-status-button')[0].classList.contains('is-disabled'), true, 'Edit file status Button is disabled when no files are selected');
    assert.equal(findAll('.file-actionbar .event-analysis')[0].classList.contains('is-disabled'), true, 'Pivot-to-investigate Button is disabled when no files are selected');
  });

});
