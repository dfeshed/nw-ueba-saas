import { module, test, setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { findAll, find, render, click, waitUntil } from '@ember/test-helpers';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import EmberObject from '@ember/object';
import Service from '@ember/service';
import { hostDownloads } from '../../../../state/downloads';
import { patchReducer } from '../../../../../../helpers/vnext-patch';

import Immutable from 'seamless-immutable';
import endpoint from '../../../../state/schema';
import hostListState from '../../../../state/host.machines';

const transitions = [];
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
const config = [{
  tableId: 'hosts',
  columns: [
    {
      field: 'id'
    },
    {
      field: 'machineIdentity.agentVersion'
    },
    {
      field: 'machine.scanStartTime'
    },
    {
      field: 'machineIdentity.machineOsType'
    }
  ]
}];

const endpointQuery = {
  serverId: 'e82241fc-0681-4276-a930-dd6e5d00f152'
};
const endpointState =
  {
    endpoint:
      {
        schema: { schema: endpoint.schema },
        machines: {
          hostList: hostListState.machines.hostList, selectedHostList: [ { version: '11.3', managed: true, id: 'C1C6F9C1-74D1-43C9-CBD4-289392F6442F', scanStatus: 'idle' }],
          hostColumnSort: 'machineIdentity.machineName',
          focusedHost: null
        },
        hostDownloads,
        detailsInput: {
          agentId: 'agent-id'
        }
      },
    preferences: {
      preferences: {
        machinePreference: {
          columnConfig: config
        }
      }
    },
    endpointServer,
    endpointQuery
  };

let initState;
module('Integration | Component | host-detail/downloads/downloads-list/body-cell', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    this.owner.register('service:-routing', Service.extend({
      currentRouteName: 'hosts.details',
      generateURL: () => {
        return;
      },
      transitionTo: (name, args, queryParams) => {
        transitions.push({ name, queryParams });
      }
    }));
  });

  test('it renders the checkbox column', async function(assert) {
    this.set('column', { componentClass: 'rsa-form-checkbox' });
    this.set('selections', [{ id: '5cda8882c8811e511649e335' }]);
    this.set('item', { id: '5cda8882c8811e511649e335', fileType: 'Mft', agentId: 'A0351965-30D0-2201-F29B-FDD7FD32EB21', serviceId: 'e9be528a-ca5b-463b-bc3f-deab7cc36bb0' });
    this.set('checkBoxAction', (id) => {
      assert.equal(id, 1);
    });
    await render(hbs`{{host-detail/downloads/downloads-list/body-cell selections=selections column=column item=item checkBoxAction=(action checkBoxAction 1)}}`);

    assert.equal(findAll('.rsa-form-checkbox').length, 1);
    assert.equal(findAll('.rsa-form-checkbox:checked').length, 1, 'Expecting to select the checkbox');
    await click('.rsa-form-checkbox');
    assert.equal(findAll('.rsa-form-checkbox:checked').length, 0, 'Expecting to un-select the checkbox');
  });

  test('it should render downloaded file size', async function(assert) {
    this.set('column', { field: 'size' });
    this.set('item', { id: '5cda8882c8811e511649e335', size: 1080 });

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item}}`);
    assert.equal(find('.size').textContent.includes('1.1'), true, 'File size rendered in kb');
  });

  test('it should render filename', async function(assert) {
    this.set('column', { field: 'fileName' });
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile', fileType: 'Mft' });

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item}}`);
    assert.equal(findAll('.downloaded-file-name').length, 1, 'Renders file name');
    assert.equal(find('.fileName').textContent.trim(), 'testFile', 'Renders file name');
  });
  test('filename mft link test', async function(assert) {
    initState(endpointState);
    this.set('column', { field: 'fileName' });
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile', fileType: 'Mft' });
    this.set('serverId', 'abcd');

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item serverId=serverId}}`);
    const links = findAll('.downloaded-file-name a');
    assert.equal(links.length, 1, 'downloaded-file-name mft is linked');
    await click('.downloaded-file-name a');
    assert.deepEqual(transitions, [{
      name: 'hosts.details.tab.mft',
      queryParams: {}
    }]);
  });

  test('file file link test', async function(assert) {
    initState(endpointState);
    this.set('column', { field: 'fileName' });
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile', fileType: 'File' });
    this.set('serverId', 'abcd');

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item serverId=serverId}}`);
    const links = findAll('.downloaded-file-name a');
    assert.equal(links.length, 1, 'downloaded-file-name file is linked');
    await click('.downloaded-file-name a');
    const selector = '[a][href~="/investigate/files/file?checksum=&sid=abcd"]';
    await waitUntil(() => document.querySelectorAll(selector).length === 0, { timeout: 8000 });
  });
  test('file should not link if not file or mft test', async function(assert) {
    this.set('column', { field: 'fileName' });
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile', fileType: 'Different' });
    this.set('serverId', 'abcd');

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item serverId=serverId}}`);
    const links = findAll('.downloaded-file-name a');
    assert.equal(links.length, 0, 'downloaded-file-name file is not linked');
  });
  test('file should not link if it has Error status', async function(assert) {
    this.set('column', { field: 'fileName' });
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile', fileType: 'Mft', status: 'Error' });
    this.set('serverId', 'abcd');

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item serverId=serverId}}`);
    const links = findAll('.downloaded-file-name a');
    assert.equal(links.length, 0, 'downloaded-file-name file is not linked');
  });
  test('file should not link if it has Processing status', async function(assert) {
    this.set('column', { field: 'fileName' });
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile', fileType: 'Mft', status: 'Processing' });
    this.set('serverId', 'abcd');

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item serverId=serverId}}`);
    const links = findAll('.downloaded-file-name a');
    assert.equal(links.length, 0, 'downloaded-file-name file is not linked');
  });

  test('it should render the downloaded time', async function(assert) {
    const column = EmberObject.create({ field: 'downloadedTime' });
    this.set('column', column);
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile', downloadedTime: '2019-05-14T09:21:06.923+0000' });

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item}}`);
    assert.equal(findAll('.downloadedTime').length, 1, 'Expected to render downloaded time');
  });

  test('it should render the downloaded status', async function(assert) {
    this.set('column', { field: 'status' });
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile', status: 'Downloaded' });

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item}}`);
    assert.equal(findAll('.status').length, 1, 'Expected to render downloaded time');
  });

  test('it should render the checksum if present', async function(assert) {
    this.set('column', { field: 'checksumSha256' });
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile', checksumSha256: 'c34f34t3' });

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item}}`);
    assert.equal(find('.checksumSha256').textContent.trim(), 'c34f34t3', 'Expected to render checksum');
  });

  test('it should render NA if checksum is not present', async function(assert) {
    this.set('column', { field: 'checksumSha256' });
    this.set('item', { id: '5cda8882c8811e511649e335', fileName: 'testFile' });

    await render(hbs`{{host-detail/downloads/downloads-list/body-cell column=column item=item}}`);
    assert.equal(find('.checksumSha256').textContent.trim(), 'NA', 'render NA if checksum is not present');
  });
});
