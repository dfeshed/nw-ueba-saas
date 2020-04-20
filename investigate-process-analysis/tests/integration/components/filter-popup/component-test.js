import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, click, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import _ from 'lodash';

const selectors = {
  selectedTab: '.process-filter-popup .rsa-nav-tab.is-active',
  networkTab: '.process-filter-popup .rsa-nav-tab.is-active i.rsa-icon-network',
  fileTab: '.process-filter-popup .rsa-nav-tab.is-active i.rsa-icon-common-file-empty',
  registryTab: '.process-filter-popup .rsa-nav-tab.is-active i.rsa-icon-cell-border-bottom',
  viewAll: '[test-id=view-all]',
  viewSelected: '[test-id=view-selected]',
  cancelPopup: '[test-id=cancel-popup]',
  selectedProcessCount: '[test-id=selected-process-count]',
  processList: '.process-filter-popup__content .rsa-data-table-body-row',
  checkAll: '.process-filter-popup__content .rsa-data-table-header-cell .rsa-form-checkbox',
  selectAllChecked: '.process-filter-popup__content .rsa-data-table-header-cell .rsa-form-checkbox.checked',
  noResultsMessage: '.process-filter-popup__content .no-results-message'
};

const data = {
  node: {},
  children: [
    {
      data: { processName: 'p1' },
      selected: true
    },
    {
      data: { processName: 'cmd.exe', eventCategory: { hasNetwork: 0, hasFile: 1, hasRegistry: 0 } }
    },
    {
      data: { processName: 'egui.exe' }
    },
    {
      data: { processName: 'npp.exe', eventCategory: { hasNetwork: 0, hasFile: 1, hasRegistry: 0 } }
    },
    {
      data: { processName: 'OneDrive.exe', eventCategory: { hasNetwork: 1, hasFile: 0, hasRegistry: 0 } }
    },
    {
      data: { processName: 'p2' }
    }
  ]
};
let processData;

module('Integration | Component | filter-popup', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    processData = _.cloneDeep(data);
  });

  test('it renders filter-popup', async function(assert) {
    assert.expect(5);
    this.set('model', processData);
    this.set('onViewSelected', () => {
      assert.ok(true, 'View selected action is called');
    });
    this.set('hidePanel', () => {
      assert.ok(true, 'hidePanel action is called');
    });
    await render(hbs`{{filter-popup model=model onViewSelected=onViewSelected hidePanel=hidePanel}}`);
    assert.equal(find(selectors.selectedTab).textContent.trim(), 'All (6)', 'By default All tab is selected');
    assert.equal(findAll(selectors.processList).length, 6, '6 processes are displayed');
    assert.equal(find(selectors.selectedProcessCount).textContent.trim(), '1 Process selected', 'Selected process count is 1');
    await click(find(selectors.viewSelected));
  });

  test('on clicking network tab it is selected. Verify process count', async function(assert) {
    this.set('model', processData);
    await render(hbs`{{filter-popup model=model}}`);
    await click(findAll('.process-filter-popup .rsa-nav-tab')[1]);
    assert.equal(findAll(selectors.networkTab).length, 1, 'network tab is selected');
    assert.equal(find(selectors.selectedTab).textContent.trim(), 'network (1)', 'process with network event count = 1');
    assert.equal(findAll(selectors.processList).length, 1, '1 process is displayed');
    await click(findAll('.rsa-data-table-body-row .rsa-form-checkbox')[0]);
    assert.equal(find(selectors.selectedProcessCount).textContent.trim(), '2 Process selected', 'Selected process count is 2');
    assert.equal(findAll(selectors.selectAllChecked).length, 1, 'select all is checked');
  });

  test('on clicking file tab it is selected. Verify process count', async function(assert) {
    this.set('model', processData);
    await render(hbs`{{filter-popup model=model}}`);
    await click(findAll('.process-filter-popup .rsa-nav-tab')[2]);
    assert.equal(findAll(selectors.fileTab).length, 1, 'registry tab is selected');
    assert.equal(find(selectors.selectedTab).textContent.trim(), 'file (2)', 'process with file events count = 2');
    await click(findAll('.rsa-data-table-body-row .rsa-form-checkbox')[0]);
    assert.equal(find(selectors.selectedProcessCount).textContent.trim(), '2 Process selected', 'Selected process count is 2, one in all and one in file tab');
    assert.equal(findAll(selectors.processList).length, 2, '2 processes are displayed');
    assert.equal(findAll(selectors.selectAllChecked).length, 0, 'select all is not checked');
  });

  test('on clicking registry tab it is selected. Verify process count', async function(assert) {
    this.set('model', processData);
    await render(hbs`{{filter-popup model=model}}`);
    await click(findAll('.process-filter-popup .rsa-nav-tab')[3]);
    assert.equal(findAll(selectors.registryTab).length, 1, 'registry tab is selected');
    assert.equal(find(selectors.selectedTab).textContent.trim(), 'registry (0)', 'process with registry events count = 0');
    assert.equal(findAll(selectors.processList).length, 0, '0 process are displayed');
    assert.equal(find(selectors.noResultsMessage).textContent.trim(), 'No Results');
    assert.equal(findAll(selectors.selectAllChecked).length, 0, 'select all is not checked');
  });

  test('on View All expected actions are called', async function(assert) {
    this.set('model', processData);
    assert.expect(2);
    this.set('onView', () => {
      assert.ok(true, 'View action is called');
    });
    this.set('hidePanel', () => {
      assert.ok(true, 'hidePanel action is called');
    });
    await render(hbs`{{filter-popup model=model onView=onView hidePanel=hidePanel}}`);
    await click(selectors.viewAll);
  });

  test('when no process selected, view selected is disabled', async function(assert) {
    this.set('model', processData);
    assert.expect(2);
    this.set('onViewSelected', () => {
      assert.ok(true, 'View selected action is called');
    });
    this.set('hidePanel', () => { });
    await render(hbs`{{filter-popup model=model onViewSelected=onViewSelected hidePanel=hidePanel}}`);
    await click(selectors.viewSelected);
    await click(selectors.checkAll); // selects all checkbox
    await click(selectors.checkAll); // unselects all checkbox
    await click(selectors.viewSelected); // onViewSelected is not called
  });

  test('on cancel expected actions are called', async function(assert) {
    this.set('model', processData);
    assert.expect(1);
    this.set('hidePanel', () => {
      assert.ok(true, 'hidePanel action is called');
    });
    await render(hbs`{{filter-popup model=model hidePanel=hidePanel}}`);
    await click(selectors.cancelPopup);
  });
});
