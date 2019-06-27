import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';

let setState;

module('Integration | Component | property-panel-policy/edr-policy', function(hooks) {
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
      }
    },
    policyStatus: 'Updated',
    evaluatedTime: '2019-05-07T05:25:41.109+0000'
  };
  const { policy } = policyData;
  const { edrPolicy } = policyData.policy;
  const { scheduledScanConfig } = policyData.policy.edrPolicy;
  const { recurrentSchedule } = policyData.policy.edrPolicy.scheduledScanConfig;
  const scheduledScanConfigDisabled = {
    ...policyData,
    policy: {
      ...policy,
      edrPolicy: {
        ...edrPolicy,
        scheduledScanConfig: {
          ...scheduledScanConfig,
          enabled: false
        }
      }
    }
  };

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
            runOnDaysOfWeek: [2],
            scheduleStartDate: '2020-03-22'
          }
        }
      }
    }
  };

  test('it renders', async function(assert) {
    new ReduxDataHelper(setState).policy(policyData).build();
    await render(hbs`{{property-panel-policy/edr-policy}}`);
    assert.equal(findAll('.edr-policy').length, 1, 'should rend the component');
  });

  test('Content data', async function(assert) {
    new ReduxDataHelper(setState).policy(policyData).build();
    await render(hbs`{{property-panel-policy/edr-policy}}`);
    assert.equal(document.querySelectorAll('.edr-value').length, 15, 'All values are showing');
    assert.equal(document.querySelectorAll('.content-section__section-name')[0].textContent.trim(), 'Scan Schedule', 'Scan Schedule section shows');
    assert.equal(document.querySelectorAll('.property-name')[0].textContent.trim(), 'Run Scheduled Scan', 'Run Scheduled Scan lable shows');
    assert.equal(document.querySelectorAll('.edr-value .tooltip-text')[0].textContent.trim(), 'Enabled', 'Enabled value is showing');
    assert.equal(document.querySelectorAll('.content-section__section-name')[1].textContent.trim(), 'Agent Mode', 'Agent Mode section shows');
    assert.equal(document.querySelectorAll('.content-section__section-name')[2].textContent.trim(), 'Scan Settings', 'Scan Settings section shows');
    assert.equal(document.querySelectorAll('.content-section__section-name')[3].textContent.trim(), 'Response Action Settings', 'Response Action Settings section shows');
    assert.equal(document.querySelectorAll('.content-section__section-name')[4].textContent.trim(), 'Endpoint Server Settings', 'Endpoint Server Settings section shows');

    assert.equal(document.querySelectorAll('.property-name')[1].textContent.trim(), 'Start Time', 'Start Time lable shows');
    assert.equal(document.querySelectorAll('.property-name')[2].textContent.trim(), 'Effective Date', 'Effective Date lable shows');
    assert.equal(document.querySelectorAll('.property-name')[3].textContent.trim(), 'Scan Frequency', 'Scan Frequency lable shows');
    assert.equal(document.querySelectorAll('.property-name')[4].textContent.trim(), 'CPU Maximum', 'CPU Maximum lable shows');
    assert.equal(document.querySelectorAll('.property-name')[5].textContent.trim(), 'Virtual Machine Maximum', 'Virtual Machine Maximum lable shows');

    assert.equal(document.querySelectorAll('.edr-value .tooltip-text')[1].textContent.trim(), '09:00', '09:00:00 value is shows');
    assert.equal(document.querySelectorAll('.edr-value .tooltip-text')[2].textContent.trim(), '2019-03-22', '2019-03-22 value is shows');
    assert.equal(document.querySelectorAll('.edr-value .tooltip-text')[3].textContent.trim(), 'Every 1 day(s) on Monday', 'Every 1 day(s) on Monday value is shows');
    assert.equal(document.querySelectorAll('.edr-value .tooltip-text')[4].textContent.trim(), '25 %', '25 % value is shows');
    assert.equal(document.querySelectorAll('.edr-value .tooltip-text')[5].textContent.trim(), '10 %', '10 % value is shows');

  });

  test('Content data scheduledScanConfigDisabled', async function(assert) {
    new ReduxDataHelper(setState).policy(scheduledScanConfigDisabled).build();
    await render(hbs`{{property-panel-policy/edr-policy}}`);
    assert.equal(document.querySelectorAll('.edr-value').length, 10, 'All values are showing minus five fields in Scan Schedule');
    assert.equal(document.querySelectorAll('.edr-value .tooltip-text')[0].textContent.trim(), 'Disabled', 'Disabled value is showing');
  });

  test('Content data runOnDaysOfWeekData', async function(assert) {
    new ReduxDataHelper(setState).policy(runOnDaysOfWeekData).build();
    await render(hbs`{{property-panel-policy/edr-policy}}`);
    assert.equal(document.querySelectorAll('.edr-value').length, 15, 'All values are showing');
    assert.equal(document.querySelectorAll('.edr-value .tooltip-text')[1].textContent.trim(), '10:00', '10:00:00 value is shows');
    assert.equal(document.querySelectorAll('.edr-value .tooltip-text')[2].textContent.trim(), '2020-03-22', '2020-03-22 value is shows');
    assert.equal(document.querySelectorAll('.edr-value .tooltip-text')[3].textContent.trim(), 'Every 1 day(s) on Tuesday', 'Every 1 day(s) on Tuesday value is shows');
    assert.equal(document.querySelectorAll('.edr-value .tooltip-text')[4].textContent.trim(), '25 %', '25 % value is shows');
    assert.equal(document.querySelectorAll('.edr-value .tooltip-text')[5].textContent.trim(), '10 %', '10 % value is shows');
  });
});
