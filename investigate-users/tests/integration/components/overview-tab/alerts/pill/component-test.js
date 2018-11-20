import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import alertOverview from '../../../../../data/presidio/alert_overview';
import waitForReduxStateChange from '../../../../../helpers/redux-async-helpers';

module('Integration | Component | overview-tab/alerts/pill', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  test('it renders', async function(assert) {
    this.set('alert', alertOverview.data[0]);
    await render(hbs `{{overview-tab/alerts/pill alert=alert}}`);
    assert.ok(find('.user-overview-tab_upper_alerts_container_pill').textContent.replace(/\s/g, '').indexOf('HighAbnormalADChanges|Hourlymixed') === 0);
  });

  test('it should open entity details', async function(assert) {
    assert.expect(3);
    const redux = this.owner.lookup('service:redux');
    this.set('alert', alertOverview.data[0]);
    await render(hbs `{{overview-tab/alerts/pill alert=alert}}`);
    click('.user-overview-tab_upper_alerts_container_pill_rating');
    const select = waitForReduxStateChange(redux, 'user.alertId');
    return select.then(() => {
      const state = redux.getState();
      assert.equal(state.user.userId, 'a0979b0c-7214-4a53-8114-c1552aa0952c');
      assert.equal(state.user.alertId, '5090a7fc-1218-4b74-b05a-6b197601d18d');
      assert.equal(state.user.indicatorId, null);
    });
  });
});