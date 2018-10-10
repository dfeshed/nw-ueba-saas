import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import users from '../../../../../../data/presidio/user-list';

module('Integration | Component | users-tab/body/list/alerts', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  test('it renders', async function(assert) {
    this.set('userId', 'test');
    this.set('alerts', users.data[0].alerts);
    await render(hbs `{{users-tab/body/list/alerts userId=userId alerts=alerts}}`);
    assert.equal(find('.rsa-content-tethered-panel-trigger').textContent.replace(/\s/g, ''), '3Alerts');
  });
});