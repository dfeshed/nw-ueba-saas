import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

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
    const state = {
      usm: {
        policies: {
          focusedItem: {
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
            agentMode: 'FULL_MONITORING'
          }
        }
      }
    };
    assert.expect(12);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 5, '5 sections returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policyWizard.edrPolicy.scanSchedule', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 4, 'first section has 4 properties');
    assert.equal(policyDetails[0].props[1].value, 'Every 1 week(s) on WEDNESDAY,THURSDAY', `Scan Frequency property has ${policyDetails[0].props[1].value} value`);
    assert.equal(policyDetails[1].header, 'adminUsm.policyWizard.edrPolicy.advScanSettings', 'second section  is as expected');
    assert.equal(policyDetails[1].props.length, 2, 'second section has 2 properties');
    assert.equal(policyDetails[2].header, 'adminUsm.policyWizard.edrPolicy.invasiveActions', 'third section  is as expected');
    assert.equal(policyDetails[2].props.length, 1, 'third section has 1 property');
    assert.equal(policyDetails[3].header, 'adminUsm.policyWizard.edrPolicy.endpointServerSettings', 'fourth section  is as expected');
    assert.equal(policyDetails[3].props.length, 5, 'fourth section has 5 properties');
    assert.equal(policyDetails[4].header, 'adminUsm.policyWizard.edrPolicy.agentSettings', 'fifth section  is as expected');
    assert.equal(policyDetails[4].props.length, 1, 'fifth section has 1 property');
  });

  test('selectedEdrPolicy, ignore blank properties', function(assert) {
    const state = {
      usm: {
        policies: {
          focusedItem: {
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
            agentMode: 'FULL_MONITORING'
          }
        }
      }
    };
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
    assert.equal(policyDetails[3].header, 'adminUsm.policyWizard.edrPolicy.endpointServerSettings', 'fourth section is as expected');
    assert.equal(policyDetails[3].props.length, 4, 'fourth section has 4 properties');
    assert.equal(policyDetails[3].props.includes({
      name: 'adminUsm.policyWizard.edrPolicy.primaryAddress',
      value: ''
    }), false, 'primary address is ignored since it does not have a value');
  });

  test('selectedEdrPolicy no scan settings section', function(assert) {
    const state = {
      usm: {
        policies: {
          focusedItem: {
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
            agentMode: 'FULL_MONITORING'
          }
        }
      }
    };
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
    const state = {
      usm: {
        policies: {
          focusedItem: {
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
            agentMode: 'FULL_MONITORING'
          }
        }
      }
    };
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
    const state = {
      usm: {
        policies: {
          focusedItem: {
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
            agentMode: 'FULL_MONITORING'
          }
        }
      }
    };
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
    const state = {
      usm: {
        policies: {
          focusedItem: {
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
            agentMode: 'FULL_MONITORING'
          }
        }
      }
    };
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
    const state = {
      usm: {
        policies: {
          focusedItem: {
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
          }
        }
      }
    };
    assert.expect(2);
    const policyForDetails = focusedPolicy(Immutable.from(state));
    const policyDetails = selectedEdrPolicy(Immutable.from(state), policyForDetails);
    assert.equal(policyDetails.length, 4, '4 sections returned as expected');
    assert.equal(policyDetails.includes({
      header: 'adminUsm.policyWizard.edrPolicy.agentSettings',
      props: []
    }), false, 'No agent settings section as expected');
  });
});