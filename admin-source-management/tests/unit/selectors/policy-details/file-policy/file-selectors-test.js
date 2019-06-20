import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import _ from 'lodash';
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

  test('selectedFilePolicy: basic settings', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy({ ...testPolicy })
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

  test('selectedFilePolicy: sources settings', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .policyWizFileSourceTypes()
      .focusedPolicy({ ...testPolicy })
      .setPolicyFileSources([
        {
          fileType: 'apache',
          enabled: false,
          startOfEvents: false,
          fileEncoding: 'UTF-8',
          paths: ['/c/apache_path-hint-1/*.log', '/c/Program Files/Apache Group/Apache[2-9]/*.log', 'apache_path-hint-2'],
          sourceName: 'Meta-Source-Name',
          exclusionFilters: ['exclude-string-1', 'exclude-string-2', 'exclude-string-3']
        },
        {
          fileType: 'exchange',
          enabled: true,
          startOfEvents: true,
          fileEncoding: 'UTF-8',
          paths: ['/[cd]/exchange/logs/*.log'],
          sourceName: 'Exchange aye!',
          exclusionFilters: ['exclude-string-1', 'exclude-string-2', 'exclude-string-3']
        }
      ])
      .build();
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedFilePolicy(Immutable.from(state), policyForDetails);

    assert.expect(11);
    // should be 1 file settings section + 2 source settings sections
    assert.equal(policyDetails.length, 3, '1 file settings section + 2 source settings sections as expected');

    // apache source section
    assert.equal(policyDetails[1].header, 'adminUsm.policies.detail.sourceSettings', 'apache source section header is as expected');
    assert.deepEqual(policyDetails[1].headerVars, { fileType: 'apache' }, 'apache source section headerVars is as expected');
    assert.equal(policyDetails[1].props.length, 6, 'apache source section has 6 properties');
    // test the specific values for props that get translated
    assert.equal(policyDetails[1].props[0].value, 'Disabled', 'enabled property has expected value');
    assert.equal(policyDetails[1].props[1].value, 'Collect historical and new data', 'startOfEvents has expected value');

    // exchange source section
    assert.equal(policyDetails[2].header, 'adminUsm.policies.detail.sourceSettings', 'exchange source section header is as expected');
    assert.deepEqual(policyDetails[2].headerVars, { fileType: 'exchange' }, 'exchange source section headerVars is as expected');
    assert.equal(policyDetails[2].props.length, 6, 'exchange source section has 6 properties');
    // test the specific values for props that get translated
    assert.equal(policyDetails[2].props[0].value, 'Enabled', 'enabled property has expected value');
    assert.equal(policyDetails[2].props[1].value, 'Collect new data only', 'startOfEvents has expected value');
  });

  test('selectedFilePolicy: basic settings - ignores blank values', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy({ ...testPolicy })
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
    assert.equal(
      _.find(policyDetails[0].props, ['name', 'adminUsm.policies.detail.protocol']),
      undefined, 'protocol is ignored since it does not have a value'
    );
    assert.equal(
      _.find(policyDetails[0].props, ['name', 'adminUsm.policies.detail.primaryDestination']),
      undefined, 'primary dest is ignored since it does not have a value'
    );
    assert.equal(
      _.find(policyDetails[0].props, ['name', 'adminUsm.policies.detail.secondaryDestination']),
      undefined, 'secondary dest is ignored since it does not have a value'
    );
  });

  test('selectedFilePolicy: sources settings - ignores blank values', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .policyWizFileSourceTypes()
      .focusedPolicy({ ...testPolicy })
      .setPolicyFileSources([
        {
          fileType: 'apache',
          enabled: false,
          startOfEvents: false,
          fileEncoding: '',
          paths: [],
          sourceName: '',
          exclusionFilters: []
        }
      ])
      .build();
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedFilePolicy(Immutable.from(state), policyForDetails);

    assert.expect(8);
    // should be 1 file settings section + 1 source settings sections
    assert.equal(policyDetails.length, 2, '1 file settings section + 1 source settings sections as expected');

    // apache source section
    assert.equal(policyDetails[1].header, 'adminUsm.policies.detail.sourceSettings', 'apache source section header is as expected');
    assert.deepEqual(policyDetails[1].headerVars, { fileType: 'apache' }, 'apache source section headerVars is as expected');
    assert.equal(policyDetails[1].props.length, 2, 'apache source section has 2 properties');
    // test the specific props that should NOT be present
    assert.equal(
      _.find(policyDetails[1].props, ['name', 'adminUsm.policyWizard.filePolicy.fileEncoding']),
      undefined, 'fileEncoding is ignored since it does not have a value'
    );
    assert.equal(
      _.find(policyDetails[1].props, ['name', 'adminUsm.policyWizard.filePolicy.paths']),
      undefined, 'paths is ignored since it does not have a value'
    );
    assert.equal(
      _.find(policyDetails[1].props, ['name', 'adminUsm.policyWizard.filePolicy.sourceName']),
      undefined, 'sourceName is ignored since it does not have a value'
    );
    assert.equal(
      _.find(policyDetails[1].props, ['name', 'adminUsm.policyWizard.filePolicy.exclusionFilters']),
      undefined, 'exclusionFilters is ignored since it does not have a value'
    );
  });

  test('Group ranking default file policy view', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy({ ...testPolicyAndOrigins })
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
      .focusedPolicy({ ...testPolicyAndNoOrigins })
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
