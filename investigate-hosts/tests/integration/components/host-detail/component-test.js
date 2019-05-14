import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

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
  isServicesRetrieveError: true,
  isSummaryRetrieveError: true
};

const endpointQuery = {
  serverId: 'e82241fc-0681-4276-a930-dd6e5d00f152'
};

module('Integration | Component | host-detail', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('renders the host details header and details', async function(assert) {

    await render(hbs`{{host-detail}}`);

    assert.equal(findAll('.host-header').length, 1, 'header rendered');
    assert.equal(findAll('.host-detail-wrapper').length, 1, 'details rendered');

  });

  test('it shows loading indicator while fetching the data', async function(assert) {
    new ReduxDataHelper(setState)
      .isSnapshotsLoading(true)
      .isSnapshotsAvailable(true).selectedTabComponent('PROCESS').build();
    await render(hbs`{{host-detail}}`);
    assert.equal(findAll('.rsa-loader').length, 1, 'loading indicator displayed');
  });

  test('it renders the selected tab component', async function(assert) {
    new ReduxDataHelper(setState)
      .isSnapshotsLoading(false)
      .isSnapshotsAvailable(true)
      .selectedTabComponent('PROCESS').build();
    await render(hbs`{{host-detail}}`);
    assert.equal(findAll('.host-process-wrapper').length, 1, 'process information rendered');
  });

  test('it renders the no snapshot message', async function(assert) {
    new ReduxDataHelper(setState)
      .isSnapshotsLoading(false)
      .isSnapshotsAvailable(false)
      .selectedTabComponent('PROCESS')
      .build();
    await render(hbs`{{host-detail}}`);
    assert.equal(findAll('.rsa-panel-message').length, 1, 'message panel is rendered');
    assert.equal(find('.message').textContent.trim(), 'No scan history were found.', 'empty snapshot message');
  });

  test('no snapshot message for OVERVIEW and SYSTEM tab', async function(assert) {
    new ReduxDataHelper(setState)
      .isSnapshotsLoading(false)
      .isSnapshotsAvailable(false)
      .selectedTabComponent('OVERVIEW')
      .build();
    await render(hbs`{{host-detail}}`);
    assert.equal(findAll('.rsa-panel-message').length, 0, 'no message panel is rendered');
  });

  test('it renders error page when endpointserver is offline', async function(assert) {
    new ReduxDataHelper(setState)
      .endpointServer(endpointServer)
      .endpointQuery(endpointQuery)
      .build();
    await render(hbs`{{host-detail}}`);
    assert.equal(findAll('.host-header').length, 1, 'host header is not rendered');
    assert.equal(findAll('.error-page').length, 1, 'endpoint server is offline');
    assert.equal(findAll('.host-detail-wrapper').length, 0, 'host detail is rendered');
  });

  test('it renders host detail when endpointserver is online', async function(assert) {
    const endpointServerClone = { ...endpointServer };
    endpointServerClone.isSummaryRetrieveError = false;

    new ReduxDataHelper(setState)
      .isSnapshotsLoading(false)
      .isSnapshotsAvailable(false)
      .selectedTabComponent('OVERVIEW')
      .build();
    await render(hbs`{{host-detail}}`);

    assert.equal(findAll('.error-page').length, 0, 'endpoint server is online');
    assert.equal(findAll('.host-header').length, 1, 'host header is rendered');
    assert.equal(findAll('.host-detail-wrapper').length, 1, 'host detail is rendered');
  });

  test('on selecting Show/Hide right panel button, details right panel is open', async function(assert) {
    setState({ activePropertyPanelTab: 'HOST_DETAILS' });
    const endpointServerClone = { ...endpointServer };
    endpointServerClone.isSummaryRetrieveError = false;
    const host = {
      id: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      'machine': {
        'machineAgentId': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B'
      },
      'machineIdentity': {
        'agentVersion': '11.1'
      }
    };
    new ReduxDataHelper(setState)
      .isSnapshotsLoading(false)
      .isSnapshotsAvailable(false)
      .selectedTabComponent('OVERVIEW')
      .endpointServer(endpointServerClone)
      .endpointQuery(endpointQuery)
      .host(host)
      .isDetailRightPanelVisible(true)
      .isProcessDetailsView(false)
      .machineOSType('windows')
      .build();
    await render(hbs`{{host-detail}}`);
    assert.equal(findAll('.host-header').length, 1, 'header rendered');
    assert.equal(findAll('.host-detail-wrapper').length, 1, 'details rendered');
    assert.equal(findAll('.right-zone').length, 1, 'Host property panel is displayed');
    await click(find('.open-properties .rsa-form-button'));
    assert.equal(findAll('.right-zone').length, 0, 'Host property panel is closed');
  });

  test('Process details from host details', async function(assert) {
    const endpointServerClone = { ...endpointServer };
    endpointServerClone.isSummaryRetrieveError = false;

    new ReduxDataHelper(setState)
      .isSnapshotsLoading(false)
      .isSnapshotsAvailable(true)
      .selectedTabComponent('PROCESS')
      .machineIdentity({ agentMode: 'Advanced' })
      .endpointServer(endpointServerClone)
      .isProcessDetailsView(true)
      .endpointQuery(endpointQuery)
      .processList([])
      .processTree([])
      .machineOSType('windows')
      .build();
    await render(hbs`{{host-detail}}`);
    assert.equal(findAll('.host-process-details').length, 1, 'host process detail is rendered');
  });

  test('file analysis view hidden on load', async function(assert) {
    const fileAnalysis = {
      'fileData': null,
      'filePropertiesData': null,
      'isFileAnalysisView': false
    };
    new ReduxDataHelper(setState)
      .isSnapshotsLoading(false)
      .isSnapshotsAvailable(true)
      .selectedTabComponent('FILES')
      .fileAnalysis(fileAnalysis)
      .build();
    await render(hbs`{{host-detail}}`);

    assert.equal(findAll('.is-show-file-analysis').length, 0, 'File analysis is not visible');
  });

  test('file analysis view visible when isFileAnalysisView is set to true', async function(assert) {
    const fileAnalysis = {
      'fileData': [{
        text: 'OHE3',
        offset: '0x00017d66',
        unicode: false
      },
      {
        text: 'E;uht',
        offset: '0x00017a66',
        unicode: false
      }],
      'filePropertiesData': { format: 'pe' },
      'isFileAnalysisView': true
    };
    new ReduxDataHelper(setState)
      .isSnapshotsLoading(false)
      .isSnapshotsAvailable(true)
      .selectedTabComponent('FILES')
      .fileAnalysis(fileAnalysis)
      .build();

    await render(hbs`{{host-detail}}`);

    assert.equal(findAll('.is-show-file-analysis').length, 1, 'File analysis is visible');
  });

  test('it renders loader when isSnapshotsLoading is true', async function(assert) {
    const host = {
      id: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      'machine': {
        'machineAgentId': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B'
      },
      machineIdentity: {
        'agentMode': 'testMode',
        'agentVersion': '11.1'
      }
    };
    new ReduxDataHelper(setState)
      .isSnapshotsLoading(false)
      .isSnapshotsAvailable(true)
      .isProcessDetailsView(true)
      .selectedTabComponent('FILES')
      .machineOSType('windows')
      .host(host)
      .build();
    await render(hbs`{{host-detail}}`);
    assert.equal(findAll('.rsa-loader').length, 1, 'loader is visible');
  });

  test('it renders loader when isSnapshotsLoading is false', async function(assert) {
    const host = {
      id: 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B',
      'machine': {
        'machineAgentId': 'FE22A4B3-31B8-4E6B-86D3-BF02B8366C3B'
      },
      'machineIdentity': {
        'agentVersion': '11.1'
      }
    };
    new ReduxDataHelper(setState)
      .isSnapshotsLoading(false)
      .selectedTabComponent('OVERVIEW')
      .machineOSType('windows')
      .host(host)
      .isDetailRightPanelVisible(true)
      .build();
    await render(hbs`{{host-detail}}`);
    assert.equal(findAll('.host-detail-wrapper').length, 1, 'details rendered');
  });
  test('show right panel button is not present for process tab', async function(assert) {
    const state = {
      endpoint: { detailsInput: { agentId: 'agentId' } }
    };
    setState(state);
    this.set('closeProperties', () => {});
    this.set('openProperties', () => {});
    await render(hbs`{{host-detail}}`);
    assert.equal(findAll('.open-properties').length, 1, 'Right panel button is visible');
    await click(findAll('.rsa-nav-tab')[1]);
    assert.equal(findAll('.open-properties').length, 0, 'Right panel button is hidden');
  });
});
