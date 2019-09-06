import { find, findAll, click, render, waitUntil } from '@ember/test-helpers';
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
    assert.equal(findAll('.alerts-tab_body_body-table_body_row_date').length, 2);
  });

  test('it should render alert tab body should show alerts', async function(assert) {
    assert.expect(1);
    redux.dispatch(getAlertsForGivenTimeInterval());
    await render(hbs`{{alerts-tab/body/alerts-table}}`);
    assert.equal(findAll('.alerts-tab_body_body-table_body_row_date_icon').length, 12);
  });

  test('it should render alert tab body should show indicators inside alerts', async function(assert) {
    redux.dispatch(getAlertsForGivenTimeInterval());
    const done = assert.async();
    await render(hbs`{{alerts-tab/body/alerts-table}}`);
    click('.alerts-tab_body_body-table_body_row_date');
    return waitUntil(() => document.querySelectorAll('.alerts-tab_body_body-table_body_row_alerts').length === 1, { timeout: 30000 }).then(async() => {
      await click('.alerts-tab_body_body-table_body_row_alerts_alert');
      later(() => {
        assert.equal(findAll('.rsa-data-table-body-row').length, 4);
        click('.rsa-data-table-body-row');
        const select = waitForReduxStateChange(redux, 'user.indicatorId');
        return select.then(() => {
          const state = redux.getState();
          assert.equal(state.user.userId, 'c291d9ad-d056-42f9-9f0c-f3a40517a392');
          assert.equal(state.user.alertId, '513c7308-b3f6-4a80-a3ba-27896fd79b9b');
          assert.equal(state.user.indicatorId, 'ddc95b75-b55c-4116-abb2-5c0922205bec');
          done();
        });
      }, 500);
    });
  });
});