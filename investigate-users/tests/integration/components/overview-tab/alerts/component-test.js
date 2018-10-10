import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import alertOverview from '../../../../data/presidio/alert_overview';

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
    assert.equal(find('.user-overview-tab_title').textContent.trim(), 'Top Alerts');
  });

  test('it should show proper count for alerts', async function(assert) {
    new ReduxDataHelper(setState).topAlerts(alertOverview.data).build();
    await render(hbs `{{overview-tab/alerts}}`);
    assert.equal(findAll('.user-overview-tab_upper_alerts_container_pill').length, 10);
  });
});