import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, render } from '@ember/test-helpers';
import alertData from '../../../../../data/alert-data';
import dSDetails from 'context/config/im-alerts';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;
module('Integration | Component | context-panel/header/actionbar', function(hooks) {
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
    const dataSourceData = {
      class: 'alarm-sound',
      isConfigured: true,
      dataSourceType: 'Alerts',
      displayType: 'table',
      details: dSDetails,
      field: 'Alerts',
      tabRequired: true,
      title: 'context.header.alerts'
    };

    const headerButtons = [
      'add-to-list',
      'investigate',
      'archer',
      'endpoint'
    ];

    new ReduxDataHelper(setState)
      .initializeContextPanel({ lookupKey: '1.1.1.1',
        meta: 'IP' })
      .setActiveTabName('Alerts')
      .setDataSources([dataSourceData])
      .setLookupData([alertData])
      .setHeaderButtons(headerButtons)
      .build();

    await render(hbs`{{context-panel/header/actionbar}}`);

    assert.ok(find('.rsa-context-panel__header').textContent.indexOf('Add/Remove from List') > 0, 'Add/Remove from list button is available');
    assert.ok(find('.rsa-context-panel__header').textContent.indexOf('Pivot to Archer') > 0, 'Pivot to Archer button is available');
    assert.ok(find('.rsa-context-panel__header').textContent.indexOf('Pivot to Endpoint Thick Client') > 0, 'Pivot to Endpoint thick client button is available');
    assert.ok(find('.rsa-context-panel__header').textContent.indexOf('Pivot to Investigate > Navigate') > 0, 'Pivot to Investigate button is available');

  });


});
