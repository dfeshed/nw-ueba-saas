import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, settled, click, triggerEvent } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import dummyFiles from '../../state/files';
import { waitForSockets } from '../../../helpers/wait-for-sockets';
import sinon from 'sinon';
import FileCreators from 'investigate-files/actions/data-creators';
import FileAnalysisCreators from 'investigate-shared/actions/data-creators/file-analysis-creators';

let initState, downloadFilesToServerSpy, saveLocalFileCopySpy;
const spys = [];
const wormhole = 'wormhole-context-menu';
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

const selectors = {
  downloadFilesToServer: '[test-id=downloadToServer]',
  saveLocalCopy: '[test-id=saveLocalCopy]',
  analyzeFile: '[test-id=analyzeFile]'
};
const selectedFileList = [
  {
    id: '7aa02ac1227e1c9817340ed06cb50caf3f93be476bc9d475a6b3a6f80b6743be',
    fileName: 'smss.exe',
    machineOsType: 'windows',
    checksumSha256: '7aa02ac1227e1c9817340ed06cb50caf3f93be476bc9d475a6b3a6f80b6743be',
    checksumSha1: 'd258d64dd6220ed4515996734e1c73ac8eae692d',
    checksumMd5: '03ce2ba7d96391aea2bfd935d243260f',
    signature: {
      timeStamp: '2018-09-15T01:28:25.811+0000',
      thumbprint: '99922da31f07a02edb07cd8b60a137f144d1fae7',
      features: [
        'microsoft',
        'signed',
        'valid'
      ],
      signer: 'Microsoft Windows Publisher'
    },
    size: 146872,
    format: 'pe',
    downloadInfo: {
      time: '2019-01-02T11:41:39.949+0000',
      path: '/var/netwitness/endpoint-server/files/7aa02',
      fileName: '7aa02ac1227e1c9817340ed06cb50caf3f93be476bc9d475a6b3a6f80b6743be',
      agentId: '6BE09586-1CF6-8539-D08F-CE26C3D1BA74',
      status: 'Downloaded'
    }
  },
  {
    id: '2acec9b1bf97912f00300208a46b955719a0a0c55f14c6cf325625e9f1a60ae5',
    fileName: 'Everything.exe',
    machineOsType: 'windows',
    checksumSha256: '2acec9b1bf97912f00300208a46b955719a0a0c55f14c6cf325625e9f1a60ae5',
    checksumSha1: 'e420e83962a043a8205de6fbe180ad4106e0e94c',
    checksumMd5: '2c9db41be3dbb47427b6d64114893ab4',
    signature: {
      timeStamp: '2017-06-07T01:42:32.000+0000',
      thumbprint: '5c725f30b2a7647b7469e70103fc77a5cad49772',
      features: [
        'signed',
        'valid'
      ],
      signer: 'David Carpenter'
    },
    size: 2197608,
    format: 'pe',
    downloadInfo: {
      time: '2019-01-03T05:43:29.618+0000',
      path: '/var/netwitness/endpoint-server/files/2acec',
      fileName: '2acec9b1bf97912f00300208a46b955719a0a0c55f14c6cf325625e9f1a60ae5',
      agentId: '95A3FBDE-96DC-B261-EF90-6F1E241AA83C',
      status: 'Downloaded'
    }
  }
];
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

const sampleData = [{
  firstFileName: 'XXX Test',
  entropy: 1,
  size: 1024,
  format: 'PE',
  checksum: 'checksum123',
  serviceId: 'service123',
  signature: {
    features: 'XXX unsigned',
    thumbprint: '',
    signer: ''
  }
}];

const config = [
  {
    sectionName: 'File.General',
    fields: [
      {
        field: 'firstFileName'
      },
      {
        field: 'entropy'
      },
      {
        field: 'size',
        format: 'SIZE'
      },
      {
        field: 'format'
      }
    ]
  },
  {
    sectionName: 'File.Signature',
    fields: [
      {
        field: 'signature.features',
        format: 'SIGNATURE'
      },
      {
        field: 'signature.timeStamp',
        format: 'DATE'
      },
      {
        field: 'signature.thumbprint'
      },
      {
        field: 'signature.signer'
      }
    ]
  }
];

spys.push(
  downloadFilesToServerSpy = sinon.stub(FileCreators, 'downloadFilesToServer'),
  saveLocalFileCopySpy = sinon.stub(FileAnalysisCreators, 'saveLocalFileCopy')
);

module('Integration | Component | Investigate-files-container', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-files')
  });


  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.lookup('service:timezone').set('selected', { zoneId: 'UTC' });
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    const wormholeDiv = document.createElement('div');
    wormholeDiv.id = wormhole;
    document.querySelector('#ember-testing').appendChild(wormholeDiv);
    document.addEventListener('contextmenu', callback);
  });

  hooks.afterEach(function() {
    spys.forEach((s) => {
      s.resetHistory();
      s.restore();
    });
    const wormholeElement = document.querySelector('#wormhole-context-menu');
    if (wormholeElement) {
      document.querySelector('#ember-testing').removeChild(wormholeElement);
    }
  });

  test('Context menu rendered and click download to server', async function(assert) {
    assert.expect(2);
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    const { files: { schema: { schema } } } = dummyFiles;
    const endpointQuery = {
      serverId: 'serverId'
    };

    new ReduxDataHelper(initState)
      .schema(schema)
      .fileCount(3)
      .endpointServer(endpointServer)
      .endpointQuery(endpointQuery)
      .setSelectedFileList(selectedFileList)
      .setSelectedFile({})
      .build();

    await render(hbs`
      <style>
      box, section {
        min-height: 2000px
      }
      </style>
    {{investigate-files-container accessControl=accessControl}}{{context-menu}}`);

    triggerEvent(findAll('.rsa-data-table-body-rows .rsa-form-checkbox-label')[0], 'contextmenu', e);
    return settled().then(async() => {

      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 9, 'Context menu rendered');
      const [download] = findAll(selectors.downloadFilesToServer);
      await click(download);
      assert.equal(downloadFilesToServerSpy.callCount, 1, 'Download Files to Server action is called once');
    });
  });

  test('Context menu rendered and click saveLocalFileCopy', async function(assert) {
    assert.expect(2);
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    const { files: { schema: { schema }, fileList: { files } } } = dummyFiles;
    const endpointQuery = {
      serverId: 'serverId'
    };

    new ReduxDataHelper(initState)
      .schema(schema)
      .files(files)
      .endpointServer(endpointServer)
      .endpointQuery(endpointQuery)
      .setSelectedFileList(selectedFileList)
      .setSelectedFile({})
      .build();

    await render(hbs`
      <style>
      box, section {
        min-height: 2000px
      }
      </style>
    {{investigate-files-container accessControl=accessControl}}{{context-menu}}`);

    triggerEvent(findAll('.rsa-data-table-body-rows .rsa-form-checkbox-label')[0], 'contextmenu', e);
    return settled().then(async() => {
      const selector = '.context-menu';
      const items = findAll(`${selector} > .context-menu__item`);
      assert.equal(items.length, 9, 'Context menu rendered');
      const [saveLocalCopy] = findAll(selectors.saveLocalCopy);
      await click(saveLocalCopy);
      assert.equal(saveLocalFileCopySpy.callCount, 1, 'saveLocalCopy action is called once');
    });
  });

  test('Context menu rendered and click analyzeFile', async function(assert) {
    assert.expect(1);
    const windowOpen = window.open;
    const accessControl = this.owner.lookup('service:accessControl');
    accessControl.set('endpointCanManageFiles', true);
    const { files: { schema: { schema }, fileList: { files } } } = dummyFiles;
    const endpointQuery = {
      serverId: 'serverId'
    };
    window.open = (path) => {
      // Since this calls opens in the same tab, cannot use sinon stub
      assert.equal(path.includes('6c7be07a56de5447dbcb86bc3a26985235689474017277bc4bff4ee14f137bd2'),
        true, 'window open is called');
    };
    new ReduxDataHelper(initState)
      .schema(schema)
      .files(files)
      .endpointServer(endpointServer)
      .endpointQuery(endpointQuery)
      .setSelectedFileList(selectedFileList)
      .setSelectedFile({})
      .build();

    await render(hbs`
      <style>
      box, section {
        min-height: 2000px
      }
      </style>
    {{investigate-files-container accessControl=accessControl}}{{context-menu}}`);

    triggerEvent(findAll('.rsa-data-table-body-rows .rsa-form-checkbox-label')[0], 'contextmenu', e);
    return settled().then(async() => {
      const [analyzeFile] = findAll(selectors.analyzeFile);
      await click(analyzeFile);
      window.open = windowOpen;
    });
  });

  test('Investigate files container, when files are available', async function(assert) {
    const { files: { schema: { schema } } } = dummyFiles;
    new ReduxDataHelper(initState)
      .schema(schema)
      .fileCount(3)
      .setSelectedFileList([])
      .build();

    await render(hbs`{{investigate-files-container}}`);

    assert.equal(findAll('.files-body .rsa-data-table').length, 1, 'file-list called.');
  });

  test('it renders error page when endpointserver is offline', async function(assert) {
    const endpointServerClone = { ...endpointServer };
    endpointServerClone.isSummaryRetrieveError = true;

    new ReduxDataHelper(initState)
      .endpointServer(endpointServerClone)
      .endpointQuery(endpointQuery)
      .isCertificateView(false)
      .setSelectedFileList([])
      .build();
    await render(hbs`{{investigate-files-container}}`);
    assert.equal(findAll('.files-body').length, 0, 'file list is not rendered');
    assert.equal(findAll('.error-page').length, 1, 'endpoint server is offline');
  });

  test('it renders file list when endpointserver is online', async function(assert) {
    const { files: { schema: { schema } } } = dummyFiles;
    new ReduxDataHelper(initState)
      .schema(schema)
      .fileCount(3)
      .setSelectedFileList([])
      .endpointServer(endpointServer)
      .endpointQuery(endpointQuery)
      .build();
    await render(hbs`{{investigate-files-container}}`);
    assert.equal(findAll('.error-page').length, 0, 'endpoint server is online');
    assert.equal(findAll('.files-body').length, 1, 'file list is rendered');
  });

  test('when hosts tab is active, file-found-on-hosts renders', async function(assert) {
    const { files: { schema: { schema } } } = dummyFiles;
    const done = waitForSockets();
    new ReduxDataHelper(initState)
      .schema(schema)
      .fileCount(3)
      .setSelectedFileList([])
      .endpointServer(endpointServer)
      .endpointQuery(endpointQuery)
      .hostNameList([{ value: 'Machine1' }])
      .activeDataSourceTab('HOSTS')
      .build();
    await render(hbs`{{investigate-files-container}}`);
    await click(findAll('.files-body .rsa-data-table-body-row')[0]);
    return settled().then(() => {
      assert.equal(findAll('.files-host-list').length, 1, 'Machine list is rendered');
      assert.equal(findAll('.investigate-file-tab .title').length, 1, 'title is rendered in right panel');
      done();
    });

  });

  test('it shows the loading indicator when schema is loading', async function(assert) {
    new ReduxDataHelper(initState)
      .isSchemaLoading(true)
      .endpointServer(endpointServer)
      .endpointQuery(endpointQuery)
      .build();
    await render(hbs`{{investigate-files-container}}`);
    assert.equal(find('.is-larger').classList.contains('rsa-loader'), true, 'Rsa loader displayed');
  });
  test('it shows the loading indicator certificate loading', async function(assert) {
    new ReduxDataHelper(initState)
      .certificatesLoadingStatus('wait')
      .isSchemaLoading(false)
      .endpointServer(endpointServer)
      .endpointQuery(endpointQuery)
      .build();
    await render(hbs`{{investigate-files-container}}`);
    assert.equal(findAll('.rsa-loader')[1].classList.contains('is-larger'), true, 'certificate rsa loader displayed');
  });
  test('it shows the loading indicator certificate stops', async function(assert) {
    endpointServer.isSummaryRetrieveError = false;
    new ReduxDataHelper(initState)
      .certificatesLoadingStatus('wait')
      .isSchemaLoading(false)
      .endpointServer(endpointServer)
      .endpointQuery(endpointQuery)
      .build();
    await render(hbs`{{investigate-files-container}}`);
    assert.equal(findAll('.rsa-loader').length, 2, 'certificate loader should not show');
  });
  test('Certificate view hide and show', async function(assert) {
    new ReduxDataHelper(initState)
      .isSchemaLoading(true)
      .endpointServer(endpointServer)
      .endpointQuery(endpointQuery)
      .build();
    await render(hbs`{{investigate-files-container}}`);
    await click('.view-certificate-button');
    const state = this.owner.lookup('service:redux').getState();
    assert.equal(state.certificate.list.isCertificateView, true, 'Certificate view is closed');
  });
  test('Certificate view button disabled on selection more than 10 files', async function(assert) {
    const selectedFileList = new Array(11)
      .join().split(',')
      .map(function(item, index) {
        return { id: ++index, checksumSha256: index };
      });
    new ReduxDataHelper(initState)
      .isSchemaLoading(true)
      .endpointServer(endpointServer)
      .endpointQuery(endpointQuery)
      .setSelectedFileList(selectedFileList)
      .build();
    await render(hbs`{{investigate-files-container}}`);
    assert.equal(find('.view-certificate-button').classList.contains('is-disabled'), true, 'View certificate button disabled');
    assert.equal(find('.view-certificate-button').title, 'Select a maximum of 10 files to view.', 'tooltip added to disabled button');
  });
  test('it closes the right panel on changing the service', async function(assert) {
    const { files: { schema: { schema } } } = dummyFiles;
    const services = {
      serviceData: [{ id: '1', displayName: 'TEST', name: 'TEST', version: '11.1.0.0' }],
      summaryData: { startTime: 0 },
      isServicesRetrieveError: false
    };
    new ReduxDataHelper(initState)
      .schema(schema)
      .fileCount(3)
      .services(services)
      .setSelectedFileList([])
      .isEndpointServerOffline(false)
      .build();
    await render(hbs`{{investigate-files-container}}`);
    await click(findAll('.files-body .rsa-data-table-body-row')[0]);
    click('.rsa-content-tethered-panel-trigger');
    return settled().then(() => {
      assert.equal(findAll('.investigate-file-tab .title').length, 1, 'title is rendered in right panel');
      assert.equal(findAll('.show-right-zone .right-zone').length, 1, 'right zone is open');
      click('.service-selector-panel li');
      return settled().then(() => {
        assert.equal(findAll('.show-right-zone .right-zone').length, 0, 'right zone is closed');
      });
    });
  });
  test('File details are shown in right panel', async function(assert) {
    new ReduxDataHelper(initState)
      .setSelectedFileList([])
      .activeDataSourceTab('FILE_DETAILS')
      .selectedDetailFile(sampleData)
      .build();
    this.set('propertyConfig', config);
    await render(hbs`{{investigate-files-container propertyConfig=propertyConfig}}`);
    assert.equal(findAll('.investigate-file-tab').length, 1, 'Right panel header is rendered');
    assert.equal(findAll('.host-property-panel').length, 1, 'Propertoes panel is rendered');
    assert.equal(findAll('.content-section .content-section__section-name').length, 1, 'total number of section should be 1');
  });

  test('filter controls are displayed', async function(assert) {
    this.set('propertyConfig', config);
    await render(hbs`{{investigate-files-container propertyConfig=propertyConfig}}`);
    assert.equal(findAll('.files-content .rsa-data-filters .filter-controls').length, 13, 'all filter controls are rendered');
  });
});
