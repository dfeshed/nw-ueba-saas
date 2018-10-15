import { find, findAll, click, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import alertsList from '../../../../../data/presidio/alerts-list';
import { later } from '@ember/runloop';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchFetch } from '../../../../../helpers/patch-fetch';
import dataIndex from '../../../../../data/presidio';
import { Promise } from 'rsvp';

let setState;

module('Integration | Component | alerts-tab/body/alerts-table', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
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
    assert.equal(find('.alerts-tab_body_body-table_header').textContent.replace(/\s/g, ''), 'AlertNameEntityNameStartTimeIndicatorCountStatus');
    assert.equal(find('.rsa-loader__text').textContent.replace(/\s/g, ''), 'Loading');
  });

  test('it should render alert tab body with data', async function(assert) {
    new ReduxDataHelper(setState).alertsListdata(alertsList.data).build();
    await render(hbs`{{alerts-tab/body/alerts-table}}`);
    assert.equal(findAll('.alerts-tab_body_body-table_body_row_date').length, 2);
  });

  test('it should render alert tab body should show alerts', async function(assert) {
    new ReduxDataHelper(setState).alertsListdata(alertsList.data).build();
    await render(hbs`{{alerts-tab/body/alerts-table}}`);
    assert.ok(find('.alerts-tab_body_body-table_body_row_alerts_alert').textContent.replace(/\s/g, '').indexOf('Highnon_standard_hours|Hourlyad') === 0);
  });

  test('it should render alert tab body should show indicators inside alerts', async function(assert) {
    new ReduxDataHelper(setState).alertsListdata(alertsList.data).build();
    const done = assert.async();
    await render(hbs`{{alerts-tab/body/alerts-table}}`);
    click('.alerts-tab_body_body-table_body_row_date');
    assert.ok(find('.alerts-tab_body_body-table_body_row_alerts_alert').textContent.replace(/\s/g, '').indexOf('Highnon_standard_hours|Hourlyad') === 0);
    await click('.alerts-tab_body_body-table_body_row_alerts_alert');
    later(() => {
      assert.ok(findAll('.severity')[2].title.indexOf(93) > -1);
      assert.equal(findAll('.rsa-data-table-body-row').length, 16);
      done();
    }, 500);
  });
});