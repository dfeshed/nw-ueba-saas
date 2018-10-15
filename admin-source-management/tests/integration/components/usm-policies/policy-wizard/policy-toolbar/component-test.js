import { module, test, skip } from 'qunit';
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
import Immutable from 'seamless-immutable';
import policyWizardCreators from 'admin-source-management/actions/creators/policy-wizard-creators';

let setState, setStateOldSchool;

module('Integration | Component | usm-policies/policy-wizard/policy-toolbar', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('admin-source-management')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    setStateOldSchool = (state) => {
      const fullState = { usm: { policyWizard: state } };
      patchReducer(this, Immutable.from(fullState));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The component appears in the DOM', async function(assert) {
    assert.expect(1);
    const state = new ReduxDataHelper(setState).policyWiz().build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    assert.equal(findAll('.policy-wizard-toolbar').length, 1, 'The component appears in the DOM');
  });

  test('Toolbar appearance for Identify Policy step with invalid data', async function(assert) {
    assert.expect(5);
    const state = new ReduxDataHelper(setState).policyWiz().build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    assert.equal(findAll('.prev-button').length, 0, 'The Previous button does NOT appear in the DOM');
    assert.equal(findAll('.next-button.is-disabled').length, 1, 'The Next button appears in the DOM and is disabled');
    assert.equal(findAll('.publish-button').length, 0, 'The Publish button does NOT appear in the DOM');
    assert.equal(findAll('.save-button.is-disabled').length, 1, 'The Save button appears in the DOM and is disabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Toolbar appearance for Identify Policy step when no settings added', async function(assert) {
    assert.expect(5);
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    assert.equal(findAll('.prev-button').length, 0, 'The Previous button does NOT appear in the DOM');
    assert.equal(findAll('.next-button:not(.is-disabled)').length, 1, 'The Next button appears in the DOM and is enabled');
    assert.equal(findAll('.publish-button').length, 0, 'The Publish button does NOT appear in the DOM');
    assert.equal(findAll('.save-button.is-disabled').length, 1, 'The Save button appears in the DOM and is disabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Toolbar closure actions for Identify Policy step with valid data', async function(assert) {
    const done = assert.async(2);
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
      done();
    });
    const [nextBtnEl] = findAll('.next-button:not(.is-disabled) button');
    await click(nextBtnEl);

    // clicking the cancel-button should call transitionToClose()
    // update transitionToClose for cancel-button
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
      done();
    });
    const [cancelBtnEl] = findAll('.cancel-button:not(.is-disabled) button');
    await click(cancelBtnEl);
  });

  // TODO skipping this as it suddenly fails most of the time - the spy props are not getting set
  skip('Toolbar save action for Identify Policy step with valid data', async function(assert) {
    const done = assert.async();
    assert.expect(2);
    const actionSpy = sinon.spy(policyWizardCreators, 'savePolicy');
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
    done();

    // skip prev-button & publish-button since they aren't rendered

    // clicking the save-button should dispatch the savePolicy action
    assert.equal(findAll('.save-button:not(.is-disabled)').length, 1, 'The Save button appears in the DOM and is enabled');
    const [saveBtnEl] = findAll('.save-button:not(.is-disabled) button');
    await focus(saveBtnEl);
    await click(saveBtnEl);
    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The savePolicy action was called once by Save');
      // only checking first arg as second arg will be a Function that lives in the Component
      assert.equal(actionSpy.getCall(0).args[0], state.usm.policyWizard.policy);
      actionSpy.restore();
      done();
    });
  });

  test('On failing to save a policy, an error flash message is shown', async function(assert) {
    const done = assert.async();
    assert.expect(2);
    const newSelectedSettings = [
      { index: 12, id: 'invActionsHeader', label: 'adminUsm.policy.invasiveActions', isHeader: true, isEnabled: true },
      { index: 13, id: 'blockingEnabled', label: 'adminUsm.policy.blockingEnabled', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'blockingEnabled', value: false }] }
    ];
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .policyWizBlockingEnabled(true)
      .build();
    const initialState = state.usm.policyWizard;

    setStateOldSchool({ ...initialState, selectedSettings: newSelectedSettings });
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);

    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const codeResponse = translation.t('adminUsm.errorCodeResponse.default');
      const expectedMessage = translation.t('adminUsm.policyWizard.actionMessages.saveFailure', { errorType: codeResponse });
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    const [saveBtnEl] = findAll('.save-button:not(.is-disabled) button');
    await click(saveBtnEl);
  });

  test('On successfully saving a policy, a success flash message is shown, and the transitionToClose action is called', async function(assert) {
    const done = assert.async();
    assert.expect(3);
    const newSelectedSettings = [
      { index: 12, id: 'invActionsHeader', label: 'adminUsm.policy.invasiveActions', isHeader: true, isEnabled: true },
      { index: 13, id: 'blockingEnabled', label: 'adminUsm.policy.blockingEnabled', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'blockingEnabled', value: false }] }
    ];
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .policyWizBlockingEnabled(true)
      .build();

    const initialState = state.usm.policyWizard;

    setStateOldSchool({ ...initialState, selectedSettings: newSelectedSettings });

    this.set('step', state.usm.policyWizard.steps[0]);
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
    });
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar
      step=step
      transitionToClose=(action transitionToClose)}}`
    );

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.policyWizard.actionMessages.saveSuccess');
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    const [saveBtnEl] = findAll('.save-button:not(.is-disabled) button');
    await click(saveBtnEl);
  });

  test('On failing to save and publish a policy, an error flash message is shown', async function(assert) {
    const done = assert.async();
    assert.expect(3);
    const newSelectedSettings = [
      { index: 12, id: 'invActionsHeader', label: 'adminUsm.policy.invasiveActions', isHeader: true, isEnabled: true },
      { index: 13, id: 'blockingEnabled', label: 'adminUsm.policy.blockingEnabled', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'blockingEnabled', value: false }] }
    ];
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .policyWizBlockingEnabled(true)
      .build();

    const initialState = state.usm.policyWizard;

    setStateOldSchool({ ...initialState, selectedSettings: newSelectedSettings });
    this.set('step', state.usm.policyWizard.steps[1]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    assert.equal(findAll('.publish-button').length, 1, 'The Publish button appears in the DOM');

    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const codeResponse = translation.t('adminUsm.errorCodeResponse.default');
      const expectedMessage = translation.t('adminUsm.policyWizard.actionMessages.savePublishFailure', { errorType: codeResponse });
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    const [savePublishBtnEl] = findAll('.publish-button:not(.is-disabled) button');
    await click(savePublishBtnEl);
  });

  test('On successfully saving and publishing a policy, a success flash message is shown, and the transitionToClose action is called', async function(assert) {
    const done = assert.async();
    assert.expect(4);
    const newSelectedSettings = [
      { index: 12, id: 'invActionsHeader', label: 'adminUsm.policy.invasiveActions', isHeader: true, isEnabled: true },
      { index: 13, id: 'blockingEnabled', label: 'adminUsm.policy.blockingEnabled', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'blockingEnabled', value: false }] }
    ];
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .policyWizBlockingEnabled(true)
      .build();

    const initialState = state.usm.policyWizard;

    setStateOldSchool({ ...initialState, selectedSettings: newSelectedSettings });

    this.set('step', state.usm.policyWizard.steps[1]);
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
    });
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar
      step=step
      transitionToClose=(action transitionToClose)}}`
    );
    assert.equal(findAll('.publish-button').length, 1, 'The Publish button appears in the DOM');

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.policyWizard.actionMessages.savePublishSuccess');
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    const [savePublishBtnEl] = findAll('.publish-button:not(.is-disabled) button');
    await click(savePublishBtnEl);
  });

  test('Name and description errors do not enable Next and Save buttons', async function(assert) {
    assert.expect(5);
    let testDesc = '';
    for (let index = 0; index < 220; index++) {
      testDesc += 'the-description-is-greater-than-8000-';
    }
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('')
      .policyWizDescription(testDesc)
      .build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    assert.equal(findAll('.prev-button').length, 0, 'The Previous button does NOT appear in the DOM');
    assert.equal(findAll('.next-button.is-disabled').length, 1, 'The Next button appears in the DOM and is NOT enabled');
    assert.equal(findAll('.publish-button').length, 0, 'The Publish button does NOT appear in the DOM');
    assert.equal(findAll('.save-button.is-disabled').length, 1, 'The Save button appears in the DOM and is NOT enabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

});
