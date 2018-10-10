import { find, render, findAll } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | user-container/body', function(hooks) {
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

  test('it should render user-container/body for overview', async function(assert) {
    await render(hbs`{{user-container/body}}`);
    assert.equal(find('.user-overview-tab_upper_users').textContent.trim(), 'High Risk Users');
    assert.equal(find('.user-overview-tab_upper_alerts').textContent.trim(), 'Top Alerts');
    assert.equal(find('.user-overview-tab_lower_users').textContent.replace(/\s/g, ''), 'AllUsers0RiskyUsers0Watched0Admin');
    assert.equal(find('.user-overview-tab_lower_alerts').textContent.trim(), 'All Alerts');
  });

  test('it should render user-container/body for users', async function(assert) {
    new ReduxDataHelper(setState).activeTab('users').build();
    await render(hbs`{{user-container/body}}`);
    assert.equal(findAll('.users-tab_filter_options').length, 1);
  });

  test('it should render user-container/body for alerts', async function(assert) {
    new ReduxDataHelper(setState).activeTab('alerts').build();
    await render(hbs`{{user-container/body}}`);
    assert.equal(findAll('.alerts-tab').length, 1);
  });

});
