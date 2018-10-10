import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import alertOverview from '../../../../../../data/presidio/alert_overview';


module('Integration | Component | overview-tab/alerts/pill/indicator', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  test('it renders', async function(assert) {
    this.set('indicators', alertOverview.data[0].evidences);
    this.set('userId', alertOverview.data[0].entityId);
    this.set('alertId', alertOverview.data[0].id);
    await render(hbs `{{overview-tab/alerts/pill/indicator userId=userId alertId=alertId indicators=indicators}} <div id='modalDestination'></div>`);
    assert.equal(find('.rsa-content-tethered-panel-trigger').textContent.replace(/\s/g, ''), '17Indicators');
    await this.$().find('.rsa-content-tethered-panel-trigger').mouseenter();
    return settled().then(() => {
      assert.ok(find('.user_alert_indicator_panel').textContent.replace(/\s/g, '').indexOf('MultipleActiveDirectory') === 0);
    });

  });
});