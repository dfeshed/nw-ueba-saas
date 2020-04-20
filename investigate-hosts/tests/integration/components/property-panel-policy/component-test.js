import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, findAll, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';

let setState;

module('Integration | Component | property panel policy', function(hooks) {
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

  const hostList = [{ version: '11.4.0.0' }];

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
        enabled: false,
        sendTestLog: false,
        primaryDestination: '',
        secondaryDestination: '',
        protocol: 'TLS',
        sources: []
      }
    },
    policyStatus: 'Updated',
    evaluatedTime: '2019-05-07T05:25:41.109+0000'
  };

  test('it renders', async function(assert) {
    new ReduxDataHelper(setState).selectedHostList(hostList).policy(policyData).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(findAll('.host-property-panel').length, 1, 'should rend the component');
    assert.equal(findAll('.blue').length, 3, 'All three acordions are rendering');
    assert.equal(findAll('.blue.windows-accordion').length, 1, '.windows-accordion acordion is rendering');
    assert.equal(findAll('.blue.file-accordion').length, 1, 'file-accordion acordion is rendering');
  });

  const { policy } = policyData;
  const noWindowsLogPolicy = {
    ...policyData,
    policy: {
      ...policy,
      windowsLogPolicy: undefined
    }
  };

  const noFilePolicies = {
    ...policyData,
    policy: {
      ...policy,
      filePolicy: undefined
    }
  };

  const noWindowsLogAndFilePolicies = {
    ...policyData,
    policy: {
      ...policy,
      windowsLogPolicy: undefined,
      filePolicy: undefined
    }
  };

  test('noWindowsLogPolicy renders', async function(assert) {
    new ReduxDataHelper(setState).policy(noWindowsLogPolicy).selectedHostList(hostList).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(findAll('.host-property-panel').length, 1, 'should rend the component');
    assert.equal(findAll('.blue').length, 2, 'two acordions are rendering');
    assert.equal(findAll('.blue.windows-accordion').length, 0, 'windows-accordion acordion is not rendering');
    assert.equal(findAll('.blue.file-accordion').length, 1, 'file-accordion acordion is rendering');
  });

  test('noFilePolicies renders', async function(assert) {
    new ReduxDataHelper(setState).policy(noFilePolicies).selectedHostList(hostList).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(findAll('.host-property-panel').length, 1, 'should rend the component');
    assert.equal(findAll('.blue').length, 2, 'two acordions are rendering');
    assert.equal(findAll('.blue.windows-accordion').length, 1, 'windows-accordion acordion is rendering');
    assert.equal(findAll('.blue.file-accordion').length, 0, 'file-accordion acordion is not rendering');
  });

  test('noWindowsLogAndFilePolicies renders', async function(assert) {
    new ReduxDataHelper(setState).policy(noWindowsLogAndFilePolicies).selectedHostList(hostList).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(findAll('.host-property-panel').length, 1, 'should rend the component');
    assert.equal(findAll('.blue').length, 1, 'one acordion is rendering');
    assert.equal(findAll('.blue.windows-accordion').length, 0, 'windows-accordion acordion is not rendering');
    assert.equal(findAll('.blue.file-accordion').length, 0, 'file-accordion acordion is not rendering');
  });

  test('General data', async function(assert) {
    new ReduxDataHelper(setState).policy(policyData).selectedHostList(hostList).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(document.querySelectorAll('.content-section__section-name')[0].textContent.trim(), 'GENERAL', 'GENERAL section shows');
    assert.equal(document.querySelectorAll('.property-name')[0].textContent.trim(), 'Evaluated Time', 'Evaluated Time lable shows');
    assert.equal(document.querySelectorAll('.property-value').length, 1, 'value is showing');
    assert.equal(document.querySelectorAll('.property-value .tooltip-text')[0].textContent.trim(), '2019-05-07T05:25:41.109+0000', 'evaluatedTime value shows');
  });

  test('on clicking agent-accordion', async function(assert) {
    new ReduxDataHelper(setState).policy(policyData).selectedHostList(hostList).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(document.querySelectorAll('.agent-accordion .liquid-container').length, 1, 'agent-accordion did render');
    assert.equal(document.querySelectorAll('.agent-accordion .liquid-container .liquid-child').length, 0, 'agent-accordion is colapsed');
    await click('.agent-accordion h3');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.agent-accordion .liquid-container .liquid-child').length, 1, 'agent-accordion is expanded');
    });
  });

  test('on clicking windows-accordion', async function(assert) {
    new ReduxDataHelper(setState).policy(policyData).selectedHostList(hostList).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(document.querySelectorAll('.windows-accordion .liquid-container').length, 1, 'windows-accordion did render');
    assert.equal(document.querySelectorAll('.windows-accordion .liquid-container .liquid-child').length, 0, 'windows-accordion is colapsed');
    await click('.windows-accordion h3');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.windows-accordion .liquid-container .liquid-child').length, 1, 'windows-accordion is expanded');
    });
  });

  const hostListSupported = [{ version: '11.5.0.0' }];
  test('on clicking file-accordion', async function(assert) {
    new ReduxDataHelper(setState).policy(policyData).selectedHostList(hostListSupported).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(document.querySelectorAll('.file-accordion .liquid-container').length, 1, 'hostListSupported file-accordion did render');
    assert.equal(document.querySelectorAll('.file-accordion .liquid-container .liquid-child').length, 0, 'file-accordion is colapsed');
    await click('.file-accordion h3');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.file-accordion .liquid-container .liquid-child').length, 1, 'file-accordion is expanded');
    });
  });

  test('agentVersionSupported IS supporting file-accordion', async function(assert) {
    new ReduxDataHelper(setState).selectedHostList(hostList).policy(policyData).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(document.querySelectorAll('.file-accordion .liquid-container').length, 1, 'file-accordion DID render');
  });

  const hostListNotSupported = [{ version: '11.3.0.0' }];
  test('hostListNotSupported NOT supporting file-accordion and supporting edr', async function(assert) {
    new ReduxDataHelper(setState).selectedHostList(hostListNotSupported).policy(policyData).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(document.querySelectorAll('.file-accordion .liquid-container').length, 0, 'file-accordion did NOT render');
    assert.equal(document.querySelectorAll('.agent-accordion .liquid-container').length, 1, 'agent-accordion-accordion did render');
  });

  const hostListSupported12 = [{ version: '12.1.0.0' }];
  test('hostListSupported12 12.x supporting edr-and file-accordions', async function(assert) {
    new ReduxDataHelper(setState).selectedHostList(hostListSupported12).policy(policyData).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(document.querySelectorAll('.agent-accordion .liquid-container').length, 1, 'agent-accordion-accordion render on 12.x');
    assert.equal(document.querySelectorAll('.file-accordion .liquid-container').length, 1, 'file-accordion render on 12.x');
  });

  const hostListNotSupported11 = [{ version: '11.2.0.0' }];
  test('hostListNotSupported11 NOT supporting edr-and-file-accordions', async function(assert) {
    new ReduxDataHelper(setState).selectedHostList(hostListNotSupported11).policy(policyData).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(document.querySelectorAll('.agent-accordion .liquid-container').length, 0, 'agent-accordion-accordion did NOT render');
    assert.equal(document.querySelectorAll('.rsa-content-warn-text-box').length, 1, 'rsa-content-warn-text-box did render');
    assert.equal(document.querySelectorAll('.file-accordion .liquid-container').length, 0, 'test 2 file-accordion did NOT render');
  });

  const hostListNotSupported2 = [{ version: '10.4.0.0' }];
  test('hostListNotSupported2 NOT supporting file-accordion', async function(assert) {
    new ReduxDataHelper(setState).selectedHostList(hostListNotSupported2).policy(policyData).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(document.querySelectorAll('.file-accordion .liquid-container').length, 0, 'test 2 file-accordion did NOT render');
  });
});
