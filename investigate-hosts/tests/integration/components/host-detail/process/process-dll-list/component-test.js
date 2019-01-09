import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import wait from 'ember-test-helpers/wait';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { applyPatch, revertPatch } from '../../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let initState;
moduleForComponent('host-detail/process/process-dll-list', 'Integration | Component | endpoint host-detail/process/process-dll-list', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    initState = (state) => {
      applyPatch(state);
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});
test('it renders data table with libraries not signed by microsoft', function(assert) {
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
  this.render(hbs`{{host-detail/process/process-dll-list}}`);
  return wait().then(() => {
    assert.equal(this.$('.process-dll-list .rsa-data-table-header-cell:eq(0)').text().trim(), 'DLL Name', 'First column should be Dll name');
    assert.equal(this.$('.process-dll-list .rsa-data-table-header-cell:eq(1)').text().trim(), 'Signature', 'Second column should be Signature');
    assert.equal(this.$('.process-dll-list .rsa-data-table-header-cell:eq(2)').text().trim(), 'File Path', 'Third column should be File path');
    assert.equal(this.$('.process-dll-list .rsa-data-table-header-cell:eq(3)').text().trim(), 'Creation Time', 'Third column should be Creation Time');
  });
});
test('test when there is loaded library information', function(assert) {
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
  this.render(hbs`{{host-detail/process/process-dll-list}}`);
  return wait().then(() => {
    assert.equal(this.$('.process-dll-list .rsa-data-table-body-row').length, '1', 'Shows 1 row of loaded library');
  });
});
test('row click on libraries not signed by microsoft', function(assert) {
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
  this.render(hbs`{{host-detail/process/process-dll-list openPropertyPanel=openProperties}}`);
  return wait().then(() => {
    this.$('.process-dll-list .rsa-data-table-body-row').trigger('click');
    const selectedDll = this.get('redux').getState().endpoint.process.selectedDllItem;
    assert.equal(selectedDll.fileName, 'ld-2.17.so', 'Shows 1 row selected');
  });
});
test('row click on libraries selected row id should set', function(assert) {
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
  this.render(hbs`{{host-detail/process/process-dll-list openPropertyPanel=openProperties}}`);
  return wait().then(() => {
    this.$('.process-dll-list .rsa-data-table-body-row').trigger('click');
    const { endpoint: { process: { selectedDllRowIndex } } } = this.get('redux').getState();
    assert.equal(selectedDllRowIndex, 0, 'selected row index updated in the state');
    assert.equal(this.$('.file-name-link').length, 1, 'link added to dll name');
    assert.equal(this.$('.file-info').text().trim(), 'Showing 1 out of 1 loaded libraries', 'Shows footer message');
  });
});

test('test when there is no loaded library information', function(assert) {
  this.render(hbs`{{host-detail/process/process-dll-list}}`);
  return wait().then(() => {
    assert.equal(this.$('.process-dll-list .rsa-data-table-body').text().trim(), 'No loaded library information was found', 'Shows no results message');
  });
});