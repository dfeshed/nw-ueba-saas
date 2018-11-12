import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { click, findAll, render, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { patchFlash } from '../../../../../helpers/patch-flash';
import { throwSocket } from '../../../../../helpers/patch-socket';

let setState;

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

  test('The component appears in the DOM', async function(assert) {
    assert.expect(1);
    const state = new ReduxDataHelper(setState).policyWiz().build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    assert.equal(findAll('.policy-wizard-toolbar').length, 1, 'The component appears in the DOM');
  });

  test('Identify Policy Step - Toolbar appearance with invalid data', async function(assert) {
    assert.expect(5);
    const state = new ReduxDataHelper(setState).policyWiz().build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    assert.equal(findAll('.prev-button.is-disabled').length, 1, 'The Previous button appears in the DOM and is disabled');
    assert.equal(findAll('.next-button:not(.is-disabled)').length, 1, 'The Next button appears in the DOM and is enabled');
    assert.equal(findAll('.publish-button.is-disabled').length, 1, 'The Publish button appears in the DOM and is disabled');
    assert.equal(findAll('.save-button.is-disabled').length, 1, 'The Save button appears in the DOM and is disabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Identify Policy Step - Toolbar appearance with valid data', async function(assert) {
    assert.expect(5);
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .build();
    this.set('step', state.usm.policyWizard.steps[0]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    assert.equal(findAll('.prev-button.is-disabled').length, 1, 'The Previous button appears in the DOM and is disabled');
    assert.equal(findAll('.next-button:not(.is-disabled)').length, 1, 'The Next button appears in the DOM and is enabled');
    assert.equal(findAll('.publish-button.is-disabled').length, 1, 'The Publish button appears in the DOM and is disabled');
    assert.equal(findAll('.save-button.is-disabled').length, 1, 'The Save button appears in the DOM and is disabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Identify Policy Step - Toolbar closure with valid data', async function(assert) {
    const done = assert.async(2);
    assert.expect(7);
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
    await settled();
    assert.equal(findAll('.prev-button.is-disabled').length, 1, 'The Previous button appears in the DOM and is disabled');
    assert.equal(findAll('.next-button:not(.is-disabled)').length, 1, 'The Next button appears in the DOM and is enabled');
    assert.equal(findAll('.publish-button.is-disabled').length, 1, 'The Publish button appears in the DOM and is disabled');
    assert.equal(findAll('.save-button.is-disabled').length, 1, 'The Save button appears in the DOM and is disabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');

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

  test('Define Policy Step - Toolbar appearance with invalid data', async function(assert) {
    assert.expect(5);
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .build();
    this.set('step', state.usm.policyWizard.steps[1]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    await settled();
    assert.equal(findAll('.prev-button:not(.is-disabled)').length, 1, 'The Previous button appears in the DOM and is enabled');
    assert.equal(findAll('.next-button.is-disabled').length, 1, 'The Next button appears in the DOM and is disabled');
    assert.equal(findAll('.publish-button:not(.is-disabled)').length, 1, 'The Publish button appears in the DOM and is enabled');
    assert.equal(findAll('.save-button:not(.is-disabled)').length, 1, 'The Save button appears in the DOM and is enabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Define Policy Step - Toolbar appearance with valid data', async function(assert) {
    assert.expect(5);
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .policyWizBlockingEnabled(true)
      .build();
    this.set('step', state.usm.policyWizard.steps[1]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    await settled();
    assert.equal(findAll('.prev-button:not(.is-disabled)').length, 1, 'The Previous button appears in the DOM and is enabled');
    assert.equal(findAll('.next-button.is-disabled').length, 1, 'The Next button appears in the DOM and is disabled');
    assert.equal(findAll('.publish-button:not(.is-disabled)').length, 1, 'The Publish button appears in the DOM and is enabled');
    assert.equal(findAll('.save-button:not(.is-disabled)').length, 1, 'The Save button appears in the DOM and is enabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Define Policy Step - Toolbar closure actions with valid data', async function(assert) {
    const done = assert.async(4);
    assert.expect(9);
    const newSelectedSettings = [
      { index: 12, id: 'invActionsHeader', label: 'adminUsm.policy.invasiveActions', isHeader: true, isEnabled: true },
      { index: 13, id: 'blockingEnabled', label: 'adminUsm.policy.blockingEnabled', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'blockingEnabled', value: false }] }
    ];
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizName('test name')
      .policyWizBlockingEnabled(true)
      .policyWizSelectedSettings(newSelectedSettings)
      .build();
    this.set('step', state.usm.policyWizard.steps[1]);
    this.set('transitionToStep', () => {});
    this.set('transitionToClose', () => {});
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar
      step=step
      transitionToStep=(action transitionToStep)
      transitionToClose=(action transitionToClose)}}`
    );
    await settled();
    assert.equal(findAll('.prev-button:not(.is-disabled)').length, 1, 'The Previous button appears in the DOM and is enabled');
    assert.equal(findAll('.next-button.is-disabled').length, 1, 'The Next button appears in the DOM and is disabled');
    assert.equal(findAll('.publish-button:not(.is-disabled)').length, 1, 'The Publish button appears in the DOM and is enabled');
    assert.equal(findAll('.save-button:not(.is-disabled)').length, 1, 'The Save button appears in the DOM and is enabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');

    // clicking the prev-button should call transitionToStep() with the correct stepId
    // update transitionToStep for prev-button
    this.set('transitionToStep', (stepId) => {
      assert.equal(stepId, this.get('step').prevStepId, `transitionToStep(${stepId}) was called with the correct stepId by Previous`);
      done();
    });
    const [prevBtnEl] = findAll('.prev-button:not(.is-disabled) button');
    await click(prevBtnEl);

    // clicking the publish-button should call transitionToClose()
    // update transitionToClose for publish-button
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
      done();
    });
    const [publishBtnEl] = findAll('.publish-button:not(.is-disabled) button');
    await click(publishBtnEl);

    // clicking the save-button should call transitionToClose()
    // update transitionToClose for save-button
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
      done();
    });
    const [saveBtnEl] = findAll('.save-button:not(.is-disabled) button');
    await click(saveBtnEl);

    // clicking the cancel-button should call transitionToClose()
    // update transitionToClose for cancel-button
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
      done();
    });
    const [cancelBtnEl] = findAll('.cancel-button:not(.is-disabled) button');
    await click(cancelBtnEl);
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
      .policyWizSelectedSettings(newSelectedSettings)
      .build();
    this.set('step', state.usm.policyWizard.steps[1]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    await settled();

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
      .policyWizSelectedSettings(newSelectedSettings)
      .build();
    this.set('step', state.usm.policyWizard.steps[1]);
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
    });
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar
      step=step
      transitionToClose=(action transitionToClose)}}`
    );
    await settled();

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
      .policyWizSelectedSettings(newSelectedSettings)
      .build();
    this.set('step', state.usm.policyWizard.steps[1]);
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar step=step}}`);
    await settled();

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
      .policyWizSelectedSettings(newSelectedSettings)
      .build();
    this.set('step', state.usm.policyWizard.steps[1]);
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
    });
    await render(hbs`{{usm-policies/policy-wizard/policy-toolbar
      step=step
      transitionToClose=(action transitionToClose)}}`
    );
    await settled();

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

});
