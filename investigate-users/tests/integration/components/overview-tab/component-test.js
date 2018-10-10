import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | overview-tab', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  test('it renders', async function(assert) {
    await render(hbs`{{overview-tab}}`);
    assert.equal(find('.user-overview-tab_upper_users').textContent.trim(), 'High Risk Users');
    assert.equal(find('.user-overview-tab_upper_alerts').textContent.trim(), 'Top Alerts');
    assert.equal(find('.user-overview-tab_lower_users').textContent.replace(/\s/g, ''), 'AllUsers0RiskyUsers0Watched0Admin');
    assert.equal(find('.user-overview-tab_lower_alerts').textContent.trim(), 'All Alerts');
  });
});
