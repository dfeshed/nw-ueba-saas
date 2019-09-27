import { find, findAll, render, settled } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchFetch } from '../../../../helpers/patch-fetch';
import { Promise } from 'rsvp';

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
    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return {};
          }
        });
      });
    });
  });

  test('it should render alert tab filter', async function(assert) {
    await render(hbs`{{alerts-tab/filter}}`);
    assert.equal(findAll('.users-tab_filter_filter_select').length, 4);
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
    await selectChoose('.users-tab_filter_filter_select:nth-child(4)', 'Abnormal File Access Event');
    return settled();
  });

  test('it should render alert tab filter for update filters for severity', async function(assert) {
    new ReduxDataHelper(setState).existAnomalyTypesForFilter({
      abnormal_file_action_operation_type: 45,
      abnormal_logon_day_time: 25,
      user_password_reset: 52
    }).build();
    await render(hbs`{{alerts-tab/filter}}`);
    await clickTrigger('.users-tab_filter_filter_select:nth-child(2)');
    assert.equal(findAll('.ember-power-select-option').length, 4);
    await selectChoose('.users-tab_filter_filter_select:nth-child(2)', 'High');
    return settled();
  });

  test('it should render alert tab filter for update filters for feedback', async function(assert) {
    new ReduxDataHelper(setState).existAnomalyTypesForFilter({
      abnormal_file_action_operation_type: 45,
      abnormal_logon_day_time: 25,
      user_password_reset: 52
    }).build();
    await render(hbs`{{alerts-tab/filter}}`);
    await clickTrigger('.users-tab_filter_filter_select:nth-child(3)');
    assert.equal(findAll('.ember-power-select-option').length, 2);
    await selectChoose('.users-tab_filter_filter_select:nth-child(3)', 'None');
    return settled();
  });

  test('it should render alert tab filter for update filters for entity type', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState).existAnomalyTypesForFilter({
      abnormal_file_action_operation_type: 45,
      abnormal_logon_day_time: 25,
      user_password_reset: 52
    }).build();
    await render(hbs`{{alerts-tab/filter}}`);
    await clickTrigger('.users-tab_filter_filter_select:nth-child(5)');
    assert.equal(findAll('.ember-power-select-option').length, 4);
    await selectChoose('.users-tab_filter_filter_select:nth-child(5)', 'JA3');
    assert.equal(find('.ember-power-select-selected-item').innerText, 'JA3');
    return settled();
  });

});
