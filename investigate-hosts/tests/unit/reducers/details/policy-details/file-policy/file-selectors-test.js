import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../../helpers/patch-reducer';

import {
  selectedFilePolicy
} from 'investigate-hosts/reducers/details/policy-details/file-policy/file-selectors';

let setState;

module('Unit | Selectors | Policy Details | file-policy | file-selectors', function(hooks) {
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
        fileDownloadConfig: {
          criteria: 'Unsigned',
          maxSize: 10000,
          enabled: true
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
      },
      filePolicy: {
        name: 'Test File Policy',
        enabled: true,
        sendTestLog: false,
        primaryDestination: '',
        secondaryDestination: '',
        protocol: 'TLS',
        customConfig: { 'enabled': true, 'sendTestLog': false, 'protocol': 'UDP', 'policyType': 'filePolicy', 'name': 'Test File Policy', 'description': 'Test File Policy Description.' }
      }
    },
    policyStatus: 'Updated',
    evaluatedTime: '2019-05-07T05:25:41.109+0000'
  };

  test('selectedFilePolicy selector', function(assert) {
    const state = new ReduxDataHelper(setState).policy(policyData).build();
    const policyDetails = selectedFilePolicy(Immutable.from(state));
    assert.equal(policyDetails.length, 3, '3 sections returned as expected');
    assert.equal(policyDetails[0].header, 'adminUsm.policies.detail.fileSettings', 'first section is as expected');
    assert.equal(policyDetails[0].props.length, 3, 'first section has 3 properties');
    assert.equal(policyDetails[0].props[0].value, 'Enabled', 'Enabled filePolicy correct');
    assert.equal(policyDetails[0].props[2].value, 'Disabled', 'Disabled sendTestLog correct');
    assert.equal(policyDetails[1].header, 'adminUsm.policyWizard.filePolicy.advancedConfig', 'second section is as expected');
    assert.equal(policyDetails[1].props.length, 1, 'second section has 1 property');
    assert.equal(policyDetails[1].props[0].value, '{"enabled":true,"sendTestLog":false,"protocol":"UDP","policyType":"filePolicy","name":"Test File Policy","description":"Test File Policy Description."}', 'file advanced config as expected');
  });

  const { policy } = policyData;
  const { filePolicy } = policyData.policy;
  const filePolicyDidabled = {
    ...policyData,
    policy: {
      ...policy,
      filePolicy: {
        ...filePolicy,
        enabled: false
      }
    }
  };

  test('selectedFilePolicy selector with data Disabled', function(assert) {
    const state = new ReduxDataHelper(setState).policy(filePolicyDidabled).build();
    const policyDetails = selectedFilePolicy(Immutable.from(state));
    assert.equal(policyDetails.length, 1, '1 section returned as expected when filePolicy is disabled');
    assert.equal(policyDetails[0].props.length, 1, 'first and only section has 1 property');
    assert.equal(policyDetails[0].props[0].value, 'Disabled', 'Disabled filePolicy correct');
  });

  const filePolicySources = {
    ...policyData,
    policy: {
      ...policy,
      filePolicy: {
        ...filePolicy,
        sources: [
          {
            fileType: 'exchange',
            enabled: false,
            startOfEvents: true,
            fileEncoding: 'utf-8',
            paths: ['/*foo/bar*/*.txt'],
            sourceName: 'testSource3',
            exclusionFilters: ['exclude-string-1'],
            typeSpec: {
              parserId: 'file.exchange',
              processorType: 'generic',
              dataStartLine: '1',
              fieldDelim: '0x20'
            }
          },
          {
            fileType: 'apache',
            enabled: true,
            startOfEvents: true,
            fileEncoding: 'utf-8',
            paths: ['/*foo/bar*/*.txt'],
            sourceName: 'testSource2',
            exclusionFilters: ['exclude-string-1'],
            typeSpec: {
              parserId: 'file.apache',
              processorType: 'generic',
              dataStartLine: '1',
              fieldDelim: '0x20'
            }
          }
        ]
      }
    }
  };

  test('filePolicySources selector', function(assert) {
    const state = new ReduxDataHelper(setState).policy(filePolicySources).build();
    const policyDetails = selectedFilePolicy(Immutable.from(state));
    assert.equal(policyDetails.length, 4, '4 sections returned as expected');
    assert.equal(policyDetails[3].header, 'adminUsm.policies.detail.sourceSettings', 'sourceSettings section is as expected');
    assert.equal(policyDetails[3].props.length, 6, 'sourceSettings section has 6 properties when enabled');
    assert.equal(policyDetails[3].props[0].value, 'Enabled', 'Enabled apache fileType correct');
    assert.equal(policyDetails[3].props[1].value, 'Collect historical and new data', 'startOfEvents valus correct');
    assert.equal(policyDetails[3].props[2].value, 'utf-8', 'fileEncoding value is correct');
    assert.equal(policyDetails[3].props[3].value, '/*foo/bar*/*.txt', 'path value is correct');
    assert.equal(policyDetails[3].props[4].value, 'testSource2', 'sourceName value is correct');
    assert.equal(policyDetails[3].props[5].value, 'exclude-string-1', 'exclusionFilters value is correct');

    assert.equal(policyDetails[2].props.length, 1, 'sourceSettings section has 1 property when disabled');
    assert.equal(policyDetails[2].props[0].value, 'Disabled', 'exchange fileType is disabled');
  });

});
