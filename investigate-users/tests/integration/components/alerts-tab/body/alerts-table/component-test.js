import { find, findAll, click, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { later } from '@ember/runloop';
import { patchFetch } from '../../../../../helpers/patch-fetch';
import dataIndex from '../../../../../data/presidio';
import { Promise } from 'rsvp';
import { getAlertsForGivenTimeInterval } from 'investigate-users/actions/alert-details';
import waitForReduxStateChange from '../../../../../helpers/redux-async-helpers';

let redux;

module('Integration | Component | alerts-tab/body/alerts-table', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
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
  });

  test('it should render alert tab table body with loading', async function(assert) {
    await render(hbs`{{alerts-tab/body/alerts-table}}`);
    assert.equal(find('.alerts-tab_body_body-table_header').textContent.replace(/\s/g, ''), 'AlertNameEntityNameStartTimeIndicatorCountFeedback');
    assert.equal(find('.rsa-loader__text').textContent.replace(/\s/g, ''), 'Loading');
  });

  test('it should render alert tab body with data', async function(assert) {
    redux.dispatch(getAlertsForGivenTimeInterval());
    await render(hbs`{{alerts-tab/body/alerts-table}}`);
    assert.equal(findAll('.alerts-tab_body_body-table_body_row_date').length, 7);
  });

  test('it should render alert tab body should show alerts', async function(assert) {
    redux.dispatch(getAlertsForGivenTimeInterval());
    await render(hbs`{{alerts-tab/body/alerts-table}}`);
    click('.alertName > span');
    const select = waitForReduxStateChange(redux, 'user.alertId');
    return select.then(() => {
      const state = redux.getState();
      assert.equal(state.user.userId, 'a0979b0c-7214-4a53-8114-c1552aa0952c');
      assert.equal(state.user.alertId, '5090a7fc-1218-4b74-b05a-6b197601d18d');
      assert.equal(state.user.indicatorId, null);
    });
  });

  test('it should render alert tab body should show indicators inside alerts', async function(assert) {
    redux.dispatch(getAlertsForGivenTimeInterval());
    const done = assert.async();
    await render(hbs`{{alerts-tab/body/alerts-table}}`);
    click('.alerts-tab_body_body-table_body_row_date');
    await click('.alerts-tab_body_body-table_body_row_alerts_alert');
    later(() => {
      assert.equal(findAll('.rsa-data-table-body-row').length, 71);
      click('.rsa-data-table-body-row');
      const select = waitForReduxStateChange(redux, 'user.indicatorId');
      return select.then(() => {
        const state = redux.getState();
        assert.equal(state.user.userId, 'a0979b0c-7214-4a53-8114-c1552aa0952c');
        assert.equal(state.user.alertId, '5090a7fc-1218-4b74-b05a-6b197601d18d');
        assert.equal(state.user.indicatorId, '07ce09d8-9f43-4d8a-aad0-f955c1bb413f');
        done();
      });
    }, 500);
  });
});