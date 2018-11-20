import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import waitForReduxStateChange from '../../../../../helpers/redux-async-helpers';
import { getUsers } from 'investigate-users/actions/user-tab-actions';
import { patchFetch } from '../../../../../helpers/patch-fetch';
import { Promise } from 'rsvp';
import dataIndex from '../../../../../data/presidio';

let redux;

module('Integration | Component | users-tab/body/list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    redux = this.owner.lookup('service:redux');
    patchFetch((url) => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return dataIndex(url);
          }
        });
      });
    });
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders', async function(assert) {
    await render(hbs`{{users-tab/body/list}}`);
    assert.equal(findAll('.rsa-data-table').length, 1);
  });

  test('it renders with proper user data', async function(assert) {
    redux.dispatch(getUsers());
    await render(hbs`{{users-tab/body/list}}`);
    assert.equal(findAll('.rsa-data-table-body-row').length, 2);
    assert.equal(find('.rsa-data-table-body-rows').textContent.replace(/\s/g, ''), '45auth_qa_1_23Alerts40qa_ad_contains_static2Alerts');
  });

  test('it should open entity details on user click', async function(assert) {
    redux.dispatch(getUsers());
    await render(hbs`{{users-tab/body/list}}`);
    click('.rsa-data-table-body-row');
    const select = waitForReduxStateChange(redux, 'user.userId');
    return select.then(() => {
      const state = redux.getState();
      assert.equal(state.user.userId, '247a0375-8eb7-4e55-bc1a-8fc16befa035');
      assert.equal(state.user.alertId, null);
      assert.equal(state.user.indicatorId, null);
    });
  });
});
