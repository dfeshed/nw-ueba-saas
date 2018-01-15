import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import engineResolver from '../../../../../helpers/engine-resolver';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import sinon from 'sinon';
import processData from '../../../../../integration/components/state/process-data';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import startApp from '../../../../../helpers/start-app';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import * as DataCreators from 'investigate-hosts/actions/data-creators/process';

const application = startApp();
initialize(application);

let setState;

moduleForComponent('host-detail/process/process-list', 'Integration | Component | host-detail/process/process-list', {
  integration: true,
  resolver: engineResolver('investigate-hosts'),
  beforeEach() {
    setState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('Column Names appear in datatable header', function(assert) {
  assert.expect(3);
  this.render(hbs`{{host-detail/process/process-list}}`);
  assert.equal(this.$('.rsa-data-table-header-cell').find('.rsa-icon').length, 2, '2 sortable columns in header');
  assert.equal(this.$('.rsa-data-table-header-cell:eq(0)').text().trim(), 'Process Name', 'First column is Process Name');
  assert.equal(this.$('.rsa-data-table-header-cell:eq(1)').text().trim(), 'PID', 'First column is PID');
});

test('Process list is rendered', function(assert) {
  new ReduxDataHelper(setState)
  .processList(processData.processList)
  .processTree(processData.processTree)
  .selectedTab(null).build();
  this.render(hbs`
  <style>
      box, section {
        min-height: 2000px
      }
  </style>
  {{host-detail/process/process-list}}`);
  assert.equal(this.$('.rsa-data-table-body-row').length, 77, 'Row Length of process list rendered');
});

sinon.stub(DataCreators, 'getProcessDetails');

test('Check that row click action is handled', function(assert) {
  assert.expect();
  new ReduxDataHelper(setState)
  .processList(processData.processList)
  .processTree(processData.processTree)
  .selectedTab(null).build();
  this.render(hbs`<style>
      box, section {
        min-height: 2000px
      }
  </style>
  {{host-detail/process/process-list}}`);
  assert.equal(this.$('.rsa-data-table-body-row:eq(2)').hasClass('is-selected'), false, '2nd row is not selected before click');
  this.$('.rsa-data-table-body-row:eq(2)').click();
  return wait().then(() => {
    assert.equal(DataCreators.getProcessDetails.calledOnce, true, 'row click action is called');
    assert.equal(DataCreators.getProcessDetails.args[0][0], 664, 'PID value passed on row-click is rendered');
    assert.equal(this.$('.rsa-data-table-body-row:eq(2)').hasClass('is-selected'), true, '2nd row is selected after click');
    DataCreators.getProcessDetails.restore();
  });
});

sinon.stub(DataCreators, 'sortBy');

test('Check that sort action is performed & correct values are passed', function(assert) {
  assert.expect(5);
  new ReduxDataHelper(setState)
    .sortField('name')
    .isDescOrder(false).build();
  this.render(hbs`<style>
      box, section {
        min-height: 2000px
      }
  </style>
  {{host-detail/process/process-list}}`);
  assert.equal(this.$('.rsa-data-table-header-cell:eq(0)').find('i').hasClass('rsa-icon-arrow-up-12-filled'), true, 'Default arrow up icon before sorting');
  this.$('.rsa-data-table-header-cell:eq(0)').find('.rsa-icon').click();
  return wait().then(() => {
    assert.equal(DataCreators.sortBy.calledOnce, true, 'sort action is called');
    assert.equal(DataCreators.sortBy.args[0][0], 'name', 'Value passed in sorting action is rendered');
    assert.equal(DataCreators.sortBy.args[0][1], false, 'Value passed in sorting action is rendered');
    assert.equal(this.$('.rsa-data-table-header-cell:eq(0)').find('i').hasClass('rsa-icon-arrow-down-12-filled'), true, 'Arrow down icon appears after sorting');
    DataCreators.sortBy.restore();
  });
});

test('Should apply appropriate rsa-loader if process tree is loading', function(assert) {
  new ReduxDataHelper(setState).isProcessTreeLoading(true).build();
  this.render(hbs`{{host-detail/process/process-list}}`);
  assert.equal(this.$('.rsa-loader').hasClass('is-medium'), true, 'Appropriate rsa-loader applied when process tree is loading');
});

test('Should not apply rsa-loader if process tree is not loading', function(assert) {
  new ReduxDataHelper(setState).isProcessTreeLoading(false).build();
  this.render(hbs`{{host-detail/process/process-list}}`);
  assert.equal(this.$('.rsa-loader').hasClass('is-medium'), false, 'rsa-loader not applied when process tree is not loading');
});

test('Check that no results message rendered if there is no process information', function(assert) {
  this.render(hbs`{{#host-detail/process/process-list as |item index column|}}{{/host-detail/process/process-list}}`);
  assert.equal(this.$('.rsa-data-table-body').text().trim(), 'No process information were found', 'No process information message rendered');
});