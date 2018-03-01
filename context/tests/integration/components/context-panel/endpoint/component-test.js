import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import endpointData from '../../../../data/endpoint';
import * as ACTION_TYPES from 'context/actions/types';
import EmberObject from '@ember/object';
import machineDetails from 'context/config/machines';
import modulesDetails from 'context/config/endpoint-modules';
import IOCdetails from 'context/config/endpoint-ioc';

const endpointDetails = {
  IOC: {
    class: 'report-problem-diamond',
    dataSourceType: 'IOC',
    displayType: 'table',
    field: 'IOC',
    tabRequired: false,
    title: 'context.header.iioc',
    details: IOCdetails
  },
  Machines: {
    class: 'network-computers-2',
    dataSourceType: 'Machines',
    displayType: 'grid',
    field: 'Machines',
    tabRequired: false,
    title: 'context.hostSummary.title',
    details: machineDetails
  },
  Modules: {
    class: 'graph',
    dataSourceType: 'Modules',
    displayType: 'table',
    field: 'Modules',
    tabRequired: false,
    title: 'context.header.modules',
    details: modulesDetails
  }
};
const dataSource = {
  class: 'graph',
  dataSourceType: 'Endpoint',
  datasourceGroup: 'Endpoint',
  displayType: 'table',
  details: endpointDetails,
  field: 'Endpoint',
  tabRequired: true,
  title: 'context.header.endpoint'
};

moduleForComponent('context-panel/endpoint', 'Integration | Component | context panel/endpoint', {
  integration: true,
  beforeEach() {
    this.inject.service('redux');
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});

test('Test to display endpoint', function(assert) {

  const contextData = EmberObject.create({});
  contextData.set('Machines', endpointData);
  this.set('contextData', contextData);
  this.set('dSDetails', endpointDetails.Machines);


  this.set('i18n', this.get('i18n'));
  this.get('redux').dispatch({
    type: ACTION_TYPES.INITIALIZE_CONTEXT_PANEL,
    payload: {
      lookupKey: '1.1.1.1',
      meta: 'IP'
    }
  });
  this.get('redux').dispatch({
    type: ACTION_TYPES.UPDATE_ACTIVE_TAB,
    payload: 'Endpoint'
  });
  this.get('redux').dispatch({
    type: ACTION_TYPES.UPDATE_ACTIVE_TAB,
    payload: 'Endpoint'
  });

  this.set('dataSource', dataSource);
  this.get('redux').dispatch({
    type: ACTION_TYPES.GET_ALL_DATA_SOURCES,
    payload: [dataSource]
  });
  this.get('redux').dispatch({
    type: ACTION_TYPES.GET_LOOKUP_DATA,
    payload: [endpointData]
  });
  this.render(hbs `{{context-panel/endpoint dataSource=dataSource.details contextData=contextData i18n=i18n}}`);
  assert.equal(this.$('.rsa-context-panel__endpoint').length, 1, 'Testing number of datasource displayed');
});
