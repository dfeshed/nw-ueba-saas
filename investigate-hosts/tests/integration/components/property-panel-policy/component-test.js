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

  test('it renders', async function(assert) {
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(findAll('.host-property-panel').length, 1, 'should rend the component');
    assert.equal(findAll('.blue').length, 3, 'All three acordions are rendering');
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

  test('General data', async function(assert) {
    new ReduxDataHelper(setState).policy(policyData).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(document.querySelectorAll('.content-section__section-name')[0].textContent.trim(), 'GENERAL', 'GENERAL section shows');
    assert.equal(document.querySelectorAll('.property-name')[0].textContent.trim(), 'Evaluated Time', 'Evaluated Time lable shows');
    assert.equal(document.querySelectorAll('.property-value').length, 1, 'value is showing');
    assert.equal(document.querySelectorAll('.property-value .tooltip-text')[0].textContent.trim(), '2019-05-07T05:25:41.109+0000', 'evaluatedTime value shows');
  });

  test('on clicking agent-accordion', async function(assert) {
    new ReduxDataHelper(setState).policy(policyData).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(document.querySelectorAll('.agent-accordion .liquid-container').length, 1, 'agent-accordion did render');
    assert.equal(document.querySelectorAll('.agent-accordion .liquid-container .liquid-child').length, 0, 'agent-accordion is colapsed');
    await click('.agent-accordion h3');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.agent-accordion .liquid-container .liquid-child').length, 1, 'agent-accordion is expanded');
    });
  });

  test('on clicking windows-accordion', async function(assert) {
    new ReduxDataHelper(setState).policy(policyData).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(document.querySelectorAll('.windows-accordion .liquid-container').length, 1, 'windows-accordion did render');
    assert.equal(document.querySelectorAll('.windows-accordion .liquid-container .liquid-child').length, 0, 'windows-accordion is colapsed');
    await click('.windows-accordion h3');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.windows-accordion .liquid-container .liquid-child').length, 1, 'windows-accordion is expanded');
    });
  });

  test('on clicking file-accordion', async function(assert) {
    new ReduxDataHelper(setState).policy(policyData).build();
    await render(hbs`{{property-panel-policy}}`);
    assert.equal(document.querySelectorAll('.file-accordion .liquid-container').length, 1, 'file-accordion did render');
    assert.equal(document.querySelectorAll('.file-accordion .liquid-container .liquid-child').length, 0, 'file-accordion is colapsed');
    await click('.file-accordion h3');
    return settled().then(() => {
      assert.equal(document.querySelectorAll('.file-accordion .liquid-container .liquid-child').length, 1, 'file-accordion is expanded');
    });
  });

});
