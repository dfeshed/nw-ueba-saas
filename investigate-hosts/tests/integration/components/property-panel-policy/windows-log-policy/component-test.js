import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | property-panel-policy/windows-log-policy', function(hooks) {
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
      }
    },
    policyStatus: 'Updated',
    evaluatedTime: '2019-05-07T05:25:41.109+0000'
  };
  const { policy } = policyData;
  const { windowsLogPolicy } = policyData.policy;
  const windowsLogPolicyDisabled = {
    ...policyData,
    policy: {
      ...policy,
      windowsLogPolicy: {
        ...windowsLogPolicy,
        enabled: false
      }
    }
  };

  test('it renders', async function(assert) {
    new ReduxDataHelper(setState).policy(policyData).build();
    await render(hbs`{{property-panel-policy/windows-log-policy}}`);
    assert.equal(findAll('.windows-log-policy').length, 1, 'should rend the component');
  });

  test('Content data', async function(assert) {
    new ReduxDataHelper(setState).policy(policyData).build();
    await render(hbs`{{property-panel-policy/windows-log-policy}}`);
    assert.equal(document.querySelectorAll('.win-value').length, 4, 'All values are showing');
    assert.equal(document.querySelectorAll('.content-section__section-name')[0].textContent.trim(), 'Windows Log Settings', 'Windows Log Settings section shows');
    assert.equal(document.querySelectorAll('.property-name')[0].textContent.trim(), 'Status', 'Status lable shows');
    assert.equal(document.querySelectorAll('.win-value .tooltip-text')[0].textContent.trim(), 'Enabled', 'Enabled value is showing');
    assert.equal(document.querySelectorAll('.content-section__section-name')[1].textContent.trim(), 'Channel Filter Settings', 'Channel Filter Settings section shows');

    assert.equal(document.querySelectorAll('.property-name')[1].textContent.trim(), 'Protocol', 'Protocol lable shows');

    assert.equal(document.querySelectorAll('.win-value .tooltip-text')[1].textContent.trim(), 'TLS', 'TLS value shows');
    assert.equal(document.querySelectorAll('.win-value .tooltip-text')[2].textContent.trim(), 'Enabled', 'Enabled value  shows');


  });

  test('Content data windowsLogPolicyDisabled', async function(assert) {
    new ReduxDataHelper(setState).policy(windowsLogPolicyDisabled).build();
    await render(hbs`{{property-panel-policy/windows-log-policy}}`);
    assert.equal(document.querySelectorAll('.win-value').length, 2, 'All values are showing minus fields below Disabled');
    assert.equal(document.querySelectorAll('.win-value .tooltip-text')[0].textContent.trim(), 'Disabled', 'Disabled value is showing');
  });
});
