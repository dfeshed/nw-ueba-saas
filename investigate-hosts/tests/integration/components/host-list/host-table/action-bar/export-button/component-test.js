import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import engineResolver from '../../../../../../helpers/engine-resolver';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';

import Immutable from 'seamless-immutable';

let setState;
module('Integration | Component | host-list/host-table/action-bar/export-button', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });
  test('it renders host table action bar export button', async function(assert) {
    await render(hbs`{{host-list/host-table/action-bar/export-button}}`);
    assert.equal(findAll('.rsa-form-button-wrapper').length, 1, 'export button is rendered');
  });

  test('it renders host table action bar export button when exportStatus is streaming', async function(assert) {
    new ReduxDataHelper(setState)
      .hostExportStatus('streaming')
      .build();
    await render(hbs`{{host-list/host-table/action-bar/export-button}}`);
    assert.equal(findAll('.rsa-form-button-wrapper button .rsa-loader').length, 1, 'loader is rendered');
    assert.equal(find('.rsa-form-button-wrapper button').textContent.trim(), 'Downloading', 'downloading export button is rendered');
  });

  test('it renders host table action bar export button when exportStatus is completed', async function(assert) {
    new ReduxDataHelper(setState)
      .hostExportStatus('completed')
      .build();
    await render(hbs`{{host-list/host-table/action-bar/export-button}}`);
    assert.equal(findAll('.rsa-form-button-wrapper button .rsa-loader').length, 0, 'loader is not present');
    assert.equal(find('.rsa-form-button-wrapper button').textContent.trim(), 'Export to CSV', 'default export button is rendered');
  });
});