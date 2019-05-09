import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import testPolicyAndOrigins from '../../../../data/subscriptions/groups/fetchRankingView/dataWindow';

import {
  focusedPolicy,
  selectedFilePolicy
} from 'admin-source-management/reducers/usm/policy-details/file-policy/file-selectors';

let setState;

const testPolicy = {
  id: 'policy_F001',
  policyType: 'filePolicy',
  name: 'F001',
  description: 'File Policy # F001',
  dirty: true,
  defaultPolicy: false,
  createdBy: 'admin',
  createdOn: 1540318426092,
  lastModifiedBy: 'admin',
  lastModifiedOn: 1540318426092,
  lastPublishedOn: 0,
  lastPublishedCopy: null,
  enabled: true,
  protocol: 'TCP',
  sendTestLog: false,
  primaryDestination: 'LD_01',
  secondaryDestination: 'LD_02',
  associatedGroups: []
};

module('Unit | Selectors | Policy Details | File Policy | File Selectors', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  test('selectedFilePolicy selector', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy(testPolicy)
      .policyWizFileEnabled(true)
      .policyWizFileProtocol('TCP')
      .policyWizFileSendTestLog(false)
      .setPolicyFilePrimaryDest('10.10.10.10')
      .setPolicyFileSecondaryDest('10.10.10.12')
      .build();
    assert.expect(6);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedFilePolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 1, '1 section returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.fileSettings', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 5, 'first section has 5 properties');
    assert.equal(policyDetails[0].props[0].value, 'Enabled', 'enabled property has expected value');
    assert.equal(policyDetails[0].props[3].value, 'NWAPPLIANCE55555 - Log Server', 'primary destination has expected value');
    assert.equal(policyDetails[0].props[4].value, 'NWAPPLIANCE113- Log Server', 'secondary destination has expected value');
  });

  test('selectedFilePolicy ignores blank values', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy(testPolicy)
      .setPolicyFileEnabled(true)
      .setPolicyFileProtocol('')
      .setPolicyFileSendTestLog(false)
      .setPolicyFilePrimaryDest('')
      .setPolicyFileSecondaryDest('')
      .build();
    assert.expect(6);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedFilePolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 1, '1 section returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.fileSettings', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 2, 'first section has 2 properties');
    assert.equal(policyDetails[0].props.includes({
      name: 'adminUsm.policies.detail.protocol',
      value: ''
    }), false, 'protocol is ignored since it does not have a value');
    assert.equal(policyDetails[0].props.includes({
      name: 'adminUsm.policies.detail.primaryDestination',
      value: ''
    }), false, 'primary dest is ignored since it does not have a value');
    assert.equal(policyDetails[0].props.includes({
      name: 'adminUsm.policies.detail.secondaryDestination',
      value: ''
    }), false, 'secondary dest is ignored since it does not have a value');
  });

  test('Group ranking default file policy view', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy(testPolicyAndOrigins)
      .build();
    assert.expect(6);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedFilePolicy(Immutable.from(state), policyForDetails.policy);
    assert.equal(policyDetails.length, 1, '1 section returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.fileSettings', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 3, '3 properties returned as expected in endpoint server settings');
    assert.equal(policyDetails[0].props[1].value, 'TLS', 'Protocol value shows as expected');
    assert.equal(policyDetails[0].props[2].origin.groupName, 'test', 'groupName returned as expected');
    assert.equal(policyDetails[0].props[2].origin.policyName, 'test', 'policyName returned as expected');
  });

  const testPolicyAndNoOrigins = {
    ...testPolicyAndOrigins,
    origins: { }
  };

  test('Group ranking default windows policy view with empty origins', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy(testPolicyAndNoOrigins)
      .build();
    assert.expect(6);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedFilePolicy(Immutable.from(state), policyForDetails.policy);
    assert.equal(policyDetails.length, 1, '1 section returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.fileSettings', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 3, '3 properties returned as expected in endpoint server settings');
    assert.equal(policyDetails[0].props[1].value, 'TLS', 'Protocol value shows as expected');
    assert.equal(policyDetails[0].props[2].origin.groupName, '', 'groupName returned as expected');
    assert.equal(policyDetails[0].props[2].origin.policyName, '', 'policyName returned as expected');
  });

  test('focusedPolicy', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy(testPolicyAndNoOrigins)
      .build();
    assert.expect(6);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedFilePolicy(Immutable.from(state), policyForDetails.policy);
    assert.equal(policyDetails.length, 1, '1 section returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.fileSettings', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 3, '3 properties returned as expected in endpoint server settings');
    assert.equal(policyDetails[0].props[1].value, 'TLS', 'Protocol value shows as expected');
    assert.equal(policyDetails[0].props[2].origin.groupName, '', 'groupName returned as expected');
    assert.equal(policyDetails[0].props[2].origin.policyName, '', 'policyName returned as expected');
  });

  test('focusedPolicy result with no origins', function(assert) {
    const state = {
      usm: {
        policies: {
          focusedItem: {
            name: 'foo'
          }
        }
      }
    };
    const policyExpected = { name: 'foo' };
    assert.deepEqual(focusedPolicy(Immutable.from(state)), policyExpected, 'focusedPolicy policyyResult is as policyExpected when no origins are present');
  });

  test('focusedPolicy result with origins', function(assert) {
    const state = {
      usm: {
        policies: {
          focusedItem: {
            policy: { name: 'foo2' },
            origins: { group: 'test' }
          }
        }
      }
    };
    const policyExpected = { name: 'foo2' };
    assert.deepEqual(focusedPolicy(Immutable.from(state)), policyExpected, 'focusedPolicy policyyResult is as policyExpected when origins are present');
  });
});
