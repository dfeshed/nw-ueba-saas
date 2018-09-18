import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchSocket } from '../../../../helpers/patch-socket';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import hostFiles from '../../state/host.files';

let setState;

moduleForComponent('host-detail/files', 'Integration | Component | endpoint host detail/files', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    initialize(this);
    this.registry.injection('component', 'i18n', 'service:i18n');
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('table still loading', function(assert) {
  this.render(hbs`{{host-detail/files}}`);
  new ReduxDataHelper(setState)
    .filesLoadMoreStatus('wait')
    .selectedFileList([])
    .fileStatusData({})
    .build();
  assert.equal(this.$('.rsa-data-table div.rsa-loader').length, 1, 'RSA loader displayed');
});

test('table with no data', function(assert) {
  new ReduxDataHelper(setState)
    .filesLoadMoreStatus('completed')
    .selectedFileList([])
    .fileStatusData({})
    .files([]).build();
  this.render(hbs`{{host-detail/files}}`);

  assert.equal(this.$('.no-results-message').text().trim(), 'No Results Found.', 'Empty table message');
});

test('windows specific columns are rendered', function(assert) {
  new ReduxDataHelper(setState)
    .columnsConfig('windows')
    .selectedFileList([])
    .fileStatusData({})
    .build();
  this.render(hbs`{{host-detail/files}}`);
  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-data-table-header-cell').length, 20, '20 columns with right click options rendered for windows');
    assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-icon').length, 3, '3 sortable columns, others have sort disabled');
  });
});

test('mac specific columns are rendered', function(assert) {
  new ReduxDataHelper(setState)
    .columnsConfig('mac')
    .selectedFileList([])
    .fileStatusData({})
    .build();
  this.render(hbs`{{host-detail/files}}`);

  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-data-table-header-cell').length, 20, '20 columns with right click optionsrendered for windows');
    assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-icon').length, 3, '3 sortable columns, others have sort disabled');
  });
});

test('linux specific columns are rendered', function(assert) {
  new ReduxDataHelper(setState)
    .columnsConfig('linux')
    .selectedFileList([])
    .fileStatusData({})
    .build();
  this.render(hbs`{{host-detail/files}}`);

  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-data-table-header-cell').length, 20, '20 columns with right click options rendered for windows');
    assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-icon').length, 2, '2 sortable columns, others have sort disabled');
  });
});

test('check sortyBy action is called', function(assert) {
  new ReduxDataHelper(setState)
    .filesLoadMoreStatus('stopped')
    .files(hostFiles.files)
    .selectedFileList([])
    .fileStatusData({})
    .build();
  this.render(hbs`{{host-detail/files}}`);

  patchSocket((method, modelName, query) => {
    assert.equal(method, 'getHostFilesPages');
    assert.equal(modelName, 'endpoint');
    assert.deepEqual(query, {
      'data': {
        'criteria': {
          'agentId': null,
          'checksumSha256': null,
          'scanTime': null
        },
        'pageNumber': 0,
        'sort': {
          'descending': true,
          'keys': ['fileName']
        }
      }
    });
  });
  return wait().then(() => {
    this.$('.rsa-data-table-header-row').find('.rsa-icon')[0].click();
  });
});

test('load more calls getHostFiles', function(assert) {
  new ReduxDataHelper(setState)
    .filesLoadMoreStatus('stopped')
    .files(hostFiles.files)
    .selectedFileList([])
    .fileStatusData({})
    .build();
  this.render(hbs`{{host-detail/files}}`);
  patchSocket((method, modelName, query) => {
    assert.equal(method, 'getHostFilesPages');
    assert.equal(modelName, 'endpoint');
    assert.deepEqual(query, {
      'data': {
        'criteria': {
          'agentId': null,
          'checksumSha256': null,
          'scanTime': null
        },
        'pageNumber': NaN,
        'sort': {
          'descending': undefined,
          'keys': [undefined]
        }
      }
    });
  });
  return wait().then(() => {
    this.$('.rsa-data-table-load-more button.rsa-form-button').click();
  });
});

test('table with data', function(assert) {
  new ReduxDataHelper(setState)
    .filesLoadMoreStatus('stopped')
    .totalItems(500)
    .columnsConfig('linux')
    .selectedFileList([])
    .fileStatusData({})
    .files(hostFiles.files).build();

  // set height to get all lazy rendered items on the page
  this.render(hbs`
    <style>
      box, section { min-height: 1000px }
    </style>
    {{host-detail/files}}
  `);

  return wait().then(() => {
    const tableRows = this.$('.rsa-data-table-body-row').length;
    const fileInfoText = this.$('.file-info').text().trim();
    const firstRowOwnerText = this.$('.rsa-data-table-body-row').first().find('.rsa-data-table-body-cell').last().text().trim();
    const fileName = this.$(this.$(this.$('.rsa-data-table-body-row')[1]).find('.rsa-data-table-body-cell')[1]).text().trim();
    assert.equal(tableRows, 6, 'Verify number of rows');
    assert.equal(fileInfoText, '6 of 500 files', 'Footer displayed correctly');
    assert.equal(fileName, 'ata_generic.ko', 'First column is file name');
    assert.equal(firstRowOwnerText, 'root (0)', 'last column is owner (for linux)');
  });
});

test('Changing file status action bar', function(assert) {
  new ReduxDataHelper(setState)
    .filesLoadMoreStatus('stopped')
    .totalItems(500)
    .columnsConfig('linux')
    .selectedFileList([])
    .fileStatusData({})
    .files(hostFiles.files).build();

  // set height to get all lazy rendered items on the page
  this.render(hbs`
    <style>
      box, section { }
    </style>
    {{host-detail/files}}
  `);

  return wait().then(() => {
    const fileStatusButton = this.$('.file-status-button');
    const firstRowCheckbox = this.$('.rsa-data-table-body-row').first().find('.rsa-data-table-body-cell').first().find('.rsa-form-checkbox');
    let isDesabled = this.$(fileStatusButton).hasClass('is-disabled');
    assert.equal(isDesabled, true, 'Edit file status button should be desabled before selecting row');
    this.$(firstRowCheckbox).click();
    return wait().then(() => {
      isDesabled = this.$(fileStatusButton).hasClass('is-disabled');
      assert.equal(isDesabled, false, 'Edit file status button should enable after selecting row');
    });
  });
});

test('Property panel is rendered', function(assert) {
  new ReduxDataHelper(setState)
    .filesLoadMoreStatus('stopped')
    .files(hostFiles.files)
    .selectedFileList([])
    .fileStatusData({})
    .build();
  this.render(hbs`{{host-detail/files}}`);

  return wait().then(() => {
    const propPanelItemWithText = this.$('.host-property-panel .tooltip-text:contains("systemd-journald.service")');
    assert.equal(this.$('.header-section__title').text().trim(), 'File Properties', 'Panel title displayed');
    assert.equal(propPanelItemWithText.length, 1, 'First property is File name');
  });
});
