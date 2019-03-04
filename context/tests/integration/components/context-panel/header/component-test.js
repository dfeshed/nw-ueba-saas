import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../helpers/patch-reducer';

let setState;
module('Integration | Component | context-panel/header', function(hooks) {
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

  test('it renders', async function(assert) {

    new ReduxDataHelper(setState)
      .initializeContextPanel({ lookupKey: '1.1.1.1',
        meta: 'IP' })
      .build();

    await render(hbs`
      {{#context-panel/header as |header|}}
        {{header.title}}
        {{header.icons closePanel=closePanel}}
      {{/context-panel/header}}
    `);

    assert.ok(findAll('.rsa-icon-help-circle-lined').length === 1, 'Need to display help icons.');
    assert.ok(findAll('.rsa-icon-close-filled').length === 1, 'Need to display close icons.');
    assert.ok(find('.rsa-context-panel__header').textContent.trim().indexOf('1.1.1.1') > 0, 'Need to display only Meta key.');
  });
});
