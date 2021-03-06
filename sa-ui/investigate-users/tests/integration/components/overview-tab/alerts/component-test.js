import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import alertOverview from '../../../../data/presidio/alert_overview';
import { clickTrigger } from 'ember-power-select/test-support/helpers';

let setState;

module('Integration | Component | overview-tab/alerts', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders', async function(assert) {
    await render(hbs `{{overview-tab/alerts}}`);
    assert.equal(findAll('.user-overview-tab_title_title').length, 1);
    assert.ok(find('.user-overview-tab_title').textContent.trim().indexOf('Top Alerts') > -1);
  });

  test('it should show proper count for alerts', async function(assert) {
    new ReduxDataHelper(setState).topAlerts(alertOverview.data).build();
    await render(hbs `{{overview-tab/alerts}}`);
    assert.equal(findAll('.user-overview-tab_alerts_alerts_container_pill').length, 10);
  });
  test('it should show loader till data is not there', async function(assert) {
    new ReduxDataHelper(setState).topAlerts([]).build();
    await render(hbs `{{overview-tab/alerts}}`);
    assert.equal(findAll('.rsa-loader').length, 1);
    assert.equal(findAll('.center').length, 1);
  });

  test('it should show error in case of error', async function(assert) {
    new ReduxDataHelper(setState).topAlerts([]).topAlertsError('Error').build();
    await render(hbs `{{overview-tab/alerts}}`);
    assert.equal(findAll('.rsa-loader').length, 0);
    assert.equal(findAll('.center').length, 1);
  });

  test('it should show quick filter', async function(assert) {
    await render(hbs `{{overview-tab/alerts}}`);
    assert.equal(findAll('.user-overview-tab_alerts_entityType').length, 1);
    assert.equal(findAll('.user-overview-tab_alerts_entityTimeframe').length, 1);
    assert.equal(findAll('.ember-power-select-trigger').length, 2);
    await clickTrigger('.user-overview-tab_alerts_entityType');
    assert.equal(findAll('.ember-power-select-option').length, 4);
  });
});