import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import existAnomalyTypes from '../../../../../data/presidio/exist_anomaly_types';
import existAlertTypes from '../../../../../data/presidio/exist_alert_types';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchFetch } from '../../../../../helpers/patch-fetch';
import dataIndex from '../../../../../data/presidio';
import { Promise } from 'rsvp';
import waitForReduxStateChange from '../../../../../helpers/redux-async-helpers';

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
    patchFetch((url) => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return dataIndex(url);
          }
        });
      });
    });
  });

  test('it renders', async function(assert) {
    await render(hbs`{{users-tab/filter/filter}}`);
    assert.equal(findAll('.users-tab_filter_filter_select').length, 4);
  });

  test('it renders with selected filter', async function(assert) {
    new ReduxDataHelper(setState).existAnomalyTypesForUsers(existAnomalyTypes).usersExistAlertTypes(existAlertTypes.data).build();
    await render(hbs`{{users-tab/filter/filter}}`);
    await clickTrigger('.users-tab_filter_filter_select:nth-child(4)');
    assert.equal(findAll('.ember-power-select-option').length, 9);
    await selectChoose('.users-tab_filter_filter_select:nth-child(4)', 'Snooping User');
    return settled().then(() => {
      clickTrigger('.users-tab_filter_filter_select:nth-child(5)');
      return settled().then(() => {
        assert.equal(findAll('.ember-power-select-option').length, 25);
        selectChoose('.users-tab_filter_filter_select:nth-child(5)', 'Abnormal File Access Time');
        return settled();
      });
    });
  });

  test('it should filter severity', async function(assert) {
    assert.expect(2);
    const redux = this.owner.lookup('service:redux');
    await render(hbs`{{users-tab/filter/filter}}`);
    await clickTrigger('.users-tab_filter_filter_select:nth-child(3)');
    assert.equal(findAll('.ember-power-select-option').length, 5);
    selectChoose('.users-tab_filter_filter_select:nth-child(3)', 'High');
    const select = waitForReduxStateChange(redux, ('users.filter.severity'));
    return select.then(() => {
      const state = redux.getState();
      assert.equal(state.users.filter.severity, 'High');
    });
  });

  test('it should filter entity type', async function(assert) {
    assert.expect(3);
    const redux = this.owner.lookup('service:redux');
    await render(hbs`{{users-tab/filter/filter}}`);
    await clickTrigger('.users-tab_filter_filter_select:nth-child(1)');
    assert.equal(findAll('.ember-power-select-option').length, 3);
    selectChoose('.users-tab_filter_filter_select:nth-child(1)', 'JA3');
    const select = waitForReduxStateChange(redux, ('users.filter.entityType'));
    return select.then(() => {
      const state = redux.getState();
      assert.equal(state.users.filter.entityType, 'ja3');
      assert.equal(state.users.filter.indicatorTypes, null);
    });
  });
});
