import { click, findAll, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import {
  getAllAlertTypes,
  getAllAlertSources,
  getAllAlertNames } from 'respond/actions/creators/dictionary-creators';
import RSVP from 'rsvp';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../helpers/vnext-patch';

let init, setState;

module('Integration | Component | Respond Alerts Filters', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    setState = (state = {}) => {
      const fullState = { respond: { incidents: state } };
      patchReducer(this, Immutable.from(fullState));
      const redux = this.owner.lookup('service:redux');
      // initialize all of the required data into redux app state
      init = RSVP.allSettled([
        redux.dispatch(getAllAlertTypes()),
        redux.dispatch(getAllAlertSources()),
        redux.dispatch(getAllAlertNames())
      ]);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The Alerts Filters component renders to the DOM', async function(assert) {
    assert.expect(1);
    setState();
    await init;
    this.set('updateFilter', function() {});
    await render(hbs`{{rsa-alerts/filter-controls updateFilter=(action updateFilter)}}`);
    assert.ok(findAll('.filter-option').length >= 1, 'The Alerts Filters component should be found in the DOM');
  });

  test('All of the alert type filters appear as checkboxes, and clicking one dispatches an action', async function(assert) {
    assert.expect(2);
    setState();
    await init;
    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-alerts/filter-controls updateFilter=(action updateFilter)}}`);
    const selector = '.filter-option.alert-type-filter .rsa-form-checkbox-label';
    assert.equal(findAll(selector).length, 10, 'There should be 10 alert type filter options');
    await click('.filter-option.alert-type-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first-of-type');
  });

  test('All of the alert source filters appear as checkboxes, and clicking one dispatches an action', async function(assert) {
    assert.expect(2);
    setState();
    await init;
    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-alerts/filter-controls updateFilter=(action updateFilter)}}`);
    const selector = '.filter-option.alert-source-filter .rsa-form-checkbox-label';
    assert.equal(findAll(selector).length, 6, 'There should be 6 alert source filter options');
    await click('.filter-option.alert-source-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first-of-type');
  });

  test('All of the part-of-incident filter options appear as checkboxes, and clicking one dispatches an action', async function(assert) {
    assert.expect(2);
    setState();
    await init;
    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-alerts/filter-controls updateFilter=(action updateFilter)}}`);

    const selector = '.filter-option.part-of-incident-filter .rsa-form-checkbox-label';
    assert.equal(findAll(selector).length, 2, 'There should be 2 part-of-incident filter options');
    await click('.filter-option.part-of-incident-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first-of-type');
  });

  test('The severity slider filter appears in the DOM', async function(assert) {
    setState();
    await init;
    await render(hbs`{{rsa-alerts/filter-controls}}`);
    const selector = '.filter-option.severity-filter .noUi-tooltip';
    assert.equal(findAll(selector).length, 2, 'The are two tooltips');
    assert.equal(findAll(selector)[0].textContent.trim(), '0', 'The left end slider value should be 0');
    assert.equal(findAll(selector)[1].textContent.trim(), '100', 'The right end slider value should be 100');
  });

  test('All of the alert name filters appear as checkboxes, and clicking one dispatches an action', async function(assert) {
    assert.expect(2);
    setState();
    await init;
    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-alerts/filter-controls updateFilter=(action updateFilter)}}`);
    const selector = '.filter-option.alert-name-filter .rsa-form-checkbox-label';
    // lazy rendering of the list means we cannot assert that the full set of alert names are present
    assert.ok(findAll(selector).length >= 1, 'There should be at least one alert name filter options');
    await click('.filter-option.alert-name-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first-of-type');
  });
});
