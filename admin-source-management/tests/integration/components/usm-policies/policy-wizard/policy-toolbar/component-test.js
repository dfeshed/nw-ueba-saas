import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, render } from '@ember/test-helpers';
// import sinon from 'sinon';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
// import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';

let setState;

// const editPolicySpy = sinon.spy(policyWizardCreators, 'editPolicy');

// const spys = [
//   editPolicySpy
// ];


module('Integration | Component | usm-policies/policy-wizard/policy-toolbar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  // hooks.afterEach(function() {
  //   spys.forEach((s) => s.reset());
  // });

  // hooks.after(function() {
  //   spys.forEach((s) => s.restore());
  // });

  test('The component appears in the DOM', async function(assert) {
    const state = new ReduxDataHelper(setState).policyWiz().build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    assert.equal(findAll('.policy-wizard-toolbar').length, 1, 'The component appears in the DOM');
  });

  test('Toolbar appearance for Identify Policy step with invalid data', async function(assert) {
    const state = new ReduxDataHelper(setState).policyWiz().build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    assert.equal(findAll('.prev-button').length, 0, 'The Previous button does NOT appear in the DOM');
    assert.equal(findAll('.next-button.is-disabled').length, 1, 'The Next button appears in the DOM and is disabled');
    assert.equal(findAll('.publish-button').length, 0, 'The Publish button does NOT appear in the DOM');
    assert.equal(findAll('.save-button.is-disabled').length, 1, 'The Save button appears in the DOM and is disabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Toolbar appearance for Identify Policy step valid data', async function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    assert.equal(findAll('.prev-button').length, 0, 'The Previous button does NOT appear in the DOM');
    assert.equal(findAll('.next-button:not(.is-disabled)').length, 1, 'The Next button appears in the DOM and is enabled');
    assert.equal(findAll('.publish-button').length, 0, 'The Publish button does NOT appear in the DOM');
    assert.equal(findAll('.save-button:not(.is-disabled)').length, 1, 'The Save button appears in the DOM and is enabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Toolbar actions for Identify Policy step with valid data', async function(assert) {
    assert.expect(1);
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .build();
    this.set('step', state.usm.policyWizard.steps[0]);
    this.set('transitionToStep', () => {});
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step transitionToStep=(action transitionToStep)}}`);

    // skip prev-button & publish-button since they aren't rendered

    // update transitionToStep for next-button
    this.set('transitionToStep', (stepId) => {
      assert.equal(stepId, this.get('step').nextStepId, `transitionToStep(${stepId}) was called with the correct stepId by Next`);
    });
    const nextBtnEl = findAll('.next-button:not(.is-disabled) button')[0]; // eslint-disable-line ember-suave/prefer-destructuring
    await click(nextBtnEl);

    // skip save-button & cancel-button since they don't do anything yet
    // assert.equal(findAll('.save-button:not(.is-disabled)').length, 1, 'The Save button appears in the DOM and is enabled');
    // assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

});
