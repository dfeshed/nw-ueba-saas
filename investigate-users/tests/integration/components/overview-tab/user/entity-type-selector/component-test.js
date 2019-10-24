import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import waitForReduxStateChange from '../../../../../helpers/redux-async-helpers';
import { updateEntityType } from 'investigate-users/actions/user-details';

let setState;
module('Integration | Component | overview-tab/user/entity-type-selector', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('it should render user and network button', async function(assert) {
    await render(hbs`{{overview-tab/user/entity-type-selector}}`);
    assert.equal(findAll('.user-overview-tab_users_entities').length, 1);
    assert.equal(findAll('.user-overview-tab_users_entities_network').length, 1);
    assert.equal(findAll('.rsa-form-button-wrapper').length, 2);
  });

  test('it should show ja3 and sslSubject on network click', async function(assert) {
    await render(hbs`{{overview-tab/user/entity-type-selector}}`);
    await click('.user-overview-tab_users_entities_network > div > button');
    assert.equal(findAll('.rsa-form-button-wrapper').length, 4);
  });

  test('it should show initial state', async function(assert) {
    new ReduxDataHelper(setState).build();
    await render(hbs`{{overview-tab/user/entity-type-selector}}`);
    assert.equal(findAll('.selected.rsa-form-button-wrapper').length, 1);
  });

  test('it should show ja3 selected', async function(assert) {
    new ReduxDataHelper(setState).build();
    const redux = this.owner.lookup('service:redux');
    redux.dispatch(updateEntityType('userId'));
    const select = waitForReduxStateChange(redux, 'users.filter.entityType');
    await render(hbs`{{overview-tab/user/entity-type-selector}}`);
    await click('.user-overview-tab_users_entities_network > div > button');
    assert.equal(findAll('.rsa-form-button-wrapper').length, 4);
    await click('.user-overview-tab_users_entities_network:nth-child(2) > div:nth-child(2) > button');
    return select.then(() => {
      const state = redux.getState();
      assert.equal(state.users.filter.entityType, 'ja3');
    });
  });

  test('it should show ja3 selected', async function(assert) {
    new ReduxDataHelper(setState).build();
    const redux = this.owner.lookup('service:redux');
    redux.dispatch(updateEntityType('userId'));
    const select = waitForReduxStateChange(redux, 'users.filter.entityType');
    await render(hbs`{{overview-tab/user/entity-type-selector}}`);
    await click('.user-overview-tab_users_entities_network > div > button');
    assert.equal(findAll('.rsa-form-button-wrapper').length, 4);
    await click('.user-overview-tab_users_entities_network:nth-child(2) > div:nth-child(3) > button');
    return select.then(() => {
      const state = redux.getState();
      assert.equal(state.users.filter.entityType, 'sslSubject');
    });
  });
});
