import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../helpers/patch-reducer';
import listData from '../../../../data/list';
import EmberObject from '@ember/object';
import dSDetails from 'context/config/im-alerts';

let setState;
module('Integration | Component | context-panel/footer', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('it renders', async function(assert) {
    const dataSourceData = EmberObject.create({
      class: 'alarm-sound',
      isConfigured: true,
      dataSourceType: 'LIST',
      displayType: 'table',
      details: dSDetails,
      field: 'LIST',
      tabRequired: true,
      title: 'context.header.lIST'
    });

    new ReduxDataHelper(setState)
      .initializeContextPanel({ lookupKey: '1.1.1.1',
        meta: 'IP' })
      .setActiveTabName('lIST')
      .setDataSources([dataSourceData])
      .setLookupData([listData])
      .build();

    await render(hbs`{{context-panel/footer}}`);
    assert.equal(find('.rsa-context-panel__footer').textContent.trim(), '0 List(s)', 'Showing total count for list data.');
  });
});
