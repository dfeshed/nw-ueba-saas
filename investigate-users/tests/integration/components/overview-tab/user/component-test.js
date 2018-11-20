import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import waitForReduxStateChange from '../../../../helpers/redux-async-helpers';
import { getUserOverview } from 'investigate-users/actions/user-details';
import { patchFetch } from '../../../../helpers/patch-fetch';
import { Promise } from 'rsvp';
import dataIndex from '../../../../data/presidio';

let redux;

module('Integration | Component | overview-tab/user', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
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
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders', async function(assert) {
    await render(hbs `{{overview-tab/user}}`);
    assert.equal(find('.user-overview-tab_title').textContent.trim(), 'High Risk Users');
  });

  test('it should show proper count', async function(assert) {
    redux.dispatch(getUserOverview());
    await render(hbs `{{overview-tab/user}}`);
    assert.equal(findAll('.user-overview-tab_upper_users_row').length, 5);
    assert.equal(findAll('.rsa-icon-account-group-5-filled').length, 1);
  });

  test('it should open entity details', async function(assert) {
    redux.dispatch(getUserOverview());
    await render(hbs `{{overview-tab/user}}`);
    click('.user-overview-tab_upper_users_row');
    const select = waitForReduxStateChange(redux, 'user.userId');
    return select.then(() => {
      const state = redux.getState();
      assert.equal(state.user.userId, '46b6b6ad-6995-4840-bc65-908e1b1d0856');
      assert.equal(state.user.alertId, null);
      assert.equal(state.user.indicatorId, null);
    });
  });
});