import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, click, settled, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import hostListState from '../state/host.machines';
import endpoint from '../state/schema';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import HostCreators from 'investigate-hosts/actions/data-creators/host';
import RiskCreators from 'investigate-shared/actions/data-creators/risk-creators';
import sinon from 'sinon';

let deleteHostsSpy, startScanSpy, stopScanSpy, resetRiskScoreSpy, setState;

const spys = [];


const callback = () => {};
const e = {
  clientX: 20,
  clientY: 20,
  view: {
    window: {
      innerWidth: 100,
      innerHeight: 100
    }
  }
};
const wormhole = 'wormhole-context-menu';

const endpointServer = {
  serviceData: [
    {
      id: 'fef38f60-cf50-4d52-a4a9-7727c48f1a4b',
      name: 'endpoint-server',
      displayName: 'EPS1-server - Endpoint Server',
      host: '10.40.15.210',
      port: 7050,
      useTls: true,
      version: '11.3.0.0',
      family: 'launch',
      meta: {}
    },
    {
      id: '364e8e9c-5893-4ad1-b107-3c6b8d87b088',
      name: 'endpoint-broker-server',
      displayName: 'EPS2-server - Endpoint Broker Server',
      host: '10.40.15.199',
      port: 7054,
      useTls: true,
      version: '11.3.0.0',
      family: 'launch',
      meta: {}
    },
    {
      id: 'e82241fc-0681-4276-a930-dd6e5d00f152',
      name: 'endpoint-server',
      displayName: 'EPS2-server - Endpoint Server',
      host: '10.40.15.199',
      port: 7050,
      useTls: true,
      version: '11.3.0.0',
      family: 'launch',
      meta: {}
    }
  ],
  isServicesLoading: false,
  isServicesRetrieveError: false,
  isSummaryRetrieveError: false
};

const endpointQuery = {
  serverId: 'e82241fc-0681-4276-a930-dd6e5d00f152'
};
const endpointState =
  {
    endpoint:
    {
      schema: { schema: endpoint.schema },
      machines: {
        hostList: hostListState.machines.hostList, selectedHostList: [ { version: '11.3', managed: true, id: 'C1C6F9C1-74D1-43C9-CBD4-289392F6442F' }],
        hostColumnSort: 'machineIdentity.machineName',
        focusedHost: null
      }
    },
    preferences: {
      preferences: {
        machinePreference: {
          visibleColumns: [
            'id',
            'machineIdentity.agentVersion',
            'machine.scanStartTime',
            'machineIdentity.machineOsType'
          ]
        }
      }
    },
    endpointServer,
    endpointQuery
  };

const dummySelectedHostList = new Array(101)
  .join().split(',')
  .map(function(item, index) {
    return { index: { id: ++index, version: index, managed: true } };
  });

const selectedMoreHostsState =
  {
    endpoint:
    {
      schema: { schema: endpoint.schema },
      machines: {
        hostList: hostListState.machines.hostList, selectedHostList: dummySelectedHostList,
        hostColumnSort: 'machineIdentity.machineName',
        focusedHost: null
      }
    },
    preferences: {
      preferences: {
        machinePreference: {
          visibleColumns: [
            'id',
            'machineIdentity.agentVersion',
            'machine.scanStartTime',
            'machineIdentity.machineOsType'
          ]
        }
      }
    },
    endpointServer,
    endpointQuery
  };
spys.push(
  deleteHostsSpy = sinon.stub(HostCreators, 'deleteHosts'),
  resetRiskScoreSpy = sinon.stub(RiskCreators, 'resetRiskScore')
);

module('Integration | Component | host-list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');

    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    spys.push(
      startScanSpy = sinon.stub(HostCreators, 'startScan'),
      stopScanSpy = sinon.stub(HostCreators, 'stopScan'));
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);
  });

  hooks.afterEach(function() {
    spys.forEach((s) => {
      s.restore();
    });
    const wormholeElement = document.querySelector('#wormhole-context-menu');
    if (wormholeElement) {
      document.querySelector('#ember-testing').removeChild(wormholeElement);
    }
  });

  hooks.after(function() {
    spys.forEach((s) => {
      s.restore();
    });
  });

  test('it renders error page when endpointserver is offline', async function(assert) {
    const endpointServerState = { endpointServer: { ...endpointServer, isSummaryRetrieveError: true }, endpointQuery: { ...endpointQuery } };
    setState(endpointServerState);
    await render(hbs`{{host-list}}`);
    assert.equal(findAll('.host-list-items').length, 0, 'host list is not rendered');
    assert.equal(findAll('.error-page').length, 1, 'endpoint server is offline');
  });

  test('it renders error page when endpointserver is online', async function(assert) {
    setState(endpointState);
    await render(hbs`{{host-list}}`);
    assert.equal(findAll('.error-page').length, 0, 'endpoint server is online');
  });

  test('it renders host action bar by default', async function(assert) {
    await render(hbs`{{host-list}}`);
    assert.equal(findAll('.host-table__toolbar').length, 1, 'host table action bar is rendered by default');
  });

  test('on row click/unclick it is highlighted', async function(assert) {
    setState(endpointState);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-list
        openProperties=openProperties
        closeProperties=closeProperties}}`);
    await click(findAll('.rsa-data-table-body-row')[1]);
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'One row highlighted');
    await click(findAll('.rsa-data-table-body-row')[1]);
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 0, 'Row highlight removed');
  });

  test('on selecting the checkbox row is not highlighted', async function(assert) {
    setState(endpointState);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-list
        openProperties=openProperties
        closeProperties=closeProperties}}`);
    await click(findAll('.rsa-form-checkbox')[1]);
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 0, 'One row not highlighted');
  });

  test('On right clicking the row it renders the context menu', async function(assert) {
    setState(endpointState);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-list
        openProperties=openProperties
        closeProperties=closeProperties}}{{context-menu}}`);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 5, 'Context menu not rendered');
    });
  });
  test('For more than 100 hosts selection and On right click start scan and stop scan options should disable', async function(assert) {
    setState(selectedMoreHostsState);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-list
        openProperties=openProperties
        closeProperties=closeProperties}}{{context-menu}}`);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items[2].classList.contains('context-menu__item--disabled'), true, 'Context menu start scan option disabled');
      assert.equal(items[3].classList.contains('context-menu__item--disabled'), true, 'Context menu stop scan option disabled');
    });
  });

  test('row is getting selected on right click', async function(assert) {
    setState(endpointState);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-list
        openProperties=openProperties
        closeProperties=closeProperties}}{{context-menu}}`);

    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(() => {
      assert.equal(findAll('.checked').length, 2, 'Row checkbox is selected');
    });
  });

  test('on clicking the Start Scan it opens scan modal ', async function(assert) {
    setState(endpointState);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      <div id='modalDestination'></div>
      {{host-list
        openProperties=openProperties
        closeProperties=closeProperties}}
      {{context-menu}}
    `);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await click(`#${menuItems[2].id}`); // START_SCAN
      return settled().then(() => {
        assert.equal(document.querySelectorAll('#modalDestination .scan-command-modal').length, 1);
      });
    });
  });

  test('on clicking the Delete host modal opens ', async function(assert) {
    setState(endpointState);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      <div id='modalDestination'></div>
      {{host-list
        openProperties=openProperties
        closeProperties=closeProperties}}
      {{context-menu}}
    `);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await click(`#${menuItems[1].id}`);
      return settled().then(() => {
        assert.equal(document.querySelectorAll('#modalDestination .confirmation-modal').length, 1);
      });
    });
  });

  test('delete host action is called', async function(assert) {
    setState(endpointState);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      <div id='modalDestination'></div>
      {{host-list
        openProperties=openProperties
        closeProperties=closeProperties}}
      {{context-menu}}
    `);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await click(`#${menuItems[1].id}`);
      return settled().then(async() => {
        assert.equal(document.querySelectorAll('#modalDestination .confirmation-modal').length, 1);
        await click(document.querySelector('.confirmation-modal .is-primary button'));
        assert.equal(deleteHostsSpy.callCount, 1, 'Delete action is called once');
      });
    });
  });

  test('start scan action is getting called', async function(assert) {
    setState(endpointState);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      <div id='modalDestination'></div>
      {{host-list
        openProperties=openProperties
        closeProperties=closeProperties}}
      {{context-menu}}
    `);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await click(`#${menuItems[2].id}`); // START_SCAN
      return settled().then(async() => {
        await click(document.querySelector('.scan-command button'));
        assert.equal(startScanSpy.callCount, 1, 'Start Scan action is called');
      });
    });
  });

  test('stop scan action is getting called', async function(assert) {
    setState(endpointState);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      <div id='modalDestination'></div>
      {{host-list
        openProperties=openProperties
        closeProperties=closeProperties}}
      {{context-menu}}
    `);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await click(`#${menuItems[3].id}`); // START_SCAN
      return settled().then(async() => {
        await click(document.querySelector('.scan-command button'));
        assert.equal(stopScanSpy.callCount, 1, 'Stop Scan action is called');
      });
    });
  });

  test('reset risk score action is getting called', async function(assert) {
    setState(endpointState);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      <div id='modalDestination'></div>
      {{host-list
        openProperties=openProperties
        closeProperties=closeProperties}}
      {{context-menu}}
    `);
    triggerEvent(findAll('.score')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const menuItems = findAll(`${selector} > .context-menu__item`);
      await click(`#${menuItems[4].id}`); // RESET_RISK_SCORE
      return settled().then(async() => {
        await click(document.querySelector('.resetButton button'));
        assert.equal(resetRiskScoreSpy.callCount, 1, 'Reset Score action is called');
      });
    });
  });

  test('on right clicking the machine name context menu not rendered', async function(assert) {
    setState(endpointState);
    this.set('closeProperties', () => {
      assert.ok(true);
    });
    this.set('openProperties', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-list
        openProperties=openProperties
        closeProperties=closeProperties}}{{context-menu}}`);
    triggerEvent('.content-context-menu a', 'contextmenu', e);
    return settled().then(() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 0, 'Context menu not rendered');
    });
  });
});
