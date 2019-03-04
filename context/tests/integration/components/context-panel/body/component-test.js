import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import alertData from '../../../../data/alert-data';
import EmberObject from '@ember/object';
import dSDetails from 'context/config/im-alerts';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../helpers/patch-reducer';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;
module('Integration | Component | context-panel/body', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('it renders', async function(assert) {

    const dataSourceData = EmberObject.create({
      class: 'alarm-sound',
      isConfigured: true,
      dataSourceType: 'Alerts',
      displayType: 'table',
      details: dSDetails,
      field: 'Alerts',
      tabRequired: true,
      title: 'context.header.alerts'
    });
    const contextData = EmberObject.create({});
    contextData.set('Alerts', alertData);
    this.set('contextData', contextData);

    new ReduxDataHelper(setState)
      .initializeContextPanel({ lookupKey: '1.1.1.1',
        meta: 'IP' })
      .setActiveTabName('Alerts')
      .setDataSources([dataSourceData])
      .setLookupData(alertData)
      .build();

    await render(hbs`{{context-panel/body contextData=contextData}}`);

    assert.equal(findAll('.rsa-data-table-header-cell').length, 6, 'Testing count of data header cells');
  });

});
