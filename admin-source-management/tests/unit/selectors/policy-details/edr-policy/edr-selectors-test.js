import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

import {
  focusedPolicy,
  selectedEdrPolicy
} from 'admin-source-management/reducers/usm/policy-details/edr-policy/edr-selectors';

module('Unit | Selectors | Policy Details | EDR Policy | EDR Selectors', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('selectedEdrPolicy selector', function(assert) {
    const state = new ReduxDataHelper()
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy({
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
        // captureFloatingCode: true,
        downloadMbr: false,
        // filterSignedHooks: false,
        requestScanOnRegistration: true,
        blockingEnabled: false,
        primaryAddress: '10.10.10.10',
        primaryNwServiceId: 'id1',
        primaryHttpsPort: 443,
        primaryHttpsBeaconInterval: 3,
        primaryHttpsBeaconIntervalUnit: 'HOURS',
        primaryUdpPort: 444,
        primaryUdpBeaconInterval: 3,
        primaryUdpBeaconIntervalUnit: 'MINUTES',
        agentMode: 'ADVANCED'
      })
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
    const state = new ReduxDataHelper()
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy({
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
        scanType: 'ENABLED',
        scanStartDate: null,
        scanStartTime: null,
        recurrenceInterval: 1,
        recurrenceUnit: 'WEEKS',
        runOnDaysOfWeek: ['WEDNESDAY', 'THURSDAY'],
        cpuMax: 75,
        cpuMaxVm: 85,
        // captureFloatingCode: true,
        downloadMbr: false,
        // filterSignedHooks: false,
        requestScanOnRegistration: true,
        blockingEnabled: false,
        primaryAddress: '',
        primaryNwServiceId: 'id1',
        primaryHttpsPort: 443,
        primaryHttpsBeaconInterval: 3,
        primaryHttpsBeaconIntervalUnit: 'HOURS',
        primaryUdpPort: 444,
        primaryUdpBeaconInterval: 3,
        primaryUdpBeaconIntervalUnit: 'MINUTES',
        agentMode: 'ADVANCED'
      })
      .build();
    assert.expect(8);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 5, '5 sections returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policyWizard.edrPolicy.scanSchedule', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 4, 'first section has 4 properties');
    assert.equal(policyDetails[0].props.includes({
      name: 'adminUsm.policyWizard.edrPolicy.effectiveDate',
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

  test('selectedEdrPolicy no scan settings section', function(assert) {
    const state = new ReduxDataHelper()
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy({
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
        // captureFloatingCode: true,
        downloadMbr: false,
        // filterSignedHooks: false,
        requestScanOnRegistration: true,
        blockingEnabled: false,
        primaryAddress: '',
        primaryNwServiceId: 'id1',
        primaryHttpsPort: 443,
        primaryHttpsBeaconInterval: 3,
        primaryHttpsBeaconIntervalUnit: 'HOURS',
        primaryUdpPort: 444,
        primaryUdpBeaconInterval: 3,
        primaryUdpBeaconIntervalUnit: 'MINUTES',
        agentMode: 'ADVANCED'
      })
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

  test('selectedEdrPolicy no adv scan settings section', function(assert) {
    const state = new ReduxDataHelper()
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy({
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
        scanType: 'ENABLED',
        scanStartDate: null,
        scanStartTime: null,
        recurrenceInterval: 1,
        recurrenceUnit: 'WEEKS',
        runOnDaysOfWeek: ['WEDNESDAY', 'THURSDAY'],
        cpuMax: 75,
        cpuMaxVm: 85,
        blockingEnabled: false,
        primaryAddress: '',
        primaryNwServiceId: 'id1',
        primaryHttpsPort: 443,
        primaryHttpsBeaconInterval: 3,
        primaryHttpsBeaconIntervalUnit: 'MINUTES',
        primaryUdpPort: 444,
        primaryUdpBeaconInterval: 3,
        primaryUdpBeaconIntervalUnit: 'MINUTES',
        agentMode: 'ADVANCED'
      })
      .build();
    assert.expect(2);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 4, '4 sections returned as expected');
    assert.equal(policyDetails.includes({
      header: 'adminUsm.policyWizard.edrPolicy.advScanSettings',
      props: []
    }), false, 'No Advanced Scan settings section as expected');
  });

  test('selectedEdrPolicy no invasive action settings section', function(assert) {
    const state = new ReduxDataHelper()
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy({
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
        scanType: 'ENABLED',
        scanStartDate: null,
        scanStartTime: null,
        recurrenceInterval: 1,
        recurrenceUnit: 'WEEKS',
        runOnDaysOfWeek: ['WEDNESDAY', 'THURSDAY'],
        cpuMax: 75,
        cpuMaxVm: 85,
        // captureFloatingCode: true,
        downloadMbr: false,
        // filterSignedHooks: false,
        requestScanOnRegistration: true,
        primaryAddress: '',
        primaryNwServiceId: 'id1',
        primaryHttpsPort: 443,
        primaryHttpsBeaconInterval: 3,
        primaryHttpsBeaconIntervalUnit: 'HOURS',
        primaryUdpPort: 444,
        primaryUdpBeaconInterval: 3,
        primaryUdpBeaconIntervalUnit: 'MINUTES',
        agentMode: 'ADVANCED'
      })
      .build();
    assert.expect(2);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 4, '4 sections returned as expected');
    assert.equal(policyDetails.includes({
      header: 'adminUsm.policyWizard.edrPolicy.invasiveActions',
      props: []
    }), false, 'No Invasive Action settings section as expected');
  });

  test('selectedEdrPolicy no endpoint settings section', function(assert) {
    const state = new ReduxDataHelper()
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy({
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
        scanType: 'ENABLED',
        scanStartDate: null,
        scanStartTime: null,
        recurrenceInterval: 1,
        recurrenceUnit: 'WEEKS',
        runOnDaysOfWeek: ['WEDNESDAY', 'THURSDAY'],
        cpuMax: 75,
        cpuMaxVm: 85,
        // captureFloatingCode: true,
        downloadMbr: false,
        // filterSignedHooks: false,
        requestScanOnRegistration: true,
        blockingEnabled: false,
        agentMode: 'ADVANCED'
      })
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
    const state = new ReduxDataHelper()
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy({
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
        scanType: 'ENABLED',
        scanStartDate: null,
        scanStartTime: null,
        recurrenceInterval: 1,
        recurrenceUnit: 'WEEKS',
        runOnDaysOfWeek: ['WEDNESDAY', 'THURSDAY'],
        cpuMax: 75,
        cpuMaxVm: 85,
        // captureFloatingCode: true,
        downloadMbr: false,
        // filterSignedHooks: false,
        requestScanOnRegistration: true,
        blockingEnabled: false,
        primaryAddress: '',
        primaryNwServiceId: 'id1',
        primaryHttpsPort: 443,
        primaryHttpsBeaconInterval: 3,
        primaryHttpsBeaconIntervalUnit: 'HOURS',
        primaryUdpPort: 444,
        primaryUdpBeaconInterval: 3,
        primaryUdpBeaconIntervalUnit: 'MINUTES'
      })
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
    const state = new ReduxDataHelper()
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy({
        id: 'policy_003',
        policyType: 'edrPolicy',
        name: 'EMC Bangalore! 013',
        description: 'EMC Bangalore 013 of policy policy_013',
        dirty: true,
        defaultPolicy: false,
        lastPublishedOn: 1527489158739,
        createdBy: 'admin',
        createdOn: 1540318426092,
        lastModifiedBy: 'admin',
        lastModifiedOn: 1540318426092,
        associatedGroups: [],
        scanType: 'DISABLED',
        scanStartDate: null,
        scanStartTime: null,
        recurrenceInterval: null,
        recurrenceUnit: null,
        runOnDaysOfWeek: null,
        cpuMax: null,
        cpuMaxVm: null,
        downloadMbr: true,
        requestScanOnRegistration: false,
        blockingEnabled: false,
        primaryAddress: '10.10.10.12',
        primaryNwServiceId: 'id2',
        primaryHttpsPort: 443,
        primaryHttpsBeaconInterval: 5,
        primaryHttpsBeaconIntervalUnit: 'MINUTES',
        primaryUdpPort: 444,
        primaryUdpBeaconInterval: 5,
        primaryUdpBeaconIntervalUnit: 'SECONDS',
        agentMode: 'ADVANCED',
        customConfig: '"trackingConfig": {"uniqueFilterSeconds": 28800,"beaconStdDev": 2.0}'
      })
      .build();
    assert.expect(5);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 6, '6 sections returned as expected');
    assert.equal(policyDetails[5].props.length, 1, '1 property returned as expected');
    assert.equal(policyDetails[5].props[0].value.nonTruncated,
      '"trackingConfig": {"uniqueFilterSeconds": 28800,"beaconStdDev": 2.0}',
      'custom setting value returned as expected');
    assert.equal(policyDetails[4].props[2].value, '5 Minutes', 'https beacon interval unit is as expected');
    assert.equal(policyDetails[4].props[4].value, '5 Seconds', 'udp beacon interval unit is as expected');
  });

  test('default EDR policy has default endpoint server', function(assert) {
    const state = new ReduxDataHelper()
      .policyWiz()
      .policyWizEndpointServers()
      .focusedPolicy({
        id: '__default_edr_policy',
        policyType: 'edrPolicy',
        name: 'Default EDR Policy',
        description: 'Default EDR Policy __default_edr_policy',
        dirty: false,
        defaultPolicy: true,
        lastPublishedCopy: {},
        lastPublishedOn: 1540318459759,
        createdOn: 0,
        lastModifiedOn: 0,
        associatedGroups: [],
        scanType: 'ENABLED',
        scanStartDate: '2018-09-09',
        scanStartTime: '10:23',
        recurrenceInterval: 1,
        recurrenceUnit: 'DAYS',
        runOnDaysOfWeek: null,
        cpuMax: 75,
        cpuMaxVm: 85,
        // captureFloatingCode: false,
        downloadMbr: false,
        // filterSignedHooks: false,
        requestScanOnRegistration: false,
        blockingEnabled: false,
        primaryAddress: '',
        primaryNwServiceId: '',
        primaryHttpsPort: 443,
        primaryHttpsBeaconInterval: null,
        primaryHttpsBeaconIntervalUnit: null,
        primaryUdpPort: 444,
        primaryUdpBeaconInterval: null,
        primaryUdpBeaconIntervalUnit: null,
        agentMode: 'INSIGHTS'
      })
      .build();
    assert.expect(5);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 5, '5 sections returned as expected');
    assert.equal(policyDetails[4].props.length, 3, '3 properties returned as expected in endpoint server settings');
    assert.equal(policyDetails[4].props[0].value, 'As Per Packager', 'default value returned as expected');
    assert.equal(policyDetails[4].props[0].defaultEndpointServer, true, 'flag set as expected');
    assert.equal(policyDetails[4].props[0].tooltip,
      'The agent will communicate to the endpoint server defined in the agent packager.',
      'default endpoint server tooltip is as expected');
  });
});