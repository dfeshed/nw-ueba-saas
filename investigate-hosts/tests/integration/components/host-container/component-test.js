import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, find } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';

let setState;

module('Integration | Component | host-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders host container', async function(assert) {
    await render(hbs`{{host-container}}`);
    assert.equal(findAll('.host-container').length, 1, 'host container rendered');
  });

  test('it renders filter panel and center container', async function(assert) {
    await render(hbs`{{host-container}}`);
    assert.equal(findAll('.rsa-data-filters').length, 1, 'filters rendered');
    assert.equal(findAll('.center-zone').length, 1, 'center content rendered');
  });

  test('it renders host container', async function(assert) {
    await render(hbs`{{host-container}}`);
    assert.equal(findAll('.host-container').length, 1, 'host container rendered');
  });

  test('it renders host container detail', async function(assert) {
    new ReduxDataHelper(setState)
      .hasMachineId(true)
      .build();
    await render(hbs`{{host-container}}`);
    assert.equal(findAll('.host-container-detail').length, 1, 'host container detail rendered');
  });

  test('it renders host container list', async function(assert) {
    new ReduxDataHelper(setState)
      .hasMachineId(false)
      .build();
    await render(hbs`{{host-container}}`);
    assert.equal(findAll('.host-container-list').length, 1, 'host container list rendered');
  });

  test('risk score filter is rendered', async function(assert) {
    new ReduxDataHelper(setState)
      .hasMachineId(false)
      .build();
    await render(hbs`{{host-container}}`);
    assert.equal(findAll('.filter-controls .range-filter').length, 1, 'Range filter (score) is present');
    assert.equal(find('.filter-controls .range-filter .filter-text').textContent, 'Risk Score', 'Filter name is Risk Score');
  });
});
