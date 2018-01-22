import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import engineResolver from '../../../../../helpers/engine-resolver';
import sinon from 'sinon';
import processData from '../../../../../integration/components/state/process-data';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import * as DataCreators from 'investigate-hosts/actions/data-creators/process';

let setState;

moduleForComponent('host-detail/process/process-tree', 'Integration | Component | host-detail/process/process-tree', {
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
  this.render(hbs`{{host-detail/process/process-tree}}`);

  assert.equal(this.$('.rsa-data-table-header .rsa-data-table-header-cell').length, 2, '2 columns in header');
  assert.equal(this.$('.rsa-data-table-header-cell:eq(0)').text().trim(), 'Process Name', 'First column is Process Name');
  assert.equal(this.$('.rsa-data-table-header-cell:eq(1)').text().trim(), 'PID', 'Second column is PID');
});

test('Get the length of visible items in datatable', function(assert) {
  new ReduxDataHelper(setState)
    .processList(processData.processList)
    .processTree(processData.processTree)
    .selectedTab(null)
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 2000px
      }
    </style>
    {{host-detail/process/process-tree}}`);

  return wait().then(() => {
    assert.equal(this.$('.rsa-process-tree .rsa-data-table-body-row').length, 77, '77 visible items in datatable');
  });
});
sinon.stub(DataCreators, 'getProcessDetails');
test('Check that row click action is handled', function(assert) {
  assert.expect(4);
  new ReduxDataHelper(setState)
    .processList(processData.processList)
    .processTree(processData.processTree)
    .selectedTab(null)
    .build();
  this.render(hbs`
    <style>
        box, section {
          min-height: 2000px
        }
    </style>
    {{host-detail/process/process-tree}}`);

  assert.equal(this.$('.rsa-process-tree .rsa-data-table-body-row:eq(3)').hasClass('is-selected'), false, 'Forth row is not selected before click');
  this.$('.rsa-process-tree .rsa-data-table-body-row:eq(3)').click();
  return wait().then(() => {
    assert.equal(DataCreators.getProcessDetails.calledOnce, true, 'row click action is called');
    assert.equal(DataCreators.getProcessDetails.args[0][0], 29680, 'PID value passed on row-click is rendered');
    assert.equal(this.$('.rsa-process-tree .rsa-data-table-body-row:eq(3)').hasClass('is-selected'), true, 'Forth row is selected after click');
    DataCreators.getProcessDetails.restore();
  });
});

test('Should apply rsa-loader if process tree is loading', function(assert) {
  new ReduxDataHelper(setState).isProcessTreeLoading(true).build();
  this.render(hbs`{{host-detail/process/process-tree}}`);
  assert.equal(this.$('.rsa-loader').hasClass('is-medium'), true, 'rsa-loader applied when process tree is loading');
});

test('Should not apply rsa-loader if process tree loading is complete', function(assert) {
  new ReduxDataHelper(setState).isProcessTreeLoading(false).build();
  this.render(hbs`{{host-detail/process/process-tree}}`);
  assert.equal(this.$('.rsa-loader').hasClass('is-medium'), false, 'rsa-loader not applied when process tree loading is complete');
});

test('Check that no results message rendered if there is no process information', function(assert) {
  this.render(hbs`{{host-detail/process/process-tree}}`);
  assert.equal(this.$('.rsa-data-table-body').text().trim(), 'No process information were found', 'No process information message rendered');
});

test('Renders number of process-names, its leaf nodes & non-leaf nodes', function(assert) {
  assert.expect(3);
  new ReduxDataHelper(setState)
    .processList(processData.processList)
    .processTree(processData.processTree)
    .selectedTab(null)
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 2000px
      }
    </style>
    {{host-detail/process/process-tree}}`);

  return wait().then(() => {
    assert.equal(this.$('.rsa-process-tree .process-name').length, 77, '77 process names present');
    assert.equal(this.$('.rsa-process-tree .process-name.is-leaf').length, 54, '54 last child process (leaf nodes)');
    const nonLeafItems = this.$('.rsa-process-tree .process-name').length - this.$('.rsa-process-tree .process-name.is-leaf').length;
    assert.equal(nonLeafItems, 23, '23 length of non-leaf process');
  });
});

test('Style property of process name is computed correctly for different levels in process tree', function(assert) {
  assert.expect(3);
  new ReduxDataHelper(setState)
    .processList(processData.processList)
    .processTree(processData.processTree)
    .selectedTab(null)
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 2000px
      }
    </style>
    {{host-detail/process/process-tree}}`);

  return wait().then(() => {
    assert.equal(this.$('.rsa-process-tree .process-name-column:eq(0)').find('span').attr('style'), 'padding-left: 0px;', 'style property computed correctly for root node');
    assert.equal(this.$('.rsa-process-tree .process-name-column:eq(1)').find('span').attr('style'), 'padding-left: 30px;', 'style property computed correctly for level 1 node');
    assert.equal(this.$('.rsa-process-tree .process-name-column:eq(3)').find('span').attr('style'), 'padding-left: 60px;', 'style property computed correctly for level 2 node');
  });
});

test('Check that toggle expand action is called', function(assert) {
  assert.expect(2);
  this.set('value', true);
  new ReduxDataHelper(setState)
    .processList(processData.processList)
    .processTree(processData.processTree)
    .selectedTab(null)
    .build();
  this.render(hbs`
    <style>
      box, section {
        min-height: 2000px
      }
    </style>
    {{host-detail/process/process-tree}}`);

  assert.equal(this.$('.rsa-process-tree .process-name-column:eq(0) .tree-expand-icon').hasClass('is-expanded'), true, '1st row is expanded before toggle');
  this.$('.rsa-process-tree .process-name-column:eq(0) .tree-expand-icon').click();
  return wait().then(() => {
    assert.equal(this.$('.rsa-process-tree .process-name-column:eq(0) .tree-expand-icon').hasClass('is-expanded'), false, '1st row is collapsed after toggle');
  });
});
