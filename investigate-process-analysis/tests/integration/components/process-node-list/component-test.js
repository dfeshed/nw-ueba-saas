import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import _ from 'lodash';

const data = [
  {
    data: {
      processName: 'malware.exe',
      riskScore: 100,
      machineCount: 10,
      selected: false
    }
  },
  {
    data: {
      processName: 'chrome.exe',
      riskScore: 70,
      machineCount: 1,
      selected: false
    }
  },
  {
    data: {
      processName: 'virus.exe',
      riskScore: 90,
      machineCount: 3,
      selected: false
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
    assert.strictEqual(document.querySelectorAll('.rsa-data-table-header-cell').length, 4, '4 columns are rendered');
    assert.strictEqual(document.querySelectorAll('.rsa-data-table-body-row').length, 3, '3 rows are rendered');
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

  test('risk score component is rendered', async function(assert) {
    this.set('nodeList', testData);
    await render(hbs`{{process-node-list nodeList=nodeList}}`);
    assert.strictEqual(document.querySelectorAll('.rsa-risk-score').length, 3, 'risk score is rendered');
  });

  test('on clicking the process name sorts the table', async function(assert) {
    this.set('nodeList', testData);
    this.set('currentSort', {
      field: 'data.processName',
      direction: 'desc'
    });
    await render(hbs`{{process-node-list nodeList=nodeList currentSort=currentSort}}`);
    assert.strictEqual(document.querySelectorAll('.rsa-data-table-header-cell.is-sorted .rsa-icon-arrow-down-7-filled').length, 1, 'Default arrow up icon before sorting');
    assert.strictEqual(document.querySelector('.process-node-list .rsa-data-table-body-row:nth-child(1) .rsa-data-table-body-cell:nth-child(2)').textContent.trim(), 'malware.exe');
    await click(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('.rsa-icon'));
    assert.strictEqual(document.querySelector('.process-node-list .rsa-data-table-body-row:nth-child(1) .rsa-data-table-body-cell:nth-child(2)').textContent.trim(), 'virus.exe');
    await click(document.querySelectorAll('.rsa-data-table-header-cell')[1].querySelector('.rsa-icon'));
    assert.strictEqual(document.querySelector('.process-node-list .rsa-data-table-body-row:nth-child(1) .rsa-data-table-body-cell:nth-child(2)').textContent.trim(), 'chrome.exe');
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

});
