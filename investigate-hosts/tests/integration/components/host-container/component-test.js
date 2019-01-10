import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, find, click, settled } from '@ember/test-helpers';
import { patchReducer } from '../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import endpoint from '../state/schema';
import hostListState from '../state/host.machines';

let setState;

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
              hostList: hostListState.machines.hostList,
              selectedHostList: [],
              hostColumnSort: 'machineIdentity.machineName',
              activeHostListPropertyTab: 'HOST_DETAILS'
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

module('Integration | Component | host-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders host container', async function(assert) {
    await render(hbs`{{host-container}}`);
    assert.equal(findAll('.host-container').length, 1, 'host container rendered');
  });

  test('it renders filter panel and center container', async function(assert) {
    await render(hbs`{{host-container}}`);
    assert.equal(findAll('.rsa-data-filters').length, 1, 'filters rendered');
    assert.equal(findAll('.center-zone').length, 1, 'center content rendered');
    assert.equal(findAll('.right-zone').length, 1, 'host properties rendered');
  });

  test('it renders host container', async function(assert) {
    await render(hbs`{{host-container}}`);
    assert.equal(findAll('.host-container').length, 1, 'host container rendered');
  });

  test('it renders host container list', async function(assert) {
    const state = {
      endpoint: { detailsInput: { agentId: null } }
    };
    setState(state);
    await render(hbs`{{host-container}}`);
    assert.equal(findAll('.host-container-list').length, 1, 'host container list rendered');
  });

  test('risk score filter is rendered', async function(assert) {
    await render(hbs`{{host-container}}`);
    assert.equal(findAll('.filter-controls .range-filter').length, 1, 'Range filter (score) is present');
    assert.equal(find('.filter-controls .range-filter .filter-text').textContent, 'Risk Score', 'Filter name is Risk Score');
  });

  test('on selecting the row right panel is open/close', async function(assert) {
    setState(endpointState);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-container}}`);
    await click(findAll('.rsa-data-table-body-row')[1]);
    assert.equal(findAll('.host-container-list .show-right-zone').length, 1, 'Properties panel is open on host row click');
    assert.equal(find('.host-list-properties h3.title').textContent.trim(), 'server.local', 'hostname is displayed');
    assert.equal(findAll('.host-list-properties .rsa-nav-tab').length, 2, 'tabs are rendered');
    assert.equal(find('.host-list-properties .rsa-nav-tab.is-active .label').textContent, 'Host Details', 'default tab is host details');
    assert.equal(findAll('.right-zone .host-property-panel').length, 1, 'Host property panel is displayed');

    await click(findAll('.rsa-data-table-body-row')[1]);
    assert.equal(findAll('.host-container-list .show-right-zone').length, 0, 'Properties panel is closed on same host row click');
  });

  test('click on risk properties and close using the button', async function(assert) {
    setState(endpointState);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`
      <style>
        box, section {
          min-height: 1000px
        }
      </style>
      {{host-container}}`);
    await click(findAll('.rsa-data-table-body-row')[1]);
    assert.equal(findAll('.host-container-list .show-right-zone').length, 1, 'Properties panel is open on host row click');
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'Clicked row is highlighted');
    await click(findAll('.host-list-properties .rsa-nav-tab')[1]); // click on risk details
    assert.equal(find('.host-list-properties .rsa-nav-tab.is-active .label').textContent, 'Risk Details', 'selected tab is risk details');
    assert.equal(findAll('.right-zone .risk-properties').length, 1, 'Risk properties displayed');

    await click(find('.host-list-properties .close-zone')); // click on close button
    assert.equal(findAll('.host-container-list .show-right-zone').length, 0, 'Properties panel is closed on close button click');
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 0, 'Clicked row is not highlighted on closing right panel');
  });

  test('changing server list closes right properties', async function(assert) {
    assert.expect(4);
    setState(endpointState);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`{{host-container}}`);

    await click(findAll('.rsa-data-table-body-row')[1]);
    assert.equal(findAll('.show-right-zone').length, 1, 'Properties panel is open on host row click');
    assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 1, 'Clicked row is highlighted');

    click('.rsa-content-tethered-panel-trigger');
    return settled().then(() => {
      click('.service-selector-panel li');
      return settled().then(() => {
        assert.equal(findAll('.show-right-zone').length, 0, 'right zone is closed');
        assert.equal(findAll('.rsa-data-table-body-row.is-selected').length, 0, 'Clicked row is not highlighted on closing right panel');
      });
    });
  });
});
