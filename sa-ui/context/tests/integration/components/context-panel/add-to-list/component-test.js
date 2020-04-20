import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';


let setState;

module('Integration | Component | context-panel/add-to-list', function(hooks) {

  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
  });

  hooks.afterEach(function() {
    revertPatch();
  });
  const entity = {
    type: 'IP',
    id: '10.10.10.10'
  };

  test('renders list-view screen', async function(assert) {
    new ReduxDataHelper(setState)
      .initializeContextPanel({ lookupKey: '1.1.1.1',
        meta: 'IP' })
      .setListView(true)
      .setEntityType(entity)
      .build();

    await render(hbs`
      <div id='modalDestination'></div>
      {{context-panel/add-to-list}}
    `);
    assert.ok(findAll('.rsa-context-tree-table__addtoListBox').length);
  });

  test('renders create-list screen', async function(assert) {
    new ReduxDataHelper(setState)
      .initializeContextPanel({ lookupKey: '1.1.1.1',
        meta: 'IP' })
      .setListView(false)
      .setEntityType(entity)
      .build();

    await render(hbs`
      <div id='modalDestination'></div>
      {{context-panel/add-to-list}}
    `);
    assert.ok(findAll('.rsa-context-tree-table__createList').length);
  });
});
