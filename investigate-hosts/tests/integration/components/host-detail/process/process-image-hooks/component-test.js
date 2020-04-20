import { module, test } from 'qunit';
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
module('Integration | Component | endpoint host-detail/process/process-image-hooks', function(hooks) {
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

  test('Image hooks component rendered', async function(assert) {
    new ReduxDataHelper(initState).dllList(dllListData).selectedProcessId(1392).build();
    await render(hbs`{{host-detail/process/process-image-hooks}}`);

    assert.equal(findAll('.process-image-hooks-list').length, 1, 'Image hooks component rendered');
  });

  test('All the matched image hooks are rendered', async function(assert) {
    new ReduxDataHelper(initState).dllList(dllListData).selectedProcessId(1392).build();
    await render(hbs`{{host-detail/process/process-image-hooks}}`);

    assert.equal(findAll('.process-image-hooks-list .rsa-data-table-body-row').length, 5, 'All the matched image hooks are rendered');
  });

  test('5 columns rendered in the Image hooks table', async function(assert) {
    new ReduxDataHelper(initState).dllList(dllListData).selectedProcessId(1392).build();
    await render(hbs`{{host-detail/process/process-image-hooks}}`);
    assert.equal(findAll('.process-image-hooks-list .rsa-data-table-header-row > div').length, 5, '5 column rendered');
    assert.equal(find('.process-image-hooks-list .rsa-data-table-header-row > div:nth-child(1)').textContent.trim(), 'DLL Name', 'Header text in first column, DLL Name');
    assert.equal(find('.process-image-hooks-list .rsa-data-table-header-row > div:nth-child(2)').textContent.trim(), 'Hooked FileName', 'Header text in second column, Hooked FileName');
    assert.equal(find('.process-image-hooks-list .rsa-data-table-header-row > div:nth-child(3)').textContent.trim(), 'Symbol', 'Header text in third column, Symbol');
    assert.equal(find('.process-image-hooks-list .rsa-data-table-header-row > div:nth-child(4)').textContent.trim(), 'Type', 'Header text in fourth column, Type');
    assert.equal(find('.process-image-hooks-list .rsa-data-table-header-row > div:nth-child(5)').textContent.trim(), 'Signature', 'Header text in fifth column, Signature');
    assert.equal(findAll('.file-name-link').length, 5, 'link added to dll name');
    assert.equal(find('.file-info').textContent.trim(), 'Showing 5 out of 5 image hooks', 'Shows footer message');
  });
  test('row click of Image hooks table', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(initState).dllList(dllListData).selectedProcessId(1392).build();
    this.set('openProperties', function() {
      assert.ok(true);
    });
    await render(hbs`{{host-detail/process/process-image-hooks openPropertyPanel=openProperties}}`);
    await click(findAll('.process-image-hooks-list .rsa-data-table-body-row')[0]);
    const state = this.owner.lookup('service:redux').getState();
    const { endpoint: { process: { selectedDllItem } } } = state;
    const { endpoint: { process: { selectedDllRowIndex } } } = state;
    assert.equal(selectedDllItem.hookFileName, 'gobject-2.0.dll', 'Shows 1 row selected');
    assert.equal(selectedDllRowIndex, 0, 'selected row index updated in the state');
  });
});