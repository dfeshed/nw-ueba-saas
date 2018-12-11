import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, find, findAll, settled, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../helpers/redux-data-helper';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import files from '../../state/files';
import { waitForSockets } from '../../../helpers/wait-for-sockets';
let initState;

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
  });

  test('Investigate files container, when files are available', async function(assert) {
    const { files: { schema: { schema } } } = files;
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
    const { files: { schema: { schema } } } = files;
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
    const { files: { schema: { schema } } } = files;
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
      .isSchemaLoading(true)
      .endpointServer(endpointServer)
      .endpointQuery(endpointQuery)
      .build();
    await render(hbs`{{investigate-files-container}}`);
    assert.equal(find('.rsa-loader').classList.contains('is-small'), true, 'certificate rsa loader displayed');
  });
  test('Certificate view hide and show', async function(assert) {
    new ReduxDataHelper(initState)
      .isSchemaLoading(true)
      .endpointServer(endpointServer)
      .endpointQuery(endpointQuery)
      .build();
    await render(hbs`{{investigate-files-container}}`);
    assert.equal(findAll('.certificates-container').length, 0, 'certificate view hidden');
    await click('.view-certificate-button');
    assert.equal(findAll('.certificates-container').length, 1, 'certificate view hidden');
  });
  test('it closes the right panel on changing the service', async function(assert) {
    const { files: { schema: { schema } } } = files;
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
    assert.equal(findAll('.content-section .content-section__section-name').length, 2, 'total number of section should be 2');
  });
});
