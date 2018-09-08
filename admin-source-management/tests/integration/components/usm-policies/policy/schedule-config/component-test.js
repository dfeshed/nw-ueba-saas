import { module, /* test, */ skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

// ****************************************************************************
// skipping all tests as the create policy component is being replaced...
// we'll delete this once the new policy wizard is finished and is fully tested
// ****************************************************************************

let setState, redux;
const initialState = {
  policy: {
    name: '',
    description: '',
    scheduleConfig: {
      enabledScheduledScan: false,
      scheduleOptions: {
        scanStartDate: null,
        scanStartTime: [10, 0],
        recurrenceInterval: 5,
        recurrenceIntervalUnit: 'DAYS',
        runOnDaysOfWeek: []
      },
      scanOptions: {
        cpuMaximum: 75,
        cpuMaximumOnVirtualMachine: 85
      }
    }
  },
  policyStatus: null // wait, complete, error
};

module('Integration | Component | usm-policies/policy/schedule-config', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      const fullState = { policy: state };
      patchReducer(this, Immutable.from(fullState));
      redux = this.owner.lookup('service:redux');
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.lookup('service:dateFormat').set('selected', 'MM/dd/yyyy');
    this.owner.lookup('service:timeFormat').set('selected', 'HR12');
  });

  skip('should render the schedule date and time field', async function(assert) {
    await render(hbs`{{usm-policies/policy/schedule-config}}`);
    assert.equal(findAll('.schedule-time').length, 2, 'date and time field present');
  });

  skip('toggling the enable schedule button', async function(assert) {
    new ReduxDataHelper(setState)
      .policy(initialState.policy)
      .fetchPolicyStatus('complete')
      .build();
    await render(hbs`{{usm-policies/policy/schedule-config}}`);
    assert.equal(initialState.policy.scheduleConfig.enabledScheduledScan, false, 'scan is disabled');
    await click('.x-toggle-container .x-toggle-btn');
    let state = redux.getState();
    assert.equal(state.usm.policy.policy.scheduleConfig.enabledScheduledScan, true, 'scan is enabled by toggling the button');
    await click('.x-toggle-container .x-toggle-btn');
    state = redux.getState();
    assert.equal(state.usm.policy.policy.scheduleConfig.enabledScheduledScan, false, 'scan is disabled by toggling the button');
  });
});
