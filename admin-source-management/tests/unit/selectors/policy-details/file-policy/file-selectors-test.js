import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import _ from 'lodash';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import dataFile from '../../../../data/subscriptions/groups/fetchRankingView/dataFile';

import {
  focusedPolicy,
  selectedFilePolicy
} from 'admin-source-management/reducers/usm/policy-details/file-policy/file-selectors';

const [defaultFilePolicyAndOrigins] = dataFile;
const [, filePolicyWithSourcesAndOrigins] = dataFile;

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

let setState;

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
          fileEncoding: 'UTF-8 / ASCII',
          paths: ['/c/apache_path-hint-1/*.log', '/c/Program Files/Apache Group/Apache[2-9]/*.log', 'apache_path-hint-2'],
          sourceName: 'Meta-Source-Name',
          exclusionFilters: ['exclude-string-1', 'exclude-string-2', 'exclude-string-3']
        },
        {
          fileType: 'exchange',
          enabled: true,
          startOfEvents: true,
          fileEncoding: 'UTF-8 / ASCII',
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
    assert.equal(policyDetails[1].props[1].value, 'Collect new data only', 'startOfEvents has expected value');

    // exchange source section
    assert.equal(policyDetails[2].header, 'adminUsm.policies.detail.sourceSettings', 'exchange source section header is as expected');
    assert.deepEqual(policyDetails[2].headerVars, { fileType: 'exchange' }, 'exchange source section headerVars is as expected');
    assert.equal(policyDetails[2].props.length, 6, 'exchange source section has 6 properties');
    // test the specific values for props that get translated
    assert.equal(policyDetails[2].props[0].value, 'Enabled', 'enabled property has expected value');
    assert.equal(policyDetails[2].props[1].value, 'Collect historical and new data', 'startOfEvents has expected value');
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
      .focusedPolicy({ ...defaultFilePolicyAndOrigins })
      .build();
    assert.expect(15);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedFilePolicy(Immutable.from(state), policyForDetails.policy);
    assert.equal(policyDetails.length, 1, '1 section returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.fileSettings', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 3, '3 properties returned as expected in file settings');
    // enabled prop
    assert.equal(policyDetails[0].props[0].name, 'adminUsm.policies.detail.filePolicyEnabled', 'enabled prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[0].value, 'Disabled', 'enabled prop value is Disabled as expected');
    assert.equal(policyDetails[0].props[0].origin.groupName, 'test', 'enabled prop groupName returned as expected');
    assert.equal(policyDetails[0].props[0].origin.policyName, 'test', 'enabled policyName returned as expected');
    // sendTestLog prop
    assert.equal(policyDetails[0].props[1].name, 'adminUsm.policies.detail.sendTestLog', 'sendTestLog prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[1].value, 'Disabled', 'sendTestLog prop value is Disabled as expected');
    assert.equal(policyDetails[0].props[1].origin.groupName, 'test', 'sendTestLog prop groupName returned as expected');
    assert.equal(policyDetails[0].props[1].origin.policyName, 'test', 'sendTestLog policyName returned as expected');
    // protocol prop
    assert.equal(policyDetails[0].props[2].name, 'adminUsm.policies.detail.protocol', 'protocol prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[2].value, 'TLS', 'protocol prop value is TLS as expected');
    assert.equal(policyDetails[0].props[2].origin.groupName, 'test', 'protocol prop groupName returned as expected');
    assert.equal(policyDetails[0].props[2].origin.policyName, 'test', 'protocol policyName returned as expected');
  });

  test('Group ranking file policy view all settings & sources', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy({ ...filePolicyWithSourcesAndOrigins })
      .build();
    assert.expect(53);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedFilePolicy(Immutable.from(state), policyForDetails.policy);
    assert.equal(policyDetails.length, 3, '1 fileSettings section & 2 sourceSettings sections returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.fileSettings', 'fileSettings section header is as expected');
    assert.equal(policyDetails[0].props.length, 5, '5 file connection settings');
    assert.equal(policyDetails[1].header, 'adminUsm.policies.detail.sourceSettings', '1st sourceSettings section header is as expected');
    assert.equal(policyDetails[1].headerVars.fileType, 'apache', '1st sourceSettings header fileType is apache');
    assert.equal(policyDetails[1].props.length, 6, '6 file connection settings for apache');
    assert.equal(policyDetails[2].header, 'adminUsm.policies.detail.sourceSettings', '2nd sourceSettings section header is as expected');
    assert.equal(policyDetails[2].headerVars.fileType, 'exchange', '2nd sourceSettings header fileType is exchange');
    assert.equal(policyDetails[2].props.length, 5, '5 file connection settings for exchange');
    // enabled prop
    assert.equal(policyDetails[0].props[0].name, 'adminUsm.policies.detail.filePolicyEnabled', 'enabled prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[0].value, 'Enabled', 'enabled prop value is Enabled as expected');
    assert.equal(policyDetails[0].props[0].origin.groupName, 'test', 'enabled prop groupName returned as expected');
    assert.equal(policyDetails[0].props[0].origin.policyName, 'test', 'enabled policyName returned as expected');
    // sendTestLog prop
    assert.equal(policyDetails[0].props[1].name, 'adminUsm.policies.detail.sendTestLog', 'sendTestLog prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[1].value, 'Disabled', 'sendTestLog prop value is Disabled as expected');
    assert.equal(policyDetails[0].props[1].origin.groupName, 'test', 'sendTestLog prop groupName returned as expected');
    assert.equal(policyDetails[0].props[1].origin.policyName, 'test', 'sendTestLog policyName returned as expected');
    // primaryDestination prop
    assert.equal(policyDetails[0].props[2].name, 'adminUsm.policies.detail.primaryDestination', 'primaryDestination prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[2].value, 'NWAPPLIANCE55555 - Log Server', 'primaryDestination prop value is NWAPPLIANCE55555 - Log Server as expected');
    assert.equal(policyDetails[0].props[2].origin.groupName, 'test', 'primaryDestination prop groupName returned as expected');
    assert.equal(policyDetails[0].props[2].origin.policyName, 'test', 'primaryDestination policyName returned as expected');
    // secondaryDestination prop
    assert.equal(policyDetails[0].props[3].name, 'adminUsm.policies.detail.secondaryDestination', 'secondaryDestination prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[3].value, 'NWAPPLIANCE113- Log Server', 'secondaryDestination prop value is NWAPPLIANCE113- Log Server as expected');
    assert.equal(policyDetails[0].props[3].origin.groupName, 'test', 'secondaryDestination prop groupName returned as expected');
    assert.equal(policyDetails[0].props[3].origin.policyName, 'test', 'secondaryDestination policyName returned as expected');
    // protocol prop
    assert.equal(policyDetails[0].props[4].name, 'adminUsm.policies.detail.protocol', 'protocol prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[4].value, 'TLS', 'protocol prop value is TLS as expected');
    assert.equal(policyDetails[0].props[4].origin.groupName, 'test', 'protocol prop groupName returned as expected');
    assert.equal(policyDetails[0].props[4].origin.policyName, 'test', 'protocol policyName returned as expected');

    // apache source enabled prop
    assert.equal(policyDetails[1].props[0].name, 'adminUsm.policyWizard.filePolicy.enableOnAgent', 'apache enabled prop i18n key name is as expected');
    assert.equal(policyDetails[1].props[0].value, 'Disabled', 'apache enabled prop value is Disabled as expected');
    assert.equal(policyDetails[1].props[0].origin.groupName, 'apache groupName', 'apache enabled prop groupName returned as expected');
    assert.equal(policyDetails[1].props[0].origin.policyName, 'apache policyName', 'apache enabled policyName returned as expected');
    // apache source startOfEvents prop
    assert.equal(policyDetails[1].props[1].name, 'adminUsm.policyWizard.filePolicy.dataCollection', 'apache startOfEvents prop i18n key name is as expected');
    assert.equal(policyDetails[1].props[1].value, 'Collect new data only', 'apache startOfEvents prop value is Collect new data only as expected');
    assert.equal(policyDetails[1].props[1].origin.groupName, 'apache groupName', 'apache startOfEvents prop groupName returned as expected');
    assert.equal(policyDetails[1].props[1].origin.policyName, 'apache policyName', 'apache startOfEvents policyName returned as expected');
    // apache source fileEncoding prop
    assert.equal(policyDetails[1].props[2].name, 'adminUsm.policyWizard.filePolicy.fileEncoding', 'apache fileEncoding prop i18n key name is as expected');
    assert.equal(policyDetails[1].props[2].value, 'UTF-8 / ASCII', 'apache fileEncoding prop value is UTF-8 / ASCII as expected');
    assert.equal(policyDetails[1].props[2].origin.groupName, 'apache groupName', 'apache fileEncoding prop groupName returned as expected');
    assert.equal(policyDetails[1].props[2].origin.policyName, 'apache policyName', 'apache fileEncoding policyName returned as expected');
    // apache source paths prop
    assert.equal(policyDetails[1].props[3].name, 'adminUsm.policyWizard.filePolicy.paths', 'apache paths prop i18n key name is as expected');
    assert.equal(policyDetails[1].props[3].value, '/c/apache_path-hint-1/*.log, /c/Program Files/Apache Group/Apache[2-9]/*.log, apache_path-hint-2', 'apache paths prop value is /c/apache_path-hint-1/*.log, /c/Program Files/Apache Group/Apache[2-9]/*.log, apache_path-hint-2 as expected');
    assert.equal(policyDetails[1].props[3].origin.groupName, 'apache groupName', 'apache paths prop groupName returned as expected');
    assert.equal(policyDetails[1].props[3].origin.policyName, 'apache policyName', 'apache paths policyName returned as expected');
    // apache source sourceName prop
    assert.equal(policyDetails[1].props[4].name, 'adminUsm.policyWizard.filePolicy.sourceName', 'apache sourceName prop i18n key name is as expected');
    assert.equal(policyDetails[1].props[4].value, 'Meta-Source-Name', 'apache sourceName prop value is Meta-Source-Name as expected');
    assert.equal(policyDetails[1].props[4].origin.groupName, 'apache groupName', 'apache sourceName prop groupName returned as expected');
    assert.equal(policyDetails[1].props[4].origin.policyName, 'apache policyName', 'apache sourceName policyName returned as expected');
    // apache source exclusionFilters prop
    assert.equal(policyDetails[1].props[5].name, 'adminUsm.policyWizard.filePolicy.exclusionFilters', 'apache exclusionFilters prop i18n key name is as expected');
    assert.equal(policyDetails[1].props[5].value, 'exclude-string-1, exclude-string-2, exclude-string-3', 'apache exclusionFilters prop value is exclude-string-1, exclude-string-2, exclude-string-3 as expected');
    assert.equal(policyDetails[1].props[5].origin.groupName, 'apache groupName', 'apache exclusionFilters prop groupName returned as expected');
    assert.equal(policyDetails[1].props[5].origin.policyName, 'apache policyName', 'apache exclusionFilters policyName returned as expected');
  });

  const defaultFilePolicyNoOrigins = {
    ...defaultFilePolicyAndOrigins,
    origins: { }
  };
  const filePolicyWithSourcesNoOrigins = {
    ...filePolicyWithSourcesAndOrigins,
    origins: { }
  };

  test('Group ranking default file policy view with empty origins', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy({ ...defaultFilePolicyNoOrigins })
      .build();
    assert.expect(15);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedFilePolicy(Immutable.from(state), policyForDetails.policy);
    assert.equal(policyDetails.length, 1, '1 section returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.fileSettings', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 3, '3 properties returned as expected in file settings');
    // enabled prop
    assert.equal(policyDetails[0].props[0].name, 'adminUsm.policies.detail.filePolicyEnabled', 'enabled prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[0].value, 'Disabled', 'enabled prop value is Disabled as expected');
    assert.equal(policyDetails[0].props[0].origin.groupName, '', 'enabled prop groupName empty as expected');
    assert.equal(policyDetails[0].props[0].origin.policyName, '', 'enabled policyName empty as expected');
    // sendTestLog prop
    assert.equal(policyDetails[0].props[1].name, 'adminUsm.policies.detail.sendTestLog', 'sendTestLog prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[1].value, 'Disabled', 'sendTestLog prop value is Disabled as expected');
    assert.equal(policyDetails[0].props[1].origin.groupName, '', 'sendTestLog prop groupName empty as expected');
    assert.equal(policyDetails[0].props[1].origin.policyName, '', 'sendTestLog policyName empty as expected');
    // protocol prop
    assert.equal(policyDetails[0].props[2].name, 'adminUsm.policies.detail.protocol', 'protocol prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[2].value, 'TLS', 'protocol prop value is TLS as expected');
    assert.equal(policyDetails[0].props[2].origin.groupName, '', 'protocol prop groupName empty as expected');
    assert.equal(policyDetails[0].props[2].origin.policyName, '', 'protocol policyName empty as expected');
  });

  test('Group ranking file policy view all settings & sources all with empty origins', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizWinLogLogServers()
      .focusedPolicy({ ...filePolicyWithSourcesNoOrigins })
      .build();
    assert.expect(53);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedFilePolicy(Immutable.from(state), policyForDetails.policy);
    assert.equal(policyDetails.length, 3, '1 fileSettings section & 2 sourceSettings sections returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.fileSettings', 'fileSettings section header is as expected');
    assert.equal(policyDetails[0].props.length, 5, '5 file connection settings');
    assert.equal(policyDetails[1].header, 'adminUsm.policies.detail.sourceSettings', '1st sourceSettings section header is as expected');
    assert.equal(policyDetails[1].headerVars.fileType, 'apache', '1st sourceSettings header fileType is apache');
    assert.equal(policyDetails[1].props.length, 6, '6 file connection settings for apache');
    assert.equal(policyDetails[2].header, 'adminUsm.policies.detail.sourceSettings', '2nd sourceSettings section header is as expected');
    assert.equal(policyDetails[2].headerVars.fileType, 'exchange', '2nd sourceSettings header fileType is exchange');
    assert.equal(policyDetails[2].props.length, 5, '5 file connection settings for exchange');
    // enabled prop
    assert.equal(policyDetails[0].props[0].name, 'adminUsm.policies.detail.filePolicyEnabled', 'enabled prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[0].value, 'Enabled', 'enabled prop value is Enabled as expected');
    assert.equal(policyDetails[0].props[0].origin.groupName, '', 'enabled prop groupName empty as expected');
    assert.equal(policyDetails[0].props[0].origin.policyName, '', 'enabled policyName empty as expected');
    // sendTestLog prop
    assert.equal(policyDetails[0].props[1].name, 'adminUsm.policies.detail.sendTestLog', 'sendTestLog prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[1].value, 'Disabled', 'sendTestLog prop value is Disabled as expected');
    assert.equal(policyDetails[0].props[1].origin.groupName, '', 'sendTestLog prop groupName empty as expected');
    assert.equal(policyDetails[0].props[1].origin.policyName, '', 'sendTestLog policyName empty as expected');
    // primaryDestination prop
    assert.equal(policyDetails[0].props[2].name, 'adminUsm.policies.detail.primaryDestination', 'primaryDestination prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[2].value, 'NWAPPLIANCE55555 - Log Server', 'primaryDestination prop value is NWAPPLIANCE55555 - Log Server as expected');
    assert.equal(policyDetails[0].props[2].origin.groupName, '', 'primaryDestination prop groupName empty as expected');
    assert.equal(policyDetails[0].props[2].origin.policyName, '', 'primaryDestination policyName empty as expected');
    // secondaryDestination prop
    assert.equal(policyDetails[0].props[3].name, 'adminUsm.policies.detail.secondaryDestination', 'secondaryDestination prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[3].value, 'NWAPPLIANCE113- Log Server', 'secondaryDestination prop value is NWAPPLIANCE113- Log Server as expected');
    assert.equal(policyDetails[0].props[3].origin.groupName, '', 'secondaryDestination prop groupName empty as expected');
    assert.equal(policyDetails[0].props[3].origin.policyName, '', 'secondaryDestination policyName empty as expected');
    // protocol prop
    assert.equal(policyDetails[0].props[4].name, 'adminUsm.policies.detail.protocol', 'protocol prop i18n key name is as expected');
    assert.equal(policyDetails[0].props[4].value, 'TLS', 'protocol prop value is TLS as expected');
    assert.equal(policyDetails[0].props[4].origin.groupName, '', 'protocol prop groupName empty as expected');
    assert.equal(policyDetails[0].props[4].origin.policyName, '', 'protocol policyName empty as expected');

    // apache source enabled prop
    assert.equal(policyDetails[1].props[0].name, 'adminUsm.policyWizard.filePolicy.enableOnAgent', 'apache enabled prop i18n key name is as expected');
    assert.equal(policyDetails[1].props[0].value, 'Disabled', 'apache enabled prop value is Disabled as expected');
    assert.equal(policyDetails[1].props[0].origin.groupName, '', 'apache enabled prop groupName empty as expected');
    assert.equal(policyDetails[1].props[0].origin.policyName, '', 'apache enabled policyName empty as expected');
    // apache source startOfEvents prop
    assert.equal(policyDetails[1].props[1].name, 'adminUsm.policyWizard.filePolicy.dataCollection', 'apache startOfEvents prop i18n key name is as expected');
    assert.equal(policyDetails[1].props[1].value, 'Collect new data only', 'apache startOfEvents prop value is Collect new data only as expected');
    assert.equal(policyDetails[1].props[1].origin.groupName, '', 'apache startOfEvents prop groupName empty as expected');
    assert.equal(policyDetails[1].props[1].origin.policyName, '', 'apache startOfEvents policyName empty as expected');
    // apache source fileEncoding prop
    assert.equal(policyDetails[1].props[2].name, 'adminUsm.policyWizard.filePolicy.fileEncoding', 'apache fileEncoding prop i18n key name is as expected');
    assert.equal(policyDetails[1].props[2].value, 'UTF-8 / ASCII', 'apache fileEncoding prop value is UTF-8 / ASCII as expected');
    assert.equal(policyDetails[1].props[2].origin.groupName, '', 'apache fileEncoding prop groupName empty as expected');
    assert.equal(policyDetails[1].props[2].origin.policyName, '', 'apache fileEncoding policyName empty as expected');
    // apache source paths prop
    assert.equal(policyDetails[1].props[3].name, 'adminUsm.policyWizard.filePolicy.paths', 'apache paths prop i18n key name is as expected');
    assert.equal(policyDetails[1].props[3].value, '/c/apache_path-hint-1/*.log, /c/Program Files/Apache Group/Apache[2-9]/*.log, apache_path-hint-2', 'apache paths prop value is /c/apache_path-hint-1/*.log, /c/Program Files/Apache Group/Apache[2-9]/*.log, apache_path-hint-2 as expected');
    assert.equal(policyDetails[1].props[3].origin.groupName, '', 'apache paths prop groupName empty as expected');
    assert.equal(policyDetails[1].props[3].origin.policyName, '', 'apache paths policyName empty as expected');
    // apache source sourceName prop
    assert.equal(policyDetails[1].props[4].name, 'adminUsm.policyWizard.filePolicy.sourceName', 'apache sourceName prop i18n key name is as expected');
    assert.equal(policyDetails[1].props[4].value, 'Meta-Source-Name', 'apache sourceName prop value is Meta-Source-Name as expected');
    assert.equal(policyDetails[1].props[4].origin.groupName, '', 'apache sourceName prop groupName empty as expected');
    assert.equal(policyDetails[1].props[4].origin.policyName, '', 'apache sourceName policyName empty as expected');
    // apache source exclusionFilters prop
    assert.equal(policyDetails[1].props[5].name, 'adminUsm.policyWizard.filePolicy.exclusionFilters', 'apache exclusionFilters prop i18n key name is as expected');
    assert.equal(policyDetails[1].props[5].value, 'exclude-string-1, exclude-string-2, exclude-string-3', 'apache exclusionFilters prop value is exclude-string-1, exclude-string-2, exclude-string-3 as expected');
    assert.equal(policyDetails[1].props[5].origin.groupName, '', 'apache exclusionFilters prop groupName empty as expected');
    assert.equal(policyDetails[1].props[5].origin.policyName, '', 'apache exclusionFilters policyName empty as expected');
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
