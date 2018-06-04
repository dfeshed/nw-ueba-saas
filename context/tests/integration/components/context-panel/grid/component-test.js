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
          'Device Owner': '',
          'Business Unit': '',
          'Host Name': 'NewHost',
          'MAC Address': '',
          'internal_pivot_archer_request_url': 'HTTPS://10.31.204.245/RSAArcher/default.aspx?requestUrl=..%2fGenericContent%2fRecord.aspx%3fid%3d324945%26moduleId%3d71',
          'Facilities': '',
          'Risk Rating': '',
          'IP Address': '10.30.91.91',
          'Type': 'Desktop',
          'Device ID': '324945',
          'Device Name': 'New Device',
          'Criticality Rating': 'Not Rated',
          'Business Processes': [ 'Process 1', 'Process 2', 'Process 3', 'Process 4' ]
        }
      ],
      'order': [ 'Criticality Rating', 'Risk Rating', 'Device Name', 'Host Name', 'IP Address', 'Device ID', 'Type', 'MAC Address', 'Facilities', 'Business Unit', 'Device Owner', 'internal_pivot_archer_request_url' ]
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
    assert.equal(findAll('.rsa-context-panel__config-grid__layout').length, 11, 'total 11 fields should display for Archer');
  });


  test('no fields should display when result is empty', async function(assert) {
    const lookupData = [{
      'Archer': {
        'dataSourceType': 'Archer',
        'dataSourceGroup': 'Archer',
        'connectionName': 'test'
      } }];

    const emptyData = {
      lookupKey: '10.10.100.10',
      meta: 'IP',
      lookupData
    };

    new ReduxDataHelper(setState)
      .setData('context', emptyData)
      .build();
    this.set('dataSourceDetails', dataSourceDetails);
    await render(hbs`{{context-panel/grid dataSourceDetails=dataSourceDetails}}`);
    assert.equal(findAll('.rsa-context-panel__config-grid').length, 1, 'configurable grid is rendered.');
    assert.equal(findAll('.rsa-context-panel__config-grid__layout').length, 0, 'no fields are available as resultList is empty');
  });

  test('number of fields displayed and resultList is same', async function(assert) {
    const archerDetails = {
      class: 'rsa-context-panel__grid__archer-details',
      dataSourceGroup: 'Archer',
      headerRequired: false,
      footerRequired: true,
      header: '',
      footer: '',
      title: 'context.archer.title'
    };

    new ReduxDataHelper(setState)
      .setData('context', contextData)
      .build();
    this.set('dataSourceDetails', archerDetails);
    await render(hbs `{{context-panel/grid dataSourceDetails=dataSourceDetails}}`);
    assert.equal(findAll('.rsa-context-panel__config-grid').length, 1, 'configurable grid is rendered.');
    assert.equal(findAll('.rsa-context-panel__config-grid__layout').length, 12, 'same number fields are displayed');
    assert.ok(findAll('.rsa-context-panel__config-grid__layout')[0].textContent.indexOf('Criticality Rating'), 'Criticality Rating is the first field to display');
    assert.ok(findAll('.rsa-context-panel__config-grid__layout')[10].textContent.indexOf('Device Owner'), 'Device Owner is the last field to display');

  });


  test('no fields should display when result is empty', async function(assert) {
    const lookupData = [{
      'Archer': {
        'dataSourceType': 'Archer',
        'dataSourceGroup': 'Archer',
        'connectionName': 'test'
      } }];

    const emptyData = {
      lookupKey: '10.10.100.10',
      meta: 'IP',
      lookupData
    };

    new ReduxDataHelper(setState)
      .setData('context', emptyData)
      .build();
    this.set('dataSourceDetails', dataSourceDetails);
    await render(hbs`{{context-panel/grid dataSourceDetails=dataSourceDetails}}`);
    assert.equal(findAll('.rsa-context-panel__config-grid').length, 1, 'configurable grid is rendered.');
    assert.equal(findAll('.rsa-context-panel__config-grid__layout').length, 0, 'no fields are available as resultList is empty');
  });

  test('no attributes are displayed when order details is not available in response', async function(assert) {
    const lookupData = [{
      'Archer': {
        'dataSourceType': 'Archer',
        'dataSourceGroup': 'Archer',
        'connectionName': 'test',
        'resultList': [
          {
            'Device Owner': '',
            'Business Unit': '',
            'Host Name': 'NewHost',
            'MAC Address': '',
            'Url': 'HTTPS://10.31.204.245/RSAArcher/default.aspx?requestUrl=..%2fGenericContent%2fRecord.aspx%3fid%3d324945%26moduleId%3d71',
            'Facilities': '',
            'Risk Rating': '',
            'IP Address': '10.30.91.91',
            'Type': 'Desktop',
            'Device ID': '324945',
            'Device Name': 'New Device',
            'Criticality Rating': 'Not Rated'
          }
        ]
      } }];

    const contextData = {
      lookupKey: '10.10.100.10',
      meta: 'IP',
      lookupData
    };
    new ReduxDataHelper(setState)
      .setData('context', contextData)
      .build();
    this.set('dataSourceDetails', dataSourceDetails);
    await render(hbs`{{context-panel/grid dataSourceDetails=dataSourceDetails}}`);
    assert.equal(findAll('.rsa-context-panel__config-grid__layout').length, 0, 'configurable grid is empty.');
  });

  test('show non-translated label name when attribute is not ootb', async function(assert) {
    const lookupData = [{
      'Archer': {
        'dataSourceType': 'Archer',
        'dataSourceGroup': 'Archer',
        'connectionName': 'test',
        'resultList': [
          {
            'Device Owner': '',
            'Business Unit': '',
            'Host Name': 'NewHost',
            'MAC Address': '',
            'Url': 'HTTPS://10.31.204.245/RSAArcher/default.aspx?requestUrl=..%2fGenericContent%2fRecord.aspx%3fid%3d324945%26moduleId%3d71',
            'Facilities': '',
            'Risk Rating': '',
            'IP Address': '10.30.91.91',
            'Type': 'Desktop',
            'Device ID': '324945',
            'Device Name': 'New Device',
            'Criticality Rating': 'Not Rated',
            'Manufacturer': 'Archer'
          }
        ],
        'order': [ 'Criticality Rating', 'Risk Rating', 'Manufacturer', 'Device Name', 'Host Name', 'IP Address', 'Device ID', 'Type', 'MAC Address', 'Facilities', 'Business Unit', 'Device Owner', 'Url' ]
      } }];

    const contextData = {
      lookupKey: '10.10.100.10',
      meta: 'IP',
      lookupData
    };
    new ReduxDataHelper(setState)
      .setData('context', contextData)
      .build();
    this.set('dataSourceDetails', dataSourceDetails);
    await render(hbs `{{context-panel/grid dataSourceDetails=dataSourceDetails}}`);
    assert.ok(findAll('.rsa-context-panel__config-grid__layout')[2].textContent.indexOf('Manufacturer'), 'Manufacturer label name is taken from resultList response');
  });

  test('Business process is displayed as panel', async function(assert) {
    new ReduxDataHelper(setState)
      .setData('context', contextData)
      .build();
    const content = [ 'Process 1', 'Process 2', 'Process 3', 'Process 4' ];
    this.set('content', content);
    await render(hbs`{{context-panel/dynamic-grid/group
          groupData=content
          title='context.archer.businessProcesses'
          }}`);
    assert.equal(findAll('.rsa-context-panel__grid__host-details__tetheredPanel')[0].textContent.trim(), '4');
  });
});
