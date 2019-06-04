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
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';

let setState;
module('Integration | Component | overview-tab/user', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      patchReducer(this, state);
    };
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
    new ReduxDataHelper(setState).build();
    await render(hbs `{{overview-tab/user}}`);
    assert.equal(findAll('.user-overview-tab_upper_users_row').length, 5);
    assert.equal(findAll('.rsa-icon-view-1-filled').length, 1);
  });

  test('it should show loader till data is not there', async function(assert) {
    new ReduxDataHelper(setState).topUsers([]).build();
    await render(hbs `{{overview-tab/user}}`);
    assert.equal(findAll('.rsa-loader').length, 1);
    assert.equal(findAll('.center').length, 1);
  });

  test('it should show error for server issues', async function(assert) {
    new ReduxDataHelper(setState).topUsers([]).topUsersError('Error').build();
    await render(hbs `{{overview-tab/user}}`);
    assert.equal(findAll('.rsa-loader').length, 0);
    assert.equal(findAll('.center').length, 1);
  });

  test('it should open entity details', async function(assert) {
    const redux = this.owner.lookup('service:redux');
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