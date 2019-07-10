import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../../helpers/patch-reducer';

import {
  selectedWindowsLogPolicy
} from 'investigate-hosts/reducers/details/policy-details/windows-log-policy/windows-log-selectors';

let setState;

module('Unit | Selectors | Policy Details | windows-log-policy | windows-log-selectors', function(hooks) {
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
      },
      windowsLogPolicy: {
        name: 'Default Windows Log Policy',
        enabled: true,
        sendTestLog: false,
        primaryDestination: '',
        secondaryDestination: '',
        protocol: 'TLS',
        channelFilters: [
          {
            channel: 'Security',
            eventId: '620,630,640',
            filterType: 'EXCLUDE'
          }
        ]
      }
    },
    policyStatus: 'Updated',
    evaluatedTime: '2019-05-07T05:25:41.109+0000'
  };

  test('selectedWindowsLogPolicy selector', function(assert) {
    const state = new ReduxDataHelper(setState).policy(policyData).build();
    const policyDetails = selectedWindowsLogPolicy(Immutable.from(state));
    assert.equal(policyDetails.length, 2, '2 sections returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.windowsLogSettings', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 3, 'first section has 3 properties');
    assert.equal(policyDetails[0].props[2].value, 'Enabled', 'Enabled correct');
    assert.equal(policyDetails[1].header, 'adminUsm.policies.detail.channelFilterSettings', 'second section  is as expected');
    assert.equal(policyDetails[1].channels.length, 1, 'second section has 1 channel');
    assert.equal(policyDetails[1].channels[0].value, '620,630,640', 'eventId value shows');
  });

  const { policy } = policyData;
  const { windowsLogPolicy } = policyData.policy;
  const runOnDaysOfWeekData = {
    ...policyData,
    policy: {
      ...policy,
      windowsLogPolicy: {
        ...windowsLogPolicy,
        enabled: false
      }
    }
  };

  test('selectedWindowsLogPolicy selector with data Disabled', function(assert) {
    const state = new ReduxDataHelper(setState).policy(runOnDaysOfWeekData).build();
    const policyDetails = selectedWindowsLogPolicy(Immutable.from(state));
    assert.equal(policyDetails[0].props.length, 1, 'first section has 1 properties');
    assert.equal(policyDetails[0].props[0].value, 'Disabled', 'Disabled correct');
  });
});