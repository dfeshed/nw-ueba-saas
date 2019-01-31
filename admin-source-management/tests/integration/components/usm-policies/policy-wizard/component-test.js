import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import waitForReduxStateChange from '../../../../helpers/redux-async-helpers';
import { initializePolicy } from 'admin-source-management/actions/creators/policy-wizard-creators';

let setState, setStateToo, redux;

module('Integration | Component | usm-policies/policy-wizard', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    // this one is for ReduxDataHelper
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    redux = this.owner.lookup('service:redux');
    // this one is for real redux calls
    setStateToo = (policyId) => {
      redux.dispatch(initializePolicy(policyId));
    };
  });

  test('The component appears in the DOM', async function(assert) {
    new ReduxDataHelper(setState).build();
    this.set('transitionToPolicies', () => {});
    await render(hbs`{{usm-policies/policy-wizard transitionToPolicies=(action transitionToPolicies)}}`);
    assert.equal(findAll('.usm-policy-wizard').length, 1, 'The component appears in the DOM');
  });

  test('The component renders the rsa-wizard once the fetching of the policy succeeds', async function(assert) {
    assert.expect(3);
    setStateToo('policy_014'); // this policy id exists in mock data
    const fetchPolicy = waitForReduxStateChange(redux, 'usm.policyWizard.policyStatus');
    this.set('transitionToPolicies', () => {});
    await render(hbs`{{usm-policies/policy-wizard transitionToPolicies=(action transitionToPolicies)}}`);
    await fetchPolicy;
    assert.equal(findAll('.usm-policy-wizard').length, 1, 'Policy Wizard rendered');
    assert.equal(findAll('.usm-policy-wizard .rsa-wizard-container').length, 1, 'rsa-wizard rendered within Policy Wizard');
    assert.equal(findAll('.usm-policy-wizard .error-page').length, 0, 'error-page NOT rendered within Policy Wizard');
  });

  test('The component renders the error-page once the fetching of the policy fails', async function(assert) {
    assert.expect(3);
    setStateToo('policy_blah'); // no such policy id exists
    const fetchPolicy = waitForReduxStateChange(redux, 'usm.policyWizard.policyStatus');
    this.set('transitionToPolicies', () => {});
    await render(hbs`{{usm-policies/policy-wizard transitionToPolicies=(action transitionToPolicies)}}`);
    await fetchPolicy;
    assert.equal(findAll('.usm-policy-wizard').length, 1, 'Policy Wizard rendered');
    assert.equal(findAll('.usm-policy-wizard .error-page').length, 1, 'error-page rendered within Policy Wizard');
    assert.equal(findAll('.usm-policy-wizard .rsa-wizard-container').length, 0, 'rsa-wizard NOT rendered within Policy Wizard');
  });

});
