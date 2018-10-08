import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import dataSourceDetails from 'context/config/machines';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../helpers/patch-reducer';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { render, findAll } from '@ember/test-helpers';


const contextData = {
  lookupKey: '3841818a71e13a9a40f66acd494001d0b02178123f4ab5efbb4623757874612e',
  meta: 'FILE_HASH',
  lookupData: []
};

let setState;
module('Integration | Component | context-panel/data-source-header', function(hooks) {
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

  const marketingText = 'Endpoint (Machines) is not a currently configured data source in Context Hub. Contact your Administrator to enable this feature.' +
    ' Context Hub centralizes data sources from Endpoint, Alerts, Incidents, Lists and many more sources on-demand. For more information , click Help.';

  test('it renders', async function(assert) {
    new ReduxDataHelper(setState)
      .setData('context', contextData)
      .build();
    this.set('dataSourceDetails', dataSourceDetails);
    this.set('isConfigured', false);
    await render(hbs`{{context-panel/data-source-header dataSourceDetails=dataSourceDetails contextData=contextData isConfigured=isConfigured}}`);
    assert.equal(findAll('.rsa-context-panel__error-text')[0].innerText.trim(), marketingText, 'Both message are equal');
  });
});