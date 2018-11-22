import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, findAll, click, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import processData from '../../../../../integration/components/state/process-data';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchSocket } from '../../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | host-detail/process/process-list', function(hooks) {
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
    await render(hbs`{{host-detail/process/process-list}}`);
    assert.equal(findAll('.rsa-data-table-header-cell .rsa-icon').length, 8, '8 sortable columns in header, along with the checkbox');
    assert.equal(findAll('.rsa-data-table-header-cell')[1].textContent.trim(), 'Process Name', 'First column is Process Name');
    assert.equal(findAll('.rsa-data-table-header-cell')[2].textContent.trim(), 'PID', 'First column is PID');
  });

  test('Process list is rendered', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null)
      .build();
    await render(hbs`
    <style>
        box, section {
          min-height: 2000px
        }
    </style>
    {{host-detail/process/process-list}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length, 77, 'Row Length of process list rendered');
  });
  test('Check that row click action is handled', async function(assert) {
    assert.expect(5);
    new ReduxDataHelper(setState)
      .agentId(1)
      .scanTime(123456789)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null).build();
    await render(hbs`<style>
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

    assert.equal(find(findAll('.rsa-data-table-body-row')[2]).classList.contains('is-selected'), false, '2nd row is not selected before click');
    await click(findAll('.rsa-data-table-body-row')[2]);
    return settled().then(() => {
      assert.equal(find(findAll('.rsa-data-table-body-row')[2]).classList.contains('is-selected'), true, '2nd row is selected after click');
    });
  });

  test('Check that sort action is performed & correct values are passed', async function(assert) {
    assert.expect(5);
    new ReduxDataHelper(setState)
      .sortField('name')
      .agentId(1)
      .scanTime(1234567890)
      .isDescOrder(false).build();
    await render(hbs`<style>
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
            'keys': ['name']
          }
        }
      });
    });

    assert.equal(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('i').classList.contains('rsa-icon-arrow-up-12-filled'), true, 'Default arrow up icon before sorting');
    await click(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('.rsa-icon'));
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('i').classList.contains('rsa-icon-arrow-down-12-filled'), true, 'Arrow down icon appears after sorting');
    });
  });

  test('Should apply appropriate rsa-loader if process tree is loading', async function(assert) {
    new ReduxDataHelper(setState).isProcessTreeLoading(true).build();
    await render(hbs`{{host-detail/process/process-list}}`);
    assert.equal(document.querySelector('.rsa-loader').classList.contains('is-medium'), true, 'Appropriate rsa-loader applied when process tree is loading');
  });

  test('Should not apply rsa-loader if process tree is not loading', async function(assert) {
    new ReduxDataHelper(setState).isProcessTreeLoading(false).build();
    await render(hbs`{{host-detail/process/process-list}}`);
    assert.equal(findAll('.rsa-loader').length, 0, 'rsa-loader not applied when process tree is not loading');
  });

  test('Check that no results message rendered if there is no process information', async function(assert) {
    await render(
      hbs`{{#host-detail/process/process-list as |item index column|}}{{/host-detail/process/process-list}}`
    );
    assert.equal(find('.rsa-data-table-body').textContent.trim(), 'No process information was found.', 'No process information message rendered');
  });

  test('Check if checkbox selects the items', async function(assert) {
    new ReduxDataHelper(setState)
      .processList(processData.processList)
      .processTree(processData.processTree)
      .selectedTab(null)
      .build();
    await render(hbs`
    <style>
        box, section {
          min-height: 2000px
        }
    </style>
    {{host-detail/process/process-list}}`);
    await click(findAll('.rsa-form-checkbox')[0]);
    let state = this.owner.lookup('service:redux').getState();
    assert.equal(state.endpoint.process.selectedProcessList.length, 77, 'All processes selected');
    await click(findAll('.rsa-form-checkbox')[1]);
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.endpoint.process.selectedProcessList.length, 76, '76 processes selected after deselecting one');
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

    await render(hbs`{{host-detail/process/process-list openPropertyPanel=(action openPanel) closePropertyPanel=(action closePanel)}}`);


    assert.equal(find(findAll('.rsa-data-table-body-row')[2]).classList.contains('is-selected'), false, '2nd row is not selected before click');
    await click(findAll('.rsa-data-table-body-row')[2]);
    return settled().then(async() => {
      assert.equal(find(findAll('.rsa-data-table-body-row')[2]).classList.contains('is-selected'), true, '2nd row is selected after click');
      await click(findAll('.rsa-data-table-body-row')[2]); // clicking on same row deselect the row
      assert.equal(find(findAll('.rsa-data-table-body-row')[2]).classList.contains('is-selected'), false, '2nd row is selected after click');
    });
  });


});
