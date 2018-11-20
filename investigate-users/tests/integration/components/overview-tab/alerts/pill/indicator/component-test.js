import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, settled, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import alertOverview from '../../../../../../data/presidio/alert_overview';
import waitForReduxStateChange from '../../../../../../helpers/redux-async-helpers';


module('Integration | Component | overview-tab/alerts/pill/indicator', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  test('it renders', async function(assert) {
    this.set('indicators', alertOverview.data[0].evidences);
    this.set('userId', alertOverview.data[0].entityId);
    this.set('alertId', alertOverview.data[0].id);
    await render(hbs `{{overview-tab/alerts/pill/indicator userId=userId alertId=alertId indicators=indicators}}`);
    assert.equal(find('.rsa-content-tethered-panel-trigger').textContent.replace(/\s/g, ''), '17Indicators');
  });

  test('it should open entity details on click of any indicator ID', async function(assert) {
    const redux = this.owner.lookup('service:redux');
    this.set('indicators', alertOverview.data[0].evidences);
    this.set('userId', alertOverview.data[0].entityId);
    this.set('alertId', alertOverview.data[0].id);
    await render(hbs `{{overview-tab/alerts/pill/indicator userId=userId alertId=alertId indicators=indicators}} <div id='modalDestination'></div>`);
    await this.$().find('.rsa-content-tethered-panel-trigger').mouseenter();
    return settled().then(() => {
      assert.ok(find('.user_alert_indicator_panel').textContent.replace(/\s/g, '').indexOf('MultipleActiveDirectory') === 0);
      click('.user_alert_indicator_panel > ul > li');
      const select = waitForReduxStateChange(redux, 'user.indicatorId');
      return select.then(() => {
        const state = redux.getState();
        assert.equal(state.user.userId, 'a0979b0c-7214-4a53-8114-c1552aa0952c');
        assert.equal(state.user.alertId, '5090a7fc-1218-4b74-b05a-6b197601d18d');
        assert.equal(state.user.indicatorId, '07ce09d8-9f43-4d8a-aad0-f955c1bb413f');
      });
    });

  });
});