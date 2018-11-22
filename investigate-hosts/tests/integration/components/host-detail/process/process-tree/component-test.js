import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, find, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import processData from '../../../../../integration/components/state/process-data';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchSocket } from '../../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | host-detail/process/process-tree', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      applyPatch(state);
    };
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('Column Names appear in datatable header', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .machineOSType('windows')
      .build();

    await render(hbs`{{host-detail/process/process-tree}}`);

    assert.equal(findAll('.rsa-data-table-header .rsa-data-table-header-cell').length, 9, '9 columns in header, including the checkbox');
    assert.equal(findAll('.rsa-data-table-header-cell')[1].textContent.trim(), 'Process Name', 'First column is Process Name');
    assert.equal(findAll('.rsa-data-table-header-cell')[2].textContent.trim(), 'PID', 'Second column is PID');
  });

  test('Get the length of visible items in datatable', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .machineOSType('windows')
      .selectedTab(null)
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree}}`);

    return settled().then(() => {
      assert.equal(findAll('.rsa-process-tree .rsa-data-table-body-row').length, 77, '77 visible items in datatable');
    });
  });
  test('Check that row click action is handled', async function(assert) {
    assert.expect(5);
    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null)
      .machineOSType('windows')
      .build();
    await render(hbs`
      <style>
          box, section {
            min-height: 2000px
          }
      </style>
      {{host-detail/process/process-tree}}`);

    patchSocket((method, modelName, query) => {
      assert.equal(method, 'getProcess');
      assert.equal(modelName, 'endpoint');
      assert.deepEqual(query, {
        'data': {
          'agentId': 1,
          'pid': 29680,
          'scanTime': 123456789
        }
      });
    });

    assert.equal(findAll('.rsa-process-tree .rsa-data-table-body-row')[3].classList.contains('is-selected'), false, 'Forth row is not selected before click');
    await click(findAll('.rsa-process-tree .rsa-data-table-body-row')[3]);
    return settled().then(() => {
      assert.equal(findAll('.rsa-process-tree .rsa-data-table-body-row')[3].classList.contains('is-selected'), true, 'Forth row is selected after click');
    });
  });

  test('Should apply rsa-loader if process tree is loading', async function(assert) {
    new ReduxDataHelper(setState).isProcessTreeLoading(true).machineOSType('windows').build();
    await render(hbs`{{host-detail/process/process-tree}}`);
    assert.equal(find('.rsa-loader').classList.contains('is-medium'), true, 'rsa-loader applied when process tree is loading');
  });

  test('Should not apply rsa-loader if process tree loading is complete', async function(assert) {
    new ReduxDataHelper(setState).isProcessTreeLoading(false).machineOSType('windows').build();
    await render(hbs`{{host-detail/process/process-tree}}`);
    assert.equal(findAll('.rsa-loader').length, 0, 'rsa-loader not applied when process tree loading is complete');
  });

  test('Check that no results message rendered if there is no process information', async function(assert) {
    new ReduxDataHelper(setState).machineOSType('windows').build();
    await render(hbs`{{host-detail/process/process-tree}}`);
    assert.equal(find('.rsa-data-table-body').textContent.trim(), 'No process information was found.', 'No process information message rendered');
  });

  test('Renders number of process-names, its leaf nodes & non-leaf nodes', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null)
      .machineOSType('windows')
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree}}`);

    return settled().then(() => {
      assert.equal(findAll('.rsa-process-tree .process-name').length, 77, '77 process names present');
      assert.equal(findAll('.rsa-process-tree .process-name.is-leaf').length, 54, '54 last child process (leaf nodes)');
      const nonLeafItems = findAll('.rsa-process-tree .process-name').length - findAll('.rsa-process-tree .process-name.is-leaf').length;
      assert.equal(nonLeafItems, 23, '23 length of non-leaf process');
    });
  });

  test('Style property of process name is computed correctly for different levels in process tree', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null)
      .machineOSType('windows')
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree}}`);

    return settled().then(() => {
      assert.equal(document.querySelectorAll('.rsa-process-tree .process-name-column')[0].querySelector('span').getAttribute('style'), 'padding-left: 0px;', 'style property computed correctly for root node');
      assert.equal(document.querySelectorAll('.rsa-process-tree .process-name-column')[1].querySelector('span').getAttribute('style'), 'padding-left: 30px;', 'style property computed correctly for level 1 node');
      assert.equal(document.querySelectorAll('.rsa-process-tree .process-name-column')[3].querySelector('span').getAttribute('style'), 'padding-left: 60px;', 'style property computed correctly for level 2 node');
    });
  });

  test('Check that toggle expand action is called', async function(assert) {
    assert.expect(2);
    this.set('value', true);
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null)
      .machineOSType('windows')
      .build();
    await render(hbs`
      <style>
        box, section {
          min-height: 2000px
        }
      </style>
      {{host-detail/process/process-tree}}`);

    assert.equal(findAll('.rsa-process-tree .process-name-column .tree-expand-icon')[0].classList.contains('is-expanded'), true, '1st row is expanded before toggle');
    await click('.rsa-process-tree .process-name-column .tree-expand-icon')[0];
    return settled().then(() => {
      assert.equal(findAll('.rsa-process-tree .process-name-column .tree-expand-icon')[0].classList.contains('is-expanded'), false, '1st row is collapsed after toggle');
    });
  });

  test('clicking on the row calls the external function', async function(assert) {
    assert.expect(5);
    this.set('openPanel', function() {
      assert.ok(true, 'open panel is called');
    });
    this.set('closePanel', function() {
      assert.ok(true, 'close panel is called');
    });

    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null).build();

    await render(hbs`{{host-detail/process/process-tree openPropertyPanel=(action openPanel) closePropertyPanel=(action closePanel)}}`);


    assert.equal(find(findAll('.rsa-data-table-body-row')[2]).classList.contains('is-selected'), false, '2nd row is not selected before click');
    await click(findAll('.rsa-data-table-body-row')[2]);
    return settled().then(async() => {
      assert.equal(find(findAll('.rsa-data-table-body-row')[2]).classList.contains('is-selected'), true, '2nd row is selected after click');
      await click(findAll('.rsa-data-table-body-row')[2]); // clicking on same row deselect the row
      assert.equal(find(findAll('.rsa-data-table-body-row')[2]).classList.contains('is-selected'), false, '2nd row is selected after click');
    });
  });


});
