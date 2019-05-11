import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, click, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

const selectors = {
  selectedTab: '.process-filter-popup .rsa-nav-tab.is-active',
  networkTab: '.process-filter-popup .rsa-nav-tab.is-active i.rsa-icon-network',
  viewAll: '.process-filter-popup__footer .rsa-form-button'
};

module('Integration | Component | filter-popup', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-process-analysis')
  });

  test('it renders filter-popup', async function(assert) {
    await render(hbs`{{filter-popup}}`);
    assert.equal(find(selectors.selectedTab).textContent.trim(), 'All', 'By default All tab is selected');
  });

  test('on clicking network tab it is selected', async function(assert) {
    await render(hbs`{{filter-popup}}`);
    await click(findAll('.process-filter-popup .rsa-nav-tab')[1]);
    assert.equal(findAll(selectors.networkTab).length, 1, 'network tab is selected');
  });

  test('on View All expected actions are called', async function(assert) {
    assert.expect(2);
    this.set('onView', () => {
      assert.ok(true, 'View action is called');
    });
    this.set('hidePanel', () => {
      assert.ok(true, 'hidePanel action is called');
    });
    await render(hbs`{{filter-popup onView=onView hidePanel=hidePanel}}`);
    await click(selectors.viewAll);
  });
});
