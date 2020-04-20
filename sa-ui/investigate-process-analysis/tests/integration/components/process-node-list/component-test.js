import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import _ from 'lodash';

const data = [
  {
    data: {
      processName: 'malware.exe',
      localScore: 90,
      machineCount: 10,
      selected: false,
      eventCategory: {
        hasFile: 0,
        hasNetwork: 1,
        hasRegistry: 0
      }
    }
  },
  {
    data: {
      processName: 'chrome.exe',
      localScore: 70,
      machineCount: 1,
      selected: false,
      eventCategory: {
        hasFile: 1,
        hasNetwork: 1,
        hasRegistry: 0
      }
    }
  },
  {
    data: {
      processName: 'virus.exe',
      localScore: 100,
      machineCount: 3,
      selected: false,
      eventCategory: {
        hasFile: 0,
        hasNetwork: 0,
        hasRegistry: 1
      }
    }
  }
];

let testData;

module('Integration | Component | process-node-list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });
  hooks.beforeEach(function() {
    testData = _.cloneDeep(data);
  });

  test('it renders the process list component', async function(assert) {
    testData.forEach((node) => {
      node.selected = true;
    });
    this.set('nodeList', testData);
    await render(hbs`{{process-node-list nodeList=nodeList}}`);
    assert.strictEqual(document.querySelectorAll('.process-node-list').length, 1, 'Process node list rendered');
    assert.strictEqual(document.querySelectorAll('.rsa-data-table-header-cell .rsa-form-checkbox.checked').length, 1,
      'Select all is checked when all process is checked');
  });

  test('it renders the process list table', async function(assert) {
    this.set('nodeList', testData);
    await render(hbs`{{process-node-list nodeList=nodeList}}`);
    assert.strictEqual(document.querySelectorAll('.rsa-data-table').length, 1, 'Process node list table rendered');
    assert.strictEqual(document.querySelectorAll('.rsa-data-table-header-cell').length, 5, '5 columns are rendered');
    assert.strictEqual(document.querySelectorAll('.rsa-data-table-body-row').length, 3, '3 rows are rendered');
    assert.strictEqual(document.querySelectorAll('.rsa-risk-score')[0].textContent.trim(), '100');
    assert.strictEqual(document.querySelectorAll('.table-footer').length, 1);
  });

  test('it renders the network process list table', async function(assert) {
    this.set('nodeList', testData);
    this.set('activeTab', 'network');
    await render(hbs`{{process-node-list nodeList=nodeList activeTab=activeTab}}`);
    assert.strictEqual(findAll('.rsa-data-table-body-row').length, 2, '2 rows are rendered');
    await click(findAll('.rsa-data-table-body-row .rsa-form-checkbox')[0]);
    assert.equal(findAll('.rsa-data-table-header-cell .rsa-form-checkbox.checked').length, 0, 'select all is not checked');
  });

  test('it renders the file process list table', async function(assert) {
    this.set('nodeList', testData);
    this.set('activeTab', 'file');
    await render(hbs`{{process-node-list nodeList=nodeList activeTab=activeTab}}`);
    assert.strictEqual(findAll('.rsa-data-table-body-row').length, 1, '1 row is rendered');
    await click(findAll('.rsa-data-table-body-row .rsa-form-checkbox')[0]);
    assert.equal(findAll('.rsa-data-table-header-cell .rsa-form-checkbox.checked').length, 1, 'select all is checked');
  });

  test('it renders event category icons', async function(assert) {
    this.set('nodeList', testData);
    await render(hbs`{{process-node-list nodeList=nodeList}}`);
    assert.strictEqual(document.querySelectorAll('.rsa-data-table').length, 1, 'Process node list table rendered');
    assert.strictEqual(document.querySelectorAll('.rsa-data-table-header-cell').length, 5, '5 columns are rendered');
    assert.strictEqual(document.querySelectorAll('.rsa-data-table-body-row .rsa-icon').length, 9, 'category events icons are rendered');
  });

  test('clicking on the header checkbox selects all the row', async function(assert) {
    this.set('nodeList', testData);
    await render(hbs`{{process-node-list nodeList=nodeList}}`);
    assert.strictEqual(document.querySelectorAll('.rsa-data-table').length, 1, 'Process node list table rendered');
    assert.strictEqual(document.querySelectorAll('.rsa-form-checkbox-label.checked').length, 0, 'nothing is selected');
    await click(document.querySelectorAll('.rsa-data-table-header-cell .rsa-form-checkbox')[0]);
    assert.strictEqual(document.querySelectorAll('.rsa-form-checkbox-label.checked').length, 4, '3 rows and 1 header selected');
    assert.strictEqual(document.querySelectorAll('.is-row-checked').length, 3, 'correct css class is added the row selection');

  });

  test('clicking on the header checkbox calls the external function with selection', async function(assert) {
    assert.expect(1);
    this.set('nodeList', testData);
    this.set('onRowSelection', (selections) => {
      assert.strictEqual(selections.length, 3);
    });
    await render(hbs`{{process-node-list nodeList=nodeList onRowSelection=onRowSelection}}`);
    await click(document.querySelectorAll('.rsa-data-table-header-cell .rsa-form-checkbox')[0]);
  });

  test('clicking the process name sorts the table', async function(assert) {
    this.set('nodeList', testData);
    this.set('currentSort', {
      field: 'data.localScore',
      direction: 'desc'
    });
    await render(hbs`{{process-node-list nodeList=nodeList currentSort=currentSort}}`);
    assert.strictEqual(document.querySelectorAll('.rsa-data-table-header-cell .is-sorted .rsa-icon-arrow-down-7').length, 1, 'Default arrow up icon before sorting');
    assert.strictEqual(document.querySelector('.process-node-list .rsa-data-table-body-row:nth-child(1) .rsa-data-table-body-cell:nth-child(2)').textContent.trim(), 'virus.exe');
    await click(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('.rsa-icon'));
    assert.strictEqual(document.querySelector('.process-node-list .rsa-data-table-body-row:nth-child(1) .rsa-data-table-body-cell:nth-child(2)').textContent.trim(), 'chrome.exe');
    await click(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('.rsa-icon'));
    assert.strictEqual(document.querySelector('.process-node-list .rsa-data-table-body-row:nth-child(1) .rsa-data-table-body-cell:nth-child(2)').textContent.trim(), 'virus.exe');
  });

  test('clicking on the checkbox select the row', async function(assert) {
    this.set('nodeList', testData);
    this.set('onRowSelection', (selections) => {
      assert.strictEqual(selections.length, 3);
    });

    await render(hbs`{{process-node-list nodeList=nodeList}}`);
    await click(document.querySelectorAll('.rsa-data-table-body-row .rsa-form-checkbox')[2]);
    assert.strictEqual(document.querySelectorAll('.rsa-form-checkbox-label.checked').length, 1, '1 row selected');
    assert.strictEqual(document.querySelectorAll('.is-row-checked').length, 1, 'correct css class is added the row selection');
  });

  test('clicking on the row selects the checkbox', async function(assert) {
    this.set('nodeList', testData);
    await render(hbs`{{process-node-list nodeList=nodeList}}`);
    await click(document.querySelectorAll('.rsa-data-table-body-row')[2]);
    assert.strictEqual(document.querySelectorAll('.rsa-form-checkbox-label.checked').length, 1, '1 row selected');
    assert.strictEqual(document.querySelectorAll('.is-row-checked').length, 1, 'correct css class is added the row selection');
  });

  test('risk score component is rendered', async function(assert) {
    this.set('nodeList', testData);
    await render(hbs`{{process-node-list nodeList=nodeList}}`);
    assert.strictEqual(document.querySelectorAll('.rsa-risk-score').length, 3, 'risk score is rendered');
  });
});
