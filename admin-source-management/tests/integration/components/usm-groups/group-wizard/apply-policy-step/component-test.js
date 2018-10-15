import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | usm-groups/group-wizard/apply-policy-step', function(hooks) {
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


  test('With no policy assignments component appears in the DOM', async function(assert) {
    assert.expect(3);
    const groupPayload = {
      'id': 'group_001',
      'name': 'Group 001',
      'assignedPolicies': {}
    };
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayload)
      .groupWizPolicyList(policyListPayload)
      .build();
    await render(hbs`{{usm-groups/group-wizard/apply-policy-step}}`);
    assert.equal(findAll('.apply-policy-step').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.add-source-type-button').length, 1, 'add-source-type-button control appears in the DOM');
    assert.equal(findAll('.source-type').length, 1, 'source-type control appears in the DOM');
  });

  test('With one policy assignments component appears in the DOM', async function(assert) {
    assert.expect(3);
    const groupPayload = {
      'id': 'group_001',
      'name': 'Group 001',
      'assignedPolicies': {
        'edrPolicy': {
          'referenceId': 'policy_001',
          'name': 'Policy 001'
        }
      }
    };
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayload)
      .groupWizPolicyList(policyListPayload)
      .build();
    await render(hbs`{{usm-groups/group-wizard/apply-policy-step}}`);
    assert.equal(findAll('.apply-policy-step').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.add-source-type-button').length, 1, 'add-source-type-button control appears in the DOM');
    assert.equal(findAll('.source-type').length, 1, 'source-type control appears in the DOM');
  });

  test('With two policy assignments component appears in the DOM', async function(assert) {
    assert.expect(3);
    const groupPayload = {
      'id': 'group_001',
      'name': 'Group 001',
      'assignedPolicies': {
        'edrPolicy': {
          'referenceId': 'policy_001',
          'name': 'Policy 001'
        },
        'windowsLogPolicy': {
          'referenceId': 'policy_003',
          'name': 'Policy 003'
        }
      }
    };
    new ReduxDataHelper(setState)
      .groupWiz()
      .groupWizGroup(groupPayload)
      .groupWizPolicyList(policyListPayload)
      .build();
    await render(hbs`{{usm-groups/group-wizard/apply-policy-step}}`);
    assert.equal(findAll('.apply-policy-step').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('.add-source-type-button').length, 1, 'add-source-type-button control appears in the DOM');
    assert.equal(findAll('.source-type').length, 2, 'source-type control appears in the DOM');
  });
});
