import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, render, settled } from '@ember/test-helpers';
import sinon from 'sinon';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { patchFlash } from '../../../../../helpers/patch-flash';
import { throwSocket } from '../../../../../helpers/patch-socket';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';

let setState, savePolicySpy;
const spys = [];

module('Integration | Component | usm-policies/policy-wizard/policy-toolbar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.before(function() {
    spys.push(savePolicySpy = sinon.spy(policyWizardCreators, 'savePolicy'));
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    spys.forEach((s) => s.reset());
  });

  hooks.after(function() {
    spys.forEach((s) => s.restore());
  });

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

  test('Toolbar closure actions for Identify Policy step with valid data', async function(assert) {
    assert.expect(2);
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .build();
    this.set('step', state.usm.policyWizard.steps[0]);
    this.set('transitionToStep', () => {});
    this.set('transitionToClose', () => {});
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar
      step=step
      transitionToStep=(action transitionToStep)
      transitionToClose=(action transitionToClose)}}`
    );

    // skip prev-button & publish-button since they aren't rendered

    // clicking the next-button should call transitionToStep() with the correct stepId
    // update transitionToStep for next-button
    this.set('transitionToStep', (stepId) => {
      assert.equal(stepId, this.get('step').nextStepId, `transitionToStep(${stepId}) was called with the correct stepId by Next`);
    });
    const [nextBtnEl] = findAll('.next-button:not(.is-disabled) button');
    await click(nextBtnEl);

    // clicking the cancel-button should call transitionToClose()
    // update transitionToClose for cancel-button
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
    });
    const [cancelBtnEl] = findAll('.cancel-button:not(.is-disabled) button');
    await click(cancelBtnEl);
  });

  test('Toolbar save action for Identify Policy step with valid data', async function(assert) {
    assert.expect(2);
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .build();
    this.set('step', state.usm.policyWizard.steps[0]);
    this.set('transitionToStep', () => {});
    this.set('transitionToClose', () => {});
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar
      step=step
      transitionToStep=(action transitionToStep)
      transitionToClose=(action transitionToClose)}}`
    );

    // skip prev-button & publish-button since they aren't rendered

    // clicking the save-button should dispatch the savePolicy action
    const [saveBtnEl] = findAll('.save-button:not(.is-disabled) button');
    await click(saveBtnEl);
    return settled().then(() => {
      assert.ok(savePolicySpy.calledOnce, 'The savePolicy action was called once by Save');
      // only checking first arg as second arg will be a Function that lives in the Component
      assert.equal(savePolicySpy.getCall(0).args[0], state.usm.policyWizard.policy);
    });
  });

  test('On failing to save a policy, an error flash message is shown', async function(assert) {
    assert.expect(2);
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);

    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.policyWizard.saveFailure');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
    });

    const [saveBtnEl] = findAll('.save-button:not(.is-disabled) button');
    await click(saveBtnEl);
  });

  test('On successfully saving a policy, a success flash message is shown, and the transitionToClose action is called', async function(assert) {
    assert.expect(3);
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .build();
    this.set('step', state.usm.policyWizard.steps[0]);
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
    });
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar
      step=step
      transitionToClose=(action transitionToClose)}}`
    );

    const done = assert.async();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.policyWizard.saveSuccess');
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    const [saveBtnEl] = findAll('.save-button:not(.is-disabled) button');
    await click(saveBtnEl);
  });

});
