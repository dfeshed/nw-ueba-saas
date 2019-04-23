import { module, test, skip } from 'qunit';
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

module('Integration | Component | usm-groups/group-wizard/group-toolbar', function(hooks) {
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
    const state = new ReduxDataHelper(setState).groupWiz().build();
    this.set('step', state.usm.groupWizard.steps[0]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    assert.equal(findAll('.group-wizard-toolbar').length, 1, 'The component appears in the DOM');
  });

  const policyListPayload = [
    {
      id: '__default_edr_policy',
      name: 'Default EDR Policy',
      policyType: 'edrPolicy',
      description: 'Default EDR Policy __default_edr_policy',
      lastPublishedOn: 1527489158739,
      dirty: false,
      defaultPolicy: true
    },
    {
      id: 'policy_001',
      name: 'Policy 001',
      policyType: 'edrPolicy',
      description: 'EMC 001 of policy policy_001',
      lastPublishedOn: 1527489158739,
      dirty: true,
      defaultPolicy: false
    }
  ];

  const groupPayloadIdentifyGroupStep = {
    id: 'group_001',
    name: 'Group 001',
    groupCriteria: {},
    assignedPolicies: {}
  };

  test('Identify Group Step - Toolbar appearance with invalid data', async function(assert) {
    assert.expect(5);
    const state = new ReduxDataHelper(setState).groupWiz().build();
    this.set('step', state.usm.groupWizard.steps[0]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    await settled();
    assert.equal(findAll('.prev-button.is-disabled').length, 1, 'The Previous button appears in the DOM and is disabled');
    assert.equal(findAll('.next-button:not(.is-disabled)').length, 1, 'The Next button appears in the DOM and is enabled');
    assert.equal(findAll('.publish-button.is-disabled').length, 1, 'The Publish button appears in the DOM and is disabled');
    assert.equal(findAll('.save-button.is-disabled').length, 1, 'The Save button appears in the DOM and is disabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Identify Group Step - Toolbar appearance with valid data', async function(assert) {
    assert.expect(5);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadIdentifyGroupStep)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[0]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    await settled();
    assert.equal(findAll('.prev-button.is-disabled').length, 1, 'The Previous button appears in the DOM and is disabled');
    assert.equal(findAll('.next-button:not(.is-disabled)').length, 1, 'The Next button appears in the DOM and is enabled');
    assert.equal(findAll('.publish-button.is-disabled').length, 1, 'The Publish button appears in the DOM and is disabled');
    assert.equal(findAll('.save-button.is-disabled').length, 1, 'The Save button appears in the DOM and is disabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Identify Group Step - Toolbar closure actions with valid data', async function(assert) {
    const done = assert.async(1);
    assert.expect(6);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadIdentifyGroupStep)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[0]);
    this.set('transitionToStep', () => {});
    this.set('transitionToClose', () => {});
    await render(hbs`{{usm-groups/group-wizard/group-toolbar
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
  });

  const groupPayloadDefineGroupStep = {
    id: 'group_001',
    name: 'Group 001',
    groupCriteria: {
      conjunction: 'AND',
      criteria: [
        [
          'osType',
          'IN',
          [
            'Linux'
          ]
        ]
      ]
    },
    assignedPolicies: {}
  };

  test('Define Group Step - Toolbar appearance with invalid data', async function(assert) {
    assert.expect(5);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadIdentifyGroupStep)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[1]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    await settled();
    assert.equal(findAll('.prev-button:not(.is-disabled)').length, 1, 'The Previous button appears in the DOM and is enabled');
    assert.equal(findAll('.next-button:not(.is-disabled)').length, 1, 'The Next button appears in the DOM and is enabled');
    assert.equal(findAll('.publish-button:not(.is-disabled)').length, 1, 'The Publish button appears in the DOM and is enabled');
    assert.equal(findAll('.save-button:not(.is-disabled)').length, 1, 'The Save button appears in the DOM and is enabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Define Group Step - Toolbar appearance with valid data', async function(assert) {
    assert.expect(5);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadDefineGroupStep)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[1]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    await settled();
    assert.equal(findAll('.prev-button:not(.is-disabled)').length, 1, 'The Previous button appears in the DOM and is enabled');
    assert.equal(findAll('.next-button:not(.is-disabled)').length, 1, 'The Next button appears in the DOM and is enabled');
    assert.equal(findAll('.publish-button:not(.is-disabled)').length, 1, 'The Publish button appears in the DOM and is enabled');
    assert.equal(findAll('.save-button:not(.is-disabled)').length, 1, 'The Save button appears in the DOM and is enabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  test('Define Group Step - Toolbar closure actions with valid data', async function(assert) {
    const done = assert.async(2);
    assert.expect(7);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadDefineGroupStep)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[1]);
    this.set('transitionToStep', () => {});
    this.set('transitionToClose', () => {});
    await render(hbs`{{usm-groups/group-wizard/group-toolbar
      step=step
      transitionToStep=(action transitionToStep)
      transitionToClose=(action transitionToClose)}}`
    );
    await settled();
    assert.equal(findAll('.prev-button:not(.is-disabled)').length, 1, 'The Previous button appears in the DOM and is enabled');
    assert.equal(findAll('.next-button:not(.is-disabled)').length, 1, 'The Next button appears in the DOM and is enabled');
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

    // clicking the next-button should call transitionToStep() with the correct stepId
    // update transitionToStep for next-button
    this.set('transitionToStep', (stepId) => {
      assert.equal(stepId, this.get('step').nextStepId, `transitionToStep(${stepId}) was called with the correct stepId by Next`);
      done();
    });
    const [nextBtnEl] = findAll('.next-button:not(.is-disabled) button');
    await click(nextBtnEl);
  });

  const groupPayloadApplyPolicyStepInvalidData = {
    id: 'group_001',
    name: 'Group 001',
    groupCriteria: {
      conjunction: 'AND',
      criteria: [
        [
          'osType',
          'IN',
          [
            'Linux'
          ]
        ]
      ]
    },
    assignedPolicies: {
      edrPolicy: {
        name: 'Select a Policy',
        referenceId: 'placeholder'
      }
    }
  };

  test('Apply Policy Step - Toolbar appearance with invalid data', async function(assert) {
    assert.expect(5);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadApplyPolicyStepInvalidData)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[2]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    await settled();
    assert.equal(findAll('.prev-button:not(.is-disabled)').length, 1, 'The Previous button appears in the DOM and is enabled');
    assert.equal(findAll('.next-button.is-disabled').length, 1, 'The Next button appears in the DOM and is disabled');
    assert.equal(findAll('.publish-button:not(.is-disabled)').length, 1, 'The Publish button appears in the DOM and is enabled');
    assert.equal(findAll('.save-button:not(.is-disabled)').length, 1, 'The Save button appears in the DOM and is enabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  const groupPayloadApplyPolicyStep = {
    id: 'group_001',
    name: 'Group 001',
    groupCriteria: {
      conjunction: 'AND',
      criteria: [
        [
          'osType',
          'IN',
          [
            'Linux'
          ]
        ]
      ]
    },
    assignedPolicies: {
      edrPolicy: {
        referenceId: 'policy_001',
        name: 'Policy 001'
      }
    }
  };

  test('Apply Policy Step - Toolbar appearance with valid data', async function(assert) {
    assert.expect(5);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadApplyPolicyStep)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[2]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    await settled();
    assert.equal(findAll('.prev-button:not(.is-disabled)').length, 1, 'The Previous button appears in the DOM and is enabled');
    assert.equal(findAll('.next-button.is-disabled').length, 1, 'The Next button appears in the DOM and is disabled');
    assert.equal(findAll('.publish-button:not(.is-disabled)').length, 1, 'The Publish button appears in the DOM and is enabled');
    assert.equal(findAll('.save-button:not(.is-disabled)').length, 1, 'The Save button appears in the DOM and is enabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');
  });

  skip('Apply Policy Step - Toolbar closure actions with valid data', async function(assert) {
    const done = assert.async(3);
    assert.expect(8);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadApplyPolicyStep)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[2]);
    this.set('transitionToStep', () => {});
    this.set('transitionToClose', () => {});
    await render(hbs`{{usm-groups/group-wizard/group-toolbar
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
  });

  test('On selecting the Cancel button with no changes does closure action', async function(assert) {
    const done = assert.async(1);
    assert.expect(6);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadIdentifyGroupStep, true)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[2]);
    this.set('transitionToStep', () => {});
    this.set('transitionToClose', () => {});
    await render(hbs`{{usm-groups/group-wizard/group-toolbar
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

    // clicking the cancel-button should call transitionToClose()
    // update transitionToClose for cancel-button
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
      done();
    });
    const [cancelBtnEl] = findAll('.cancel-button:not(.is-disabled) button');
    await click(cancelBtnEl);
  });

  test('On selecting the Cancel button with valid data prompts user with confirmation dialog to confirm', async function(assert) {
    assert.expect(7);
    const translation = this.owner.lookup('service:i18n');
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadDefineGroupStep)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[2]);
    this.set('transitionToStep', () => {});
    this.set('transitionToClose', () => {});
    await render(hbs`
      <div id='modalDestination'></div>
      {{usm-groups/group-wizard/group-toolbar
        step=step
        transitionToStep=(action transitionToStep)
        transitionToClose=(action transitionToClose)
      }}
    `);
    await settled();
    assert.equal(findAll('.prev-button:not(.is-disabled)').length, 1, 'The Previous button appears in the DOM and is enabled');
    assert.equal(findAll('.next-button.is-disabled').length, 1, 'The Next button appears in the DOM and is disabled');
    assert.equal(findAll('.publish-button:not(.is-disabled)').length, 1, 'The Publish button appears in the DOM and is enabled');
    assert.equal(findAll('.save-button:not(.is-disabled)').length, 1, 'The Save button appears in the DOM and is enabled');
    assert.equal(findAll('.cancel-button:not(.is-disabled)').length, 1, 'The Cancel button appears in the DOM and is enabled');

    // clicking the cancel-button with changed data should call confirmation-modal
    const [cancelBtnEl] = findAll('.cancel-button:not(.is-disabled) button');
    await click(cancelBtnEl);
    const expectedMessage = translation.t('adminUsm.groupWizard.modals.discardChanges.confirm');
    await settled();
    assert.ok(findAll('.confirmation-modal.is-open').length, 1, 'Modal Confirmation is showing');
    assert.equal(findAll('.confirmation-modal .modal-content p')[0].innerText.trim(), expectedMessage, 'Confirm message is incorrect');
    await click('.modal-footer-buttons .is-primary button');
  });

  test('On failing to save a group, an error flash message is shown', async function(assert) {
    const done = assert.async();
    assert.expect(2);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadApplyPolicyStep)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[2]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    await settled();

    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const codeResponse = translation.t('adminUsm.errorCodeResponse.default');
      const expectedMessage = translation.t('adminUsm.groupWizard.actionMessages.saveFailure', { errorType: codeResponse });
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    const [saveBtnEl] = findAll('.save-button:not(.is-disabled) button');
    await click(saveBtnEl);
  });

  test('On successfully saving a group, a success flash message is shown, and the transitionToClose action is called', async function(assert) {
    const done = assert.async();
    assert.expect(3);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadApplyPolicyStep)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[2]);
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
    });
    await render(hbs`{{usm-groups/group-wizard/group-toolbar
      step=step
      transitionToClose=(action transitionToClose)}}`
    );
    await settled();

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.groupWizard.actionMessages.saveSuccess');
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    const [saveBtnEl] = findAll('.save-button:not(.is-disabled) button');
    await click(saveBtnEl);
  });

  test('On failing to save and publish a group, an error flash message is shown', async function(assert) {
    const done = assert.async();
    assert.expect(3);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadApplyPolicyStep)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[2]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    await settled();

    assert.equal(findAll('.publish-button').length, 1, 'The Publish button appears in the DOM');

    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const codeResponse = translation.t('adminUsm.errorCodeResponse.default');
      const expectedMessage = translation.t('adminUsm.groupWizard.actionMessages.savePublishFailure', { errorType: codeResponse });
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    const [savePublishBtnEl] = findAll('.publish-button:not(.is-disabled) button');
    await click(savePublishBtnEl);
  });

  test('On attempting to saving and publishing an unchanged group, an error flash message is shown', async function(assert) {
    const done = assert.async();
    assert.expect(3);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadApplyPolicyStep, true)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[2]);
    await render(hbs`{{usm-groups/group-wizard/group-toolbar step=step}}`);
    await settled();

    assert.equal(findAll('.publish-button').length, 1, 'The Publish button appears in the DOM');

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const codeResponse = translation.t('adminUsm.errorCodeResponse.default');
      const expectedMessage = translation.t('adminUsm.groupWizard.actionMessages.savePublishNoChangeFailure', { errorType: codeResponse });
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    const [savePublishBtnEl] = findAll('.publish-button:not(.is-disabled) button');
    await click(savePublishBtnEl);
  });

  test('On successfully saving and publishing a group, a success flash message is shown, and the transitionToClose action is called', async function(assert) {
    const done = assert.async();
    assert.expect(4);
    const state = new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayloadApplyPolicyStep)
      .groupWizPolicyList(policyListPayload)
      .build();
    this.set('step', state.usm.groupWizard.steps[2]);
    this.set('transitionToClose', () => {
      assert.ok('transitionToClose() was properly triggered');
    });
    await render(hbs`{{usm-groups/group-wizard/group-toolbar
      step=step
      transitionToClose=(action transitionToClose)}}`
    );
    await settled();

    assert.equal(findAll('.publish-button').length, 1, 'The Publish button appears in the DOM');

    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('adminUsm.groupWizard.actionMessages.savePublishSuccess');
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });

    const [savePublishBtnEl] = findAll('.publish-button:not(.is-disabled) button');
    await click(savePublishBtnEl);
  });

});

