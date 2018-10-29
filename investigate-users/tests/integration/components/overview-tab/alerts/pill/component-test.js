import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import alertOverview from '../../../../../data/presidio/alert_overview';


module('Integration | Component | overview-tab/alerts/pill', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });

  test('it renders', async function(assert) {
    this.set('alert', alertOverview.data[0]);
    await render(hbs `{{overview-tab/alerts/pill alert=alert}}`);
    assert.ok(find('.user-overview-tab_upper_alerts_container_pill').textContent.replace(/\s/g, '').indexOf('HighAbnormalADChanges|Hourlymixed') === 0);
  });
});