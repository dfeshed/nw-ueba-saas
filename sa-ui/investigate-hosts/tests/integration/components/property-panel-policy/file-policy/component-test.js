import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | property-panel-policy/file-policy', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
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
      },
      filePolicy: {
        name: 'Test File Policy',
        enabled: true,
        sendTestLog: false,
        primaryDestination: '',
        secondaryDestination: '',
        protocol: 'TLS'
      }
    },
    policyStatus: 'Updated',
    evaluatedTime: '2019-05-07T05:25:41.109+0000'
  };
  const { policy } = policyData;
  const { filePolicy } = policyData.policy;
  const filePolicyyDisabled = {
    ...policyData,
    policy: {
      ...policy,
      filePolicy: {
        ...filePolicy,
        enabled: false
      }
    }
  };

  test('it renders', async function(assert) {
    new ReduxDataHelper(setState).policy(policyData).build();
    await render(hbs`{{property-panel-policy/file-policy}}`);
    assert.equal(findAll('.file-policies').length, 1, 'should rend the component');
  });

  test('it renders with undefined data', async function(assert) {
    new ReduxDataHelper(setState).policy(undefined).build();
    await render(hbs`{{property-panel-policy/file-policy}}`);
    assert.equal(findAll('.file-policies').length, 1, 'should rend the component with no errors');
  });

  test('Content data with no filePolicy sources', async function(assert) {
    new ReduxDataHelper(setState).policy(policyData).build();
    await render(hbs`{{property-panel-policy/file-policy}}`);
    assert.equal(document.querySelectorAll('.file-value').length, 3, 'All values are showing');
    assert.equal(document.querySelectorAll('.content-section__section-name')[0].textContent.trim(), 'Connection Settings', 'Connection Settings section shows');

    assert.equal(document.querySelectorAll('.content-section__section-name')[1].textContent.trim(), 'Source Settings (No sources set for collection)', 'No sources set for collection shows');
    assert.equal(document.querySelectorAll('.property-name')[0].textContent.trim(), 'Collect File Logs', 'Collect File Logs lable shows');
    assert.equal(document.querySelectorAll('.file-value .tooltip-text')[0].textContent.trim(), 'Enabled', 'Enabled value is showing');

    assert.equal(document.querySelectorAll('.property-name')[1].textContent.trim(), 'Protocol', 'Protocol lable shows');

    assert.equal(document.querySelectorAll('.file-value .tooltip-text')[1].textContent.trim(), 'TLS', 'TLS value shows');
    assert.equal(document.querySelectorAll('.file-value .tooltip-text')[2].textContent.trim(), 'Disabled', 'Disabled value  shows');
  });

  test('Content data filePolicyyDisabled', async function(assert) {
    new ReduxDataHelper(setState).policy(filePolicyyDisabled).build();
    await render(hbs`{{property-panel-policy/file-policy}}`);
    assert.equal(document.querySelectorAll('.file-value').length, 1, 'All values are showing minus fields below Disabled');
    assert.equal(document.querySelectorAll('.file-value .tooltip-text')[0].textContent.trim(), 'Disabled', 'Disabled value is showing');
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

  test('Content data with filePolicy sources', async function(assert) {
    new ReduxDataHelper(setState).policy(filePolicySources).build();
    await render(hbs`{{property-panel-policy/file-policy}}`);
    assert.equal(document.querySelectorAll('.file-value').length, 10, 'All 10 values are showing');
    assert.equal(document.querySelectorAll('.content-section__section-name')[0].textContent.trim(), 'Connection Settings', 'Connection Settings section shows');
    assert.equal(document.querySelectorAll('.content-section__section-name')[1].textContent.trim(), 'Source Settings (exchange)', 'exchange source shows');
    assert.equal(document.querySelectorAll('.content-section__section-name')[2].textContent.trim(), 'Source Settings (apache)', 'apache source shows');
    assert.equal(document.querySelectorAll('.property-name')[4].textContent.trim(), 'Collect Logs', 'Collect Logs label shows');
    assert.equal(document.querySelectorAll('.file-value .tooltip-text')[3].textContent.trim(), 'Disabled', 'Disabled value is showing');
    assert.equal(document.querySelectorAll('.property-name')[5].textContent.trim(), 'On First Connect', 'On First Connect label shows');
    assert.equal(document.querySelectorAll('.file-value .tooltip-text')[4].textContent.trim(), 'Enabled', 'Enabled value shows');
    assert.equal(document.querySelectorAll('.file-value .tooltip-text')[5].textContent.trim(), 'Collect historical and new data', 'Collect historical and new data value shows');
  });

});
