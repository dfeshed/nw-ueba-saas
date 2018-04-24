import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import dataSourceDetails from 'context/config/archer';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { revertPatch } from '../../../../helpers/patch-reducer';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';


let setState;
module('Integration | Component | context-panel/grid', function(hooks) {
  setupRenderingTest(hooks);
  const lookupData = [{
    'Archer': {
      'dataSourceType': 'Archer',
      'dataSourceGroup': 'Archer',
      'connectionName': 'test',
      'resultList': [
        {
          'Risk Rating': 'Medium Low',
          'IP Address': '24.218.91.113',
          'Business Unit': [ 'Alberta', 'North American IT Shared Services', 'U.S. Finance' ],
          'Device ID': '218053',
          'Host Name': 'appserver01.archer-tech.com',
          'Device Name': 'APPSERVER01',
          'Criticality Rating': 'Medium-High',
          'Type': 'Web Server',
          'Device Owner': 'John',
          'Facilities': ['Corporate Headquarters', 'Kansas City Data Center'],
          'Url': 'www.google.com'
        }
      ]
    } }];

  const contextData = {
    lookupKey: '10.10.100.10',
    meta: 'IP',
    lookupData
  };

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


  test('archer grid is displayed', async function(assert) {
    new ReduxDataHelper(setState)
      .setData('context', contextData)
      .build();
    this.set('dataSourceDetails', dataSourceDetails);
    await render(hbs`{{context-panel/grid dataSourceDetails=dataSourceDetails}}`);
    assert.equal(findAll('.rsa-context-panel__config-grid').length, 1, 'configurable grid is rendered.');
    assert.equal(findAll('.rsa-context-panel__config-grid__layout').length, 10, 'total 10 fields should display for Archer');
  });

});
