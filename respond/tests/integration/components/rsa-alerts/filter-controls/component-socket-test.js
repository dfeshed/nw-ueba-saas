import { waitUntil, settled, findAll, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import { alertFilterData } from './data';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { findElement } from '../../../../helpers/find-element';
import { getItems } from 'respond/actions/creators/alert-creators';
import { getAllCategories } from 'respond-shared/actions/creators/create-incident-creators';
import waitForReduxStateChange from '../../../../helpers/redux-async-helpers';
import { clickTrigger } from 'ember-power-select/test-support/helpers';

module('Integration | Component | Respond Alerts Filters Socket', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('getItems should fetch alert names again after batch completed', async function(assert) {
    patchReducer(this, Immutable.from(alertFilterData));
    const redux = this.owner.lookup('service:redux');

    redux.dispatch(getAllCategories());
    await waitForReduxStateChange(redux, 'respondShared.createIncident.categoryTags');

    await render(hbs`{{rsa-alerts}}`);

    const optionSelector = '.ember-power-select-dropdown .ember-power-select-options .ember-power-select-option';
    await clickTrigger('.alert-name-filter');
    await waitUntil(() => findElement(optionSelector, 'Toran Alert').length === 1, { timeout: 5000 });

    assert.equal(findAll(optionSelector).length, 3, 'There should be 3 alert names to filter by');

    redux.dispatch(getItems());
    await settled();

    await waitUntil(() => findAll(optionSelector).length === 30, { timeout: 5000 });
    assert.equal(findAll(optionSelector).length, 30, 'There should be 30 alert names to filter by');
  });
});
