import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import existAnomalyTypes from '../../../../../data/presidio/exist_anomaly_types';
import existAlertTypes from '../../../../../data/presidio/exist_alert_types';
import { clickTrigger, selectChoose } from '../../../../../helpers/ember-power-select';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | users-tab/filter/filter', function(hooks) {
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

  test('it renders', async function(assert) {
    await render(hbs`{{users-tab/filter/filter}}`);
    assert.equal(find('.users-tab_filter_filter').textContent.replace(/\s/g, ''), 'FiltersAllTypesIndicators');
  });

  test('it renders with selected filter', async function(assert) {
    new ReduxDataHelper(setState).existAnomalyTypesForUsers(existAnomalyTypes).usersExistAlertTypes(existAlertTypes.data).build();
    await render(hbs`{{users-tab/filter/filter}}`);
    await clickTrigger('.users-tab_filter_filter_select:nth-child(1)');
    assert.equal(findAll('.ember-power-select-option').length, 9);
    await selectChoose('.users-tab_filter_filter_select:nth-child(1)', 'snooping_user');
    return settled().then(() => {
      clickTrigger('.users-tab_filter_filter_select:nth-child(2)');
      return settled().then(() => {
        assert.equal(findAll('.ember-power-select-option').length, 26);
        selectChoose('.users-tab_filter_filter_select:nth-child(2)', 'abnormal_event_day_time');
        return settled();
      });
    });
  });
});
