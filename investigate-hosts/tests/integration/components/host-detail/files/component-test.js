import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import sinon from 'sinon';
import * as DataCreators from 'investigate-hosts/actions/data-creators/files';

import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';

import hostFiles from '../../state/host.files';

let setState;

moduleForComponent('host-detail/files', 'Integration | Component | endpoint host detail/files', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
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
    .filesLoadMoreStatus('wait').build();
  assert.equal(this.$('.rsa-data-table div.rsa-loader').length, 1, 'RSA loader displayed');
});

test('table with no data', function(assert) {
  new ReduxDataHelper(setState)
    .filesLoadMoreStatus('completed')
    .files([]).build();
  this.render(hbs`{{host-detail/files}}`);

  assert.equal(this.$('.no-results-message').text().trim(), 'No Results Found.', 'Empty table message');
});

test('windows specific columns are rendered', function(assert) {
  new ReduxDataHelper(setState)
    .columnsConfig('windows').build();
  this.render(hbs`{{host-detail/files}}`);

  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-data-table-header-cell').length, 6, '6 columns rendered for windows');
    assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-icon').length, 3, '3 sortable columns, others have sort disabled');
  });
});

test('mac specific columns are rendered', function(assert) {
  new ReduxDataHelper(setState)
    .columnsConfig('mac').build();
  this.render(hbs`{{host-detail/files}}`);

  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-data-table-header-cell').length, 6, '6 columns rendered for windows');
    assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-icon').length, 3, '3 sortable columns, others have sort disabled');
  });
});

test('linux specific columns are rendered', function(assert) {
  new ReduxDataHelper(setState)
    .columnsConfig('linux').build();
  this.render(hbs`{{host-detail/files}}`);

  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-data-table-header-cell').length, 6, '6 columns rendered for windows');
    assert.equal(this.$('.rsa-data-table-header-row').find('.rsa-icon').length, 2, '2 sortable columns, others have sort disabled');
  });
});

sinon.stub(DataCreators, 'sortBy');
test('check sortyBy action is called', function(assert) {
  new ReduxDataHelper(setState)
    .filesLoadMoreStatus('stopped')
    .files(hostFiles.files).build();
  this.render(hbs`{{host-detail/files}}`);

  return wait().then(() => {
    this.$('.rsa-data-table-header-row').find('.rsa-icon')[0].click();
    assert.equal(DataCreators.sortBy.calledOnce, true, 'sortBy action is called');
    assert.equal(DataCreators.sortBy.args[0][0].sortField, 'fileName', 'sortField is fileName');
    assert.equal(DataCreators.sortBy.args[0][0].isDescOrder, true, 'isDescOrder is true');
    DataCreators.sortBy.restore();
  });
});

sinon.stub(DataCreators, 'getHostFiles');
test('load more calls getHostFiles', function(assert) {
  new ReduxDataHelper(setState)
    .filesLoadMoreStatus('stopped')
    .files(hostFiles.files).build();
  this.render(hbs`{{host-detail/files}}`);

  return wait().then(() => {
    this.$('.rsa-data-table-load-more button.rsa-form-button').click();
    assert.equal(DataCreators.getHostFiles.calledOnce, true, 'Load More click calls an action');
    DataCreators.getHostFiles.restore();
  });
});

test('table with data', function(assert) {
  new ReduxDataHelper(setState)
    .filesLoadMoreStatus('stopped')
    .totalItems(500)
    .columnsConfig('linux')
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
    const fileName = this.$('.rsa-data-table-body-row').first().find('.rsa-data-table-body-cell').first().text().trim();

    assert.equal(tableRows, 6, 'Verify number of rows');
    assert.equal(fileInfoText, '6 of 500 files', 'Footer displayed correctly');
    assert.equal(fileName, 'systemd-journald.service', 'First column is file name');
    assert.equal(firstRowOwnerText, 'root (0)', 'last column is owner (for linux)');
  });
});

test('Property panel is rendered', function(assert) {
  new ReduxDataHelper(setState)
    .filesLoadMoreStatus('stopped')
    .files(hostFiles.files).build();
  this.render(hbs`{{host-detail/files}}`);

  return wait().then(() => {
    const propPanelItemWithText = this.$('.host-property-panel .host-text:contains("systemd-journald.service")');
    assert.equal(this.$('.header-section__title').text().trim(), 'File Properties', 'Panel title displayed');
    assert.equal(propPanelItemWithText.length, 1, 'First property is File name');
  });
});
