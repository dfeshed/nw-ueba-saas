import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import dataSourceDetails from 'context/config/fileReputationServer';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../helpers/patch-reducer';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { render, findAll } from '@ember/test-helpers';
import lookupData from '../../../../data/fileReputationServer';

const contextData = {
  lookupKey: '3841818a71e13a9a40f66acd494001d0b02178123f4ab5efbb4623757874612e',
  meta: 'FILE_HASH',
  lookupData
};

let setState;
module('Integration | Component | context-panel/dynamic-grid', function(hooks) {
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

  test('dynamic grid is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .setData('context', contextData)
      .build();
    this.set('dataSourceDetails', dataSourceDetails);
    await render(hbs`{{context-panel/dynamic-grid dataSourceDetails=dataSourceDetails contextData=contextData}}`);
    assert.equal(findAll('.rsa-context-panel__grid').length, 1, 'dynamic grid is rendered.');
    assert.equal(findAll('.rsa-context-panel__grid .rsa-context-panel__grid__reputation-server').length, 1, 'File Reputation Server grid is present');
    assert.equal(findAll('.rsa-context-panel__grid .col-xs-3').length, 5, '5 fields are available for File Reputation Server');
    assert.equal(findAll('.rsa-context-panel__grid .col-xs-3')[0].innerText.trim(), 'REPUTATION STATUS\nMalicious', 'Malicious status is displayed');
    assert.equal(findAll('.rsa-context-panel__grid .col-xs-3')[1].innerText.trim(), 'SCANNER MATCH\n0', 'Scanner match is 0');
    assert.equal(findAll('.rsa-context-panel__grid .col-xs-3')[2].innerText.trim(), 'CLASSIFICATION PLATFORM\n-', 'platform is - as it is null');
  });
});
