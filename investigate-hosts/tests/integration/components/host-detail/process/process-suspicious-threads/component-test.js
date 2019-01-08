import { module, test, skip } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { dllListData } from '../../../../../unit/state/state';
import Immutable from 'seamless-immutable';

let initState;
module('Integration | Component | endpoint host-detail/process/process-suspicious-threads', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('Suspicious threads component rendered', async function(assert) {
    new ReduxDataHelper(initState).dllList(dllListData).selectedProcessId(1392).build();
    await render(hbs`{{host-detail/process/process-suspicious-threads}}`);

    assert.equal(findAll('.process-suspicious-threads-list').length, 1, 'Suspicious threads component rendered');
  });

  skip('All the matched suspicious threads are rendered', async function(assert) {
    new ReduxDataHelper(initState).dllList(dllListData).selectedProcessId(1392).build();
    await render(hbs`{{host-detail/process/process-suspicious-threads}}`);

    assert.equal(findAll('.process-suspicious-threads-list .rsa-data-table-body-row').length, 2, 'All the matched suspicious threads are rendered');
  });

  test('5 columns rendered in the suspicious threads table', async function(assert) {
    new ReduxDataHelper(initState).dllList(dllListData).selectedProcessId(1392).build();
    await render(hbs`{{host-detail/process/process-suspicious-threads}}`);

    assert.equal(findAll('.process-suspicious-threads-list .rsa-data-table-header-row > div').length, 5, '5 column rendered');

    assert.equal(find('.process-suspicious-threads-list .rsa-data-table-header-row > div:nth-child(1)').textContent.trim(), 'DLL Name', 'Header text in first column, DLL Name');
    assert.equal(find('.process-suspicious-threads-list .rsa-data-table-header-row > div:nth-child(2)').textContent.trim(), 'Signature', 'Header text in second column, Signature');
    assert.equal(find('.process-suspicious-threads-list .rsa-data-table-header-row > div:nth-child(3)').textContent.trim(), 'Start Address', 'Header text in third column, Start Address');
    assert.equal(find('.process-suspicious-threads-list .rsa-data-table-header-row > div:nth-child(4)').textContent.trim(), 'Thread ID', 'Header text in forth column, Thread ID');
    assert.equal(find('.process-suspicious-threads-list .rsa-data-table-header-row > div:nth-child(5)').textContent.trim(), 'Thread Environment Block', 'Header text in fifth column, Thread Environment Block');
    assert.equal(findAll('.file-name-link').length, 2, 'link added to dll name');
    assert.equal(find('.file-info').textContent.trim(), 'Showing 2 out of 2 suspicious threads', 'Shows footer message');
  });
  test('row click of suspicious threads table', async function(assert) {
    new ReduxDataHelper(initState).dllList(dllListData).selectedProcessId(1392).build();
    this.set('openProperties', function() {
      assert.ok(true);
    });
    await render(hbs`{{host-detail/process/process-suspicious-threads openPropertyPanel=openProperties}}`);
    await click(findAll('.process-suspicious-threads-list .rsa-data-table-body-row')[0]);
    const state = this.owner.lookup('service:redux').getState();
    const { endpoint: { process: { selectedDllItem } } } = state;
    const { endpoint: { process: { selectedDllRowIndex } } } = state;
    assert.equal(selectedDllItem.dllFileName, 'gmodule-2.0.dll', 'Shows 1 row selected');
    assert.equal(selectedDllRowIndex, 0, 'selected row index updated in the state');
  });
});