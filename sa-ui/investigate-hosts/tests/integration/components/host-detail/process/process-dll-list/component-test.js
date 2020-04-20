import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled, findAll, find, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let initState;

module('Integration | Component | endpoint host-detail/process/process-dll-list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    this.owner.inject('component', 'i18n', 'service:i18n');
    initState = (state) => {
      applyPatch(state);
      this.redux = this.owner.lookup('service:redux');
    };
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('it renders data table with libraries not signed by microsoft', async function(assert) {
    const host = {
      machine: {
        machineAgentId: 'A8F19AA5-A48D-D17E-2930-DF5F1A75A711'
      },
      machineIdentity: {
        machineName: 'INENDHUPAAL1C',
        machineOsType: 'windows'
      }
    };
    new ReduxDataHelper(initState)
      .host(host)
      .build();
    await render(hbs`{{host-detail/process/process-dll-list}}`);
    return settled().then(() => {
      assert.equal(find('.process-dll-list .rsa-data-table-header-cell').textContent.trim(), 'DLL Name', 'First column should be Dll name');
      assert.equal(find(findAll('.process-dll-list .rsa-data-table-header-cell')[1]).textContent.trim(), 'Signature', 'Second column should be Signature');
      assert.equal(find(findAll('.process-dll-list .rsa-data-table-header-cell')[2]).textContent.trim(), 'File Path', 'Third column should be File path');
      assert.equal(find(findAll('.process-dll-list .rsa-data-table-header-cell')[3]).textContent.trim(), 'Creation Time', 'Third column should be Creation Time');
    });
  });
  test('test when there is loaded library information', async function(assert) {
    const dllData = [{
      fileName: 'ld-2.17.so',
      path: '/usr/lib64',
      fileProperties: {
        checksumMd5: 'f8b84b5d75c85a9124704f2a3dee8d06',
        checksumSha1: '33a48cbc6a0a00a64d29e5c211443aebc94e9595',
        checksumSha256: '2446d290781c8d2182c83560a16b5c956a0f6ccc3f0d840f244eb9a01ac54d30',
        firstFileName: 'ld-2.17.so',
        firstSeenTime: 1515427075000,
        format: 'elf',
        id: '2446d290781c8d2182c83560a16b5c956a0f6ccc3f0d840f244eb9a01ac54d30',
        machineOsType: 'linux',
        size: 159640
      }
    }];
    new ReduxDataHelper(initState)
      .dllList(dllData)
      .build();
    await render(hbs`{{host-detail/process/process-dll-list}}`);
    return settled().then(() => {
      assert.equal(findAll('.process-dll-list .rsa-data-table-body-row').length, '1', 'Shows 1 row of loaded library');
    });
  });
  test('row click on libraries not signed by microsoft', async function(assert) {
    assert.expect(2);
    const dllData = [{
      fileName: 'ld-2.17.so',
      path: '/usr/lib64',
      fileProperties: {
        checksumMd5: 'f8b84b5d75c85a9124704f2a3dee8d06',
        checksumSha1: '33a48cbc6a0a00a64d29e5c211443aebc94e9595',
        checksumSha256: '2446d290781c8d2182c83560a16b5c956a0f6ccc3f0d840f244eb9a01ac54d30',
        firstFileName: 'ld-2.17.so',
        firstSeenTime: 1515427075000,
        format: 'elf',
        id: '2446d290781c8d2182c83560a16b5c956a0f6ccc3f0d840f244eb9a01ac54d30',
        machineOsType: 'linux',
        size: 159640
      }
    }];
    new ReduxDataHelper(initState)
      .dllList(dllData)
      .build();
    this.set('openProperties', function() {
      assert.ok(true);
    });
    await render(hbs`{{host-detail/process/process-dll-list openPropertyPanel=openProperties}}`);
    return settled().then(async() => {
      await click('.process-dll-list .rsa-data-table-body-row');
      const selectedDll = this.get('redux').getState().endpoint.process.selectedDllItem;
      assert.equal(selectedDll.fileName, 'ld-2.17.so', 'Shows 1 row selected');
    });
  });
  test('row click on libraries selected row id should set', async function(assert) {
    assert.expect(4);
    const dllData = [{
      fileName: 'ld-2.17.so',
      path: '/usr/lib64',
      fileProperties: {
        checksumMd5: 'f8b84b5d75c85a9124704f2a3dee8d06',
        checksumSha1: '33a48cbc6a0a00a64d29e5c211443aebc94e9595',
        checksumSha256: '2446d290781c8d2182c83560a16b5c956a0f6ccc3f0d840f244eb9a01ac54d30',
        firstFileName: 'ld-2.17.so',
        firstSeenTime: 1515427075000,
        format: 'elf',
        id: '2446d290781c8d2182c83560a16b5c956a0f6ccc3f0d840f244eb9a01ac54d30',
        machineOsType: 'linux',
        size: 159640
      }
    }];
    new ReduxDataHelper(initState)
      .dllList(dllData)
      .build();
    this.set('openProperties', function() {
      assert.ok(true);
    });
    await render(hbs`{{host-detail/process/process-dll-list openPropertyPanel=openProperties}}`);
    return settled().then(async() => {
      await click('.process-dll-list .rsa-data-table-body-row');
      const { endpoint: { process: { selectedDllRowIndex } } } = this.get('redux').getState();
      assert.equal(selectedDllRowIndex, 0, 'selected row index updated in the state');
      assert.equal(findAll('.file-name-link').length, 1, 'link added to dll name');
      assert.equal(find('.file-info').textContent.trim(), 'Showing 1 out of 1 loaded libraries', 'Shows footer message');
    });
  });

  test('test when there is no loaded library information', async function(assert) {
    await render(hbs`{{host-detail/process/process-dll-list}}`);
    return settled().then(() => {
      assert.equal(find('.process-dll-list .rsa-data-table-body').textContent.trim(), 'No loaded library information was found', 'Shows no results message');
    });
  });
});