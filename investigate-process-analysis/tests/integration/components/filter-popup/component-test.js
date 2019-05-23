import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, click, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

const selectors = {
  selectedTab: '.process-filter-popup .rsa-nav-tab.is-active',
  networkTab: '.process-filter-popup .rsa-nav-tab.is-active i.rsa-icon-network',
  viewAll: '[test-id=view-all]',
  viewSelected: '[test-id=view-selected]',
  cancelPopup: '[test-id=cancel-popup]',
  selectedProcessCount: '[test-id=selected-process-count]',
  processList: '.process-filter-popup__content .rsa-data-table-body-row',
  checkAll: '.process-filter-popup__content .rsa-data-table-header-cell .rsa-form-checkbox'
};

const processData = {
  node: {},
  children: [
    {
      data: { processName: 'p1' },
      selected: true
    },
    {
      data: { processName: 'p2' }
    }
  ]
};

module('Integration | Component | filter-popup', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
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
    assert.equal(find(selectors.selectedTab).textContent.trim(), 'All', 'By default All tab is selected');
    assert.equal(findAll(selectors.processList).length, 2, '2 processes are displayed');
    assert.equal(find(selectors.selectedProcessCount).textContent.trim(), '1 Process selected', 'Selected process count is 1');
    await click(find(selectors.viewSelected));
  });

  test('on clicking network tab it is selected', async function(assert) {
    this.set('model', processData);
    await render(hbs`{{filter-popup model=model}}`);
    await click(findAll('.process-filter-popup .rsa-nav-tab')[1]);
    assert.equal(findAll(selectors.networkTab).length, 1, 'network tab is selected');
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
