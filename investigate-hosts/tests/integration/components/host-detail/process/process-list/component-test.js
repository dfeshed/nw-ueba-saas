import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import engineResolver from '../../../../../helpers/engine-resolver';
import processData from '../../../../../integration/components/state/process-data';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchSocket } from '../../../../../helpers/patch-socket';


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
test('Check that row click action is handled', function(assert) {
  assert.expect(5);
  new ReduxDataHelper(setState)
    .agentId(1)
    .scanTime(123456789)
    .processList(processData.processList)
    .processTree(processData.processTree)
    .selectedTab(null).build();
  this.render(hbs`<style>
      box, section {
        min-height: 2000px
      }
  </style>
  {{host-detail/process/process-list}}`);

  patchSocket((method, modelName, query) => {
    assert.equal(method, 'getProcess');
    assert.equal(modelName, 'endpoint');
    assert.deepEqual(query, {
      'data': {
        'agentId': 1,
        'pid': 664,
        'scanTime': 123456789
      }
    });
  });

  assert.equal(this.$('.rsa-data-table-body-row:eq(2)').hasClass('is-selected'), false, '2nd row is not selected before click');
  this.$('.rsa-data-table-body-row:eq(2)').click();
  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-body-row:eq(2)').hasClass('is-selected'), true, '2nd row is selected after click');
  });
});

test('Check that sort action is performed & correct values are passed', function(assert) {
  assert.expect(5);
  new ReduxDataHelper(setState)
    .sortField('name')
    .agentId(1)
    .scanTime(1234567890)
    .isDescOrder(false).build();
  this.render(hbs`<style>
      box, section {
        min-height: 2000px
      }
  </style>
  {{host-detail/process/process-list}}`);

  patchSocket((method, modelName, query) => {
    assert.equal(method, 'getProcessList');
    assert.equal(modelName, 'endpoint');
    assert.deepEqual(query, {
      'data': {
        'agentId': 1,
        'scanTime': 1234567890,
        'sort': {
          'descending': true,
          'key': 'name'
        }
      }
    });
  });

  assert.equal(this.$('.rsa-data-table-header-cell:eq(0)').find('i').hasClass('rsa-icon-arrow-up-12-filled'), true, 'Default arrow up icon before sorting');
  this.$('.rsa-data-table-header-cell:eq(0)').find('.rsa-icon').click();
  return wait().then(() => {
    assert.equal(this.$('.rsa-data-table-header-cell:eq(0)').find('i').hasClass('rsa-icon-arrow-down-12-filled'), true, 'Arrow down icon appears after sorting');
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
