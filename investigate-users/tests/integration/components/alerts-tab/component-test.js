import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | alerts-tab', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-users')
  });


  test('it should render alert tab body', async function(assert) {
    // Set any properties with this.set('myProperty', 'value');
    // Handle any actions with this.set('myAction', function(val) { ... });

    await render(hbs`{{alerts-tab}}`);

    assert.equal(find('.alerts-tab_filter').textContent.replace(/\s/g, ''), 'FiltersSeverityFeedbackIndicatorsDateRangeCustomDate3Monthago×ResetFilters');
    assert.equal(find('.alerts-tab_body').textContent.replace(/\s/g, ''), 'CriticalHighLowMediumExportAlertNameEntityNameStartTimeIndicatorCountStatusLoading');
  });
});
