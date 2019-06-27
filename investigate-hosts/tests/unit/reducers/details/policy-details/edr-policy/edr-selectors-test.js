import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../../helpers/patch-reducer';

import {
  general,
  selectedEdrPolicy
} from 'investigate-hosts/reducers/details/policy-details/edr-policy/edr-selectors';

let setState;

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

  const policyData = {
    serviceId: '63de7bb3-fcd3-415a-97bf-7639958cd5e6',
    serviceName: 'Node-X - Endpoint Server',
    usmRevision: 0,
    groups: [],
    policy: {
      edrPolicy: {
        name: 'Default EDR Policy',
        transportConfig: {
          primary: {
            address: '10.40.15.154',
            httpsPort: 443,
            httpsBeaconIntervalInSeconds: 900,
            udpPort: 444,
            udpBeaconIntervalInSeconds: 30
          }
        },
        agentMode: 'ADVANCED',
        scheduledScanConfig: {
          enabled: true,
          recurrentSchedule: {
            recurrence: {
              interval: 1,
              unit: 'DAYS'
            },
            runAtTime: '09:00:00',
            runOnDaysOfWeek: [1],
            scheduleStartDate: '2019-03-22'
          },
          scanOptions: {
            cpuMax: 25,
            cpuMaxVm: 10,
            scanMbr: false }
        },
        blockingConfig: {
          enabled: false
        },
        storageConfig: {
          diskCacheSizeInMb: 100
        },
        serverConfig: {
          requestScanOnRegistration: false
        }
      }
    },
    policyStatus: 'Testing',
    evaluatedTime: '2019-05-07T05:25:41.109+0000'
  };


  test('selectedEdrPolicy selector', function(assert) {
    const state = new ReduxDataHelper(setState).policy(policyData).build();
    const policyDetails = selectedEdrPolicy(Immutable.from(state));
    assert.equal(policyDetails.length, 5, '5 sections returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policyWizard.edrPolicy.scanSchedule', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 6, 'first section has 6 properties');
    assert.equal(policyDetails[0].props[1].value, '09:00', 'Scan time correct');
    assert.equal(policyDetails[1].header, 'adminUsm.policyWizard.edrPolicy.agentSettings', 'second section  is as expected');
    assert.equal(policyDetails[1].props.length, 1, 'second section has 1 property');
    assert.equal(policyDetails[2].header, 'adminUsm.policyWizard.edrPolicy.advScanSettings', 'third section  is as expected');
    assert.equal(policyDetails[2].props.length, 2, 'third section has 2 properties');
    assert.equal(policyDetails[3].header, 'adminUsm.policyWizard.edrPolicy.invasiveActions', 'fourth section  is as expected');
    assert.equal(policyDetails[3].props.length, 1, 'fourth section has 1 property');
    assert.equal(policyDetails[0].props[3].value, 'Every 1 day(s) on Monday', 'Every 1 day(s) on Monday value is shows');
    assert.equal(policyDetails[4].header, 'adminUsm.policyWizard.edrPolicy.endpointServerSettings', 'fifth section  is as expected');
    assert.equal(policyDetails[4].props.length, 5, 'fifth section has 5 properties');
  });

  const { policy } = policyData;
  const { edrPolicy } = policyData.policy;
  const { scheduledScanConfig } = policyData.policy.edrPolicy;
  const { recurrentSchedule } = policyData.policy.edrPolicy.scheduledScanConfig;
  const runOnDaysOfWeekData = {
    ...policyData,
    policy: {
      ...policy,
      edrPolicy: {
        ...edrPolicy,
        scheduledScanConfig: {
          ...scheduledScanConfig,
          recurrentSchedule: {
            ...recurrentSchedule,
            runAtTime: '10:00:00',
            runOnDaysOfWeek: [5],
            scheduleStartDate: '2020-03-22'
          }
        }
      }
    }
  };

  test('selectedEdrPolicy selector with new runOnDaysOfWeekData', function(assert) {
    const state = new ReduxDataHelper(setState).policy(runOnDaysOfWeekData).build();
    const policyDetails = selectedEdrPolicy(Immutable.from(state));
    assert.equal(policyDetails[0].props[1].value, '10:00', 'Scan time correct');
    assert.equal(policyDetails[0].props[3].value, 'Every 1 day(s) on Friday', 'Every 1 day(s) on Friday value is shows');
  });

  test('general selector', function(assert) {
    const state = new ReduxDataHelper(setState).policy(policyData).build();
    const policyDetails = general(Immutable.from(state));
    assert.equal(policyDetails.evaluatedTime, '2019-05-07T05:25:41.109+0000', 'evaluatedTime time correct');
  });
});