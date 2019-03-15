import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../helpers/patch-reducer';

import {
  focusedPolicy,
  selectedEdrPolicy
} from 'admin-source-management/reducers/usm/policy-details/edr-policy/edr-selectors';

let setState;

const testPolicy = {
  id: 'policy_014',
  policyType: 'edrPolicy',
  name: 'EMC Reston! 014',
  description: 'EMC Reston 014 of policy policy_014',
  dirty: false,
  defaultPolicy: false,
  lastPublishedOn: 1527489158739,
  createdBy: 'admin',
  createdOn: 1540318426092,
  lastModifiedBy: 'admin',
  lastModifiedOn: 1540318426092,
  lastPublishedCopy: null,
  associatedGroups: [],
  scanType: 'ENABLED',
  scanStartDate: null,
  scanStartTime: null,
  recurrenceInterval: 1,
  recurrenceUnit: 'WEEKS',
  runOnDaysOfWeek: ['WEDNESDAY', 'THURSDAY'],
  cpuMax: 75,
  cpuMaxVm: 85,
  primaryAddress: '10.10.10.10',
  primaryNwServiceId: 'id1',
  primaryHttpsPort: 443,
  primaryHttpsBeaconInterval: 3,
  primaryHttpsBeaconIntervalUnit: 'HOURS',
  primaryUdpPort: 444,
  primaryUdpBeaconInterval: 3,
  primaryUdpBeaconIntervalUnit: 'MINUTES'
};

module('Unit | Selectors | Policy Details | EDR Policy | EDR Selectors', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('selectedEdrPolicy selector', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy(testPolicy)
      .setPolicyScanMbr(false)
      .setPolicyRequestScan(true)
      .setPolicyBlockingEnabled(false)
      .setPolicyAgentMode('ADVANCED')
      .build();
    assert.expect(12);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 5, '5 sections returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policyWizard.edrPolicy.scanSchedule', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 4, 'first section has 4 properties');
    assert.equal(policyDetails[0].props[1].value, 'Every 1 week(s) on WEDNESDAY,THURSDAY', `Scan Frequency property has ${policyDetails[0].props[1].value} value`);
    assert.equal(policyDetails[1].header, 'adminUsm.policyWizard.edrPolicy.agentSettings', 'second section  is as expected');
    assert.equal(policyDetails[1].props.length, 1, 'second section has 1 property');
    assert.equal(policyDetails[2].header, 'adminUsm.policyWizard.edrPolicy.advScanSettings', 'third section  is as expected');
    assert.equal(policyDetails[2].props.length, 2, 'third section has 2 properties');
    assert.equal(policyDetails[3].header, 'adminUsm.policyWizard.edrPolicy.invasiveActions', 'fourth section  is as expected');
    assert.equal(policyDetails[3].props.length, 1, 'fourth section has 1 property');
    assert.equal(policyDetails[4].header, 'adminUsm.policyWizard.edrPolicy.endpointServerSettings', 'fifth section  is as expected');
    assert.equal(policyDetails[4].props.length, 5, 'fifth section has 5 properties');
  });

  test('selectedEdrPolicy, ignore blank properties', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy(testPolicy)
      .setPolicyScanMbr(false)
      .setPolicyRequestScan(true)
      .setPolicyBlockingEnabled(false)
      .setPolicyPrimaryAddress('')
      .setPolicyAgentMode('ADVANCED')
      .build();
    assert.expect(8);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 5, '5 sections returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policyWizard.edrPolicy.scanSchedule', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 4, 'first section has 4 properties');
    assert.equal(policyDetails[0].props.includes({
      name: 'adminUsm.policyWizard.edrPolicy.scanStartDate',
      value: ''
    }), false, 'scanStart date is ignored since it does not have a value');
    assert.equal(policyDetails[0].props.includes({
      name: 'adminUsm.policyWizard.edrPolicy.startTime',
      value: ''
    }), false, 'scanStart time is ignored since it does not have a value');
    assert.equal(policyDetails[4].header, 'adminUsm.policyWizard.edrPolicy.endpointServerSettings', 'fourth section is as expected');
    assert.equal(policyDetails[4].props.length, 4, 'fifth section has 4 properties');
    assert.equal(policyDetails[4].props.includes({
      name: 'adminUsm.policyWizard.edrPolicy.primaryAddress',
      value: ''
    }), false, 'primary address is ignored since it does not have a value');
  });

  test('selectedEdrPolicy no scan schedule section', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy(testPolicy)
      .setPolicyScanMbr(false)
      .setPolicyRequestScan(true)
      .setPolicyBlockingEnabled(false)
      .setPolicyAgentMode('ADVANCED')
      .setPolicyScanType('')
      .setPolicyScanStartDate('')
      .setPolicyScanStartTime('')
      .setPolicyRecurInterval('')
      .setPolicyRecurUnit('')
      .setPolicyRunDaysOfWeek([])
      .setPolicyCpuMax('')
      .setPolicyCpuVm('')
      .build();
    assert.expect(2);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 4, '4 sections returned as expected');
    assert.equal(policyDetails.includes({
      header: 'adminUsm.policyWizard.edrPolicy.scanSchedule',
      props: []
    }), false, 'No Scan schedule section as expected');
  });

  test('selectedEdrPolicy no scan settings section', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy(testPolicy)
      .setPolicyBlockingEnabled(true)
      .setPolicyAgentMode('ADVANCED')
      .build();
    assert.expect(2);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 4, '4 sections returned as expected');
    assert.equal(policyDetails.includes({
      header: 'adminUsm.policyWizard.edrPolicy.advScanSettings',
      props: []
    }), false, 'No Scan settings section as expected');
  });

  test('selectedEdrPolicy no response action settings section', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy(testPolicy)
      .setPolicyScanMbr(false)
      .setPolicyRequestScan(true)
      .setPolicyAgentMode('ADVANCED')
      .build();
    assert.expect(2);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 4, '4 sections returned as expected');
    assert.equal(policyDetails.includes({
      header: 'adminUsm.policyWizard.edrPolicy.invasiveActions',
      props: []
    }), false, 'No Response Action settings section as expected');
  });

  test('selectedEdrPolicy no endpoint settings section', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy(testPolicy)
      .setPolicyScanType('ENABLED')
      .setPolicyScanMbr(false)
      .setPolicyRequestScan(true)
      .setPolicyBlockingEnabled(false)
      .setPolicyAgentMode('ADVANCED')
      .setPolicyPrimaryAddress('')
      .setPolicyPrimaryHttpsPort('')
      .setPolicyPrimaryUdpPort('')
      .setPolicyPrimaryUdpBeaconInterval('')
      .setPolicyPrimaryUdpBeaconIntervalUnit('')
      .setPolicyPrimaryHttpsBeaconInterval('')
      .setPolicyPrimaryHttpsBeaconIntervalUnit('')
      .build();
    assert.expect(2);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 4, '4 sections returned as expected');
    assert.equal(policyDetails.includes({
      header: 'adminUsm.policyWizard.edrPolicy.endpointServerSettings',
      props: []
    }), false, 'No Endpoint server settings section as expected');
  });

  test('selectedEdrPolicy no agent settings section', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy(testPolicy)
      .setPolicyScanMbr(false)
      .setPolicyRequestScan(true)
      .setPolicyBlockingEnabled(false)
      .build();
    assert.expect(3);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 4, '4 sections returned as expected');
    assert.equal(policyDetails.includes({
      header: 'adminUsm.policyWizard.edrPolicy.agentSettings',
      props: []
    }), false, 'No agent settings section as expected');
    assert.equal(policyDetails.includes({
      header: 'adminUsm.policyWizard.edrPolicy.advancedConfigSettings',
      props: []
    }), false, 'No Advanced configuration settings section as expected');
  });

  test('selectedEdrPolicy has advanced config settings section', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy(testPolicy)
      .setPolicyScanMbr(false)
      .setPolicyRequestScan(true)
      .setPolicyBlockingEnabled(false)
      .setPolicyPrimaryHttpsBeaconInterval(5)
      .setPolicyPrimaryHttpsBeaconIntervalUnit('MINUTES')
      .setPolicyPrimaryUdpBeaconInterval(5)
      .setPolicyPrimaryUdpBeaconIntervalUnit('SECONDS')
      .setPolicyAgentMode('ADVANCED')
      .setPolicyCustomConfig('"trackingConfig": {"uniqueFilterSeconds": 28800,"beaconStdDev": 2.0}')
      .build();
    assert.expect(5);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 6, '6 sections returned as expected');
    assert.equal(policyDetails[5].props.length, 1, '1 property returned as expected');
    assert.equal(policyDetails[5].props[0].value.nonTruncated,
      '"trackingConfig": {"uniqueFilterSeconds": 28800,"beaconStdDev": 2.0}',
      'custom setting value returned as expected');
    assert.equal(policyDetails[4].props[0].value, '5 Minutes', 'https beacon interval unit is as expected');
    assert.equal(policyDetails[4].props[1].value, '5 Seconds', 'udp beacon interval unit is as expected');
  });

  test('default EDR policy has default endpoint server', function(assert) {
    const state = new ReduxDataHelper(setState)
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy(testPolicy)
      .setPolicyDefaultPolicy(true)
      .setPolicyScanType('ENABLED')
      .setPolicyScanMbr(false)
      .setPolicyRequestScan(true)
      .setPolicyBlockingEnabled(false)
      .setRarEnabled('ENABLED')
      .setPolicyPrimaryAddress('')
      .setPolicyPrimaryHttpsBeaconInterval(5)
      .setPolicyPrimaryHttpsBeaconIntervalUnit('MINUTES')
      .setPolicyPrimaryUdpBeaconInterval(5)
      .setPolicyPrimaryUdpBeaconIntervalUnit('SECONDS')
      .setPolicyAgentMode('ADVANCED')
      .setPolicyCustomConfig('')
      .build();
    assert.expect(5);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 5, '5 sections returned as expected');
    assert.equal(policyDetails[4].props.length, 4, '4 properties returned as expected in endpoint server settings');
    assert.equal(policyDetails[4].props[0].value, 'As Per Packager', 'default value returned as expected');
    assert.equal(policyDetails[4].props[0].defaultEndpointServer, true, 'flag set as expected');
    assert.equal(policyDetails[4].props[0].tooltip,
      'The agent will communicate to the endpoint server defined in the agent packager.',
      'default endpoint server tooltip is as expected');
  });
});