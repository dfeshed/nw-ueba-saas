import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, waitUntil, click, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import users from '../../../../../../data/presidio/user-list';
import waitForReduxStateChange from '../../../../../../helpers/redux-async-helpers';

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

  test('it should open entity details on click of any alert ID', async function(assert) {
    const redux = this.owner.lookup('service:redux');
    this.set('userId', 'a0979b0c-7214-4a53-8114-c1552aa0952c');
    this.set('alerts', users.data[0].alerts);
    await render(hbs `{{users-tab/body/list/alerts userId=userId alerts=alerts}}`);
    await triggerEvent('.users-tab_body_list_alerts', 'mouseover');
    return waitUntil(() => findAll('.users-tab_body_list_alerts_panel').length === 1, { timeout: 1000000 }).then(() => {
      click('.link');
      const select = waitForReduxStateChange(redux, 'user.userId');
      return select.then(() => {
        const state = redux.getState();
        assert.equal(state.user.userId, 'a0979b0c-7214-4a53-8114-c1552aa0952c');
        assert.equal(state.user.alertId, 'b777c6a4-91ea-4784-8689-74506270f10c');
      });
    });

  });
});