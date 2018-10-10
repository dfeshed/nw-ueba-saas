import { find, findAll, render, settled } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { clickTrigger, selectChoose } from '../../../../helpers/ember-power-select';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | alerts-tab/filter', function(hooks) {
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

  test('it should render alert tab filter', async function(assert) {
    await render(hbs`{{alerts-tab/filter}}`);
    assert.equal(find('.alerts-tab_filter').textContent.replace(/\s/g, ''), 'FiltersSeverityFeedbackIndicatorsDateRange:-:ResetFilters');
  });

  test('it should render alert tab filter for update filters', async function(assert) {
    new ReduxDataHelper(setState).existAnomalyTypesForFilter({
      abnormal_file_action_operation_type: 45,
      abnormal_logon_day_time: 25,
      user_password_reset: 52
    }).build();
    await render(hbs`{{alerts-tab/filter}}`);
    await clickTrigger('.users-tab_filter_filter_select:nth-child(4)');
    assert.equal(findAll('.ember-power-select-option').length, 3);
    await selectChoose('.users-tab_filter_filter_select:nth-child(4)', 'abnormal_file_action_operation_type');
    await clickTrigger('.users-tab_filter_filter_select:nth-child(2)');
    assert.equal(findAll('.ember-power-select-option').length, 4);
    await selectChoose('.users-tab_filter_filter_select:nth-child(2)', 'high');
    await clickTrigger('.users-tab_filter_filter_select:nth-child(3)');
    assert.equal(findAll('.ember-power-select-option').length, 2);
    await selectChoose('.users-tab_filter_filter_select:nth-child(3)', 'none');
    return settled();
  });
});
