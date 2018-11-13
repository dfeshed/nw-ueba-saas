import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import { selectChoose } from 'ember-power-select/test-support/helpers';

let setState;

module('Integration | Component | usm-groups/group-wizard/apply-policy-step/source-type', function(hooks) {
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
    },
    {
      id: 'policy_002',
      name: 'Policy 002',
      policyType: 'edrPolicy',
      description: 'EMC Reston 012 of policy policy_012',
      lastPublishedOn: 0,
      dirty: true,
      defaultPolicy: false
    },
    {
      id: '__default_windows_log_policy',
      name: 'Default Windows Log Policy',
      policyType: 'windowsLogPolicy',
      description: 'Default Windows Log Policy __default_windows_log_policy',
      lastPublishedOn: 0,
      dirty: true,
      defaultPolicy: true
    },
    {
      id: 'policy_003',
      name: 'Policy 003',
      policyType: 'windowsLogPolicy',
      description: 'EMC Reston 012 of policy policy_012',
      lastPublishedOn: 0,
      dirty: true,
      defaultPolicy: false
    }
  ];

  const groupPayload1 = {
    'id': 'group_001',
    'name': 'Group 001',
    'assignedPolicies': {
      'edrPolicy': {
        'referenceId': 'policy_001',
        'name': 'Policy 001'
      }
    }
  };

  test('The component appears in the DOM', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayload1)
      .groupWizPolicyList(policyListPayload)
      .build();
    await render(hbs`{{usm-groups/group-wizard/apply-policy-step/source-type  selectedSourceType=policy.policyType selectedPolicy=policy}}`);
    assert.equal(findAll('.source-type').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.source-type-selector ').length, 1, 'control source-type appears in the DOM');
    assert.equal(findAll('.policy-assignment').length, 1, 'control policy-assignment appears in the DOM');
    assert.notOk(find('.selector-error'), 'Error is not showing with valid source type/policy selection input');
  });

  skip('Policy Source Type invalid assignments - validation error', async function(assert) {
    assert.expect(6);
    const groupInvalidSelection = {
      'id': 'group_001',
      'name': 'Group 001',
      'assignedPolicies': {
        'edrPolicy': {
          'referenceId': 'placeholder',
          'name': ''
        }
      }
    };

    const translation = this.owner.lookup('service:i18n');
    const expectedMessage = translation.t('adminUsm.groupCriteria.inputValidations.validPolicyAssigned');
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupInvalidSelection)
      .groupWizPolicyList(policyListPayload)
      .groupWizStepShowErrors('applyPolicyStep', true)
      .build();

    await render(hbs`{{usm-groups/group-wizard/apply-policy-step/source-type  selectedSourceType=null selectedPolicy=null}}`);
    assert.equal(findAll('.source-type').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.source-type-selector ').length, 1, 'control source-type appears in the DOM');
    assert.equal(findAll('.policy-assignment').length, 1, 'control policy-assignment appears in the DOM');
    assert.ok(find('.selector-error'), 'Error is showing with invalid source type/policy selection input');
    assert.equal(findAll('.input-error').length, 1, '.input-error appears in the DOM');
    assert.equal(find('.input-error').textContent.trim(), expectedMessage, 'Correct error message is showing');
  });

  test('Policy Source Type assignment with no current assignments - selection 1', async function(assert) {
    assert.expect(4);
    const groupAssignmentsBeforeSelection = {
      'id': 'group_001',
      'name': 'Group 001',
      'assignedPolicies': {}
    };

    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupAssignmentsBeforeSelection)
      .groupWizPolicyList(policyListPayload)
      .build();
    await render(hbs`{{usm-groups/group-wizard/apply-policy-step/source-type  selectedSourceType=null selectedPolicy=null}}`);
    assert.equal(findAll('.source-type').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.source-type-selector ').length, 1, 'control source-type appears in the DOM');
    assert.equal(findAll('.policy-assignment').length, 1, 'control policy-assignment appears in the DOM');

    await selectChoose('.source-type-selector', '.ember-power-select-option', 0);
    const state = this.owner.lookup('service:redux').getState();
    const expectedAssignmentsAfterSelection = {
      'edrPolicy': {
        'name': 'Select a Policy',
        'referenceId': 'placeholder'
      }
    };
    assert.deepEqual(state.usm.groupWizard.group.assignedPolicies, expectedAssignmentsAfterSelection, 'source-type assignments are correct value for first selection');
  });

  test('Policy Source Type assignment with no current assignments - selection 2', async function(assert) {
    assert.expect(5);
    const groupAssignmentsBeforeSelection = {
      'id': 'group_001',
      'name': 'Group 001',
      'assignedPolicies': {}
    };

    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupAssignmentsBeforeSelection)
      .groupWizPolicyList(policyListPayload)
      .build();
    await render(hbs`{{usm-groups/group-wizard/apply-policy-step/source-type  selectedSourceType=null selectedPolicy=null}}`);
    assert.equal(findAll('.source-type').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.source-type-selector ').length, 1, 'control source-type appears in the DOM');
    assert.equal(findAll('.policy-assignment').length, 1, 'control policy-assignment appears in the DOM');

    await selectChoose('.source-type-selector', '.ember-power-select-option', 1);
    const state = this.owner.lookup('service:redux').getState();
    const expectedAssignmentsAfterSelection = {
      'windowsLogPolicy': {
        'name': 'Select a Policy',
        'referenceId': 'placeholder'
      }
    };
    assert.deepEqual(state.usm.groupWizard.group.assignedPolicies, expectedAssignmentsAfterSelection, 'source-type assignments are correct value for second selection');
    assert.equal(findAll('.remove-policy').length, 0, 'control to remove policy does NOT appear in the DOM');
  });

  test('Policy Source Type assignment with current assignments - selection 2', async function(assert) {
    assert.expect(4);
    const groupAssignmentsBeforeSelection = {
      'id': 'group_001',
      'name': 'Group 001',
      'assignedPolicies': {}
    };

    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupAssignmentsBeforeSelection)
      .groupWizPolicyList(policyListPayload)
      .build();

    const expectedAssignmentsAfterSelection = {
      'edrPolicy': {
        'name': 'Select a Policy',
        'referenceId': 'placeholder'
      }
    };

    await render(hbs`{{usm-groups/group-wizard/apply-policy-step/source-type  selectedSourceType=null selectedPolicy=null}}`);
    assert.equal(findAll('.source-type').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.source-type-selector ').length, 1, 'control source-type appears in the DOM');
    assert.equal(findAll('.policy-assignment').length, 1, 'control policy-assignment appears in the DOM');

    await selectChoose('.source-type-selector', '.ember-power-select-option', 0);
    const state = this.owner.lookup('service:redux').getState();
    assert.deepEqual(state.usm.groupWizard.group.assignedPolicies, expectedAssignmentsAfterSelection, 'source-type assignments are correct value for first selection');
  });

  test('Remove assigned Policy', async function(assert) {
    const groupAssignmentsBeforeSelection = {
      'id': 'group_001',
      'name': 'Group 001',
      'assignedPolicies': {
        'edrPolicy': {
          'name': 'test_123',
          'referenceId': 'test_abc'
        }
      }
    };
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupAssignmentsBeforeSelection)
      .groupWizPolicyList(policyListPayload)
      .build();

    const expectedAssignmentsAfterRemovel = {
      'edrPolicy': {
        'name': '',
        'referenceId': 'placeholder'
      }
    };
    const selectedPolicy = { 'policyType': 'edrPolicy' };
    this.set('selectedPolicy', selectedPolicy);
    await render(hbs`{{usm-groups/group-wizard/apply-policy-step/source-type  selectedSourceType=null selectedPolicy=selectedPolicy}}`);
    assert.equal(findAll('.remove-policy').length, 1, 'control to remove policy appears in the DOM');
    await click('.remove-policy');
    const state = this.owner.lookup('service:redux').getState();
    assert.deepEqual(state.usm.groupWizard.group.assignedPolicies, expectedAssignmentsAfterRemovel, 'Policy test_123 vas removed');
  });

  // TODO test policy selection when table implemented in flow-up story

});
