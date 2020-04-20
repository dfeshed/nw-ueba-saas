import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { findAll, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { revertPatch } from '../../../../helpers/patch-reducer';
import endpointData from '../../../../data/endpoint';
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

let setState;
module('Integration | Component | context panel/endpoint', function(hooks) {
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

  test('Test to display endpoint', async function(assert) {

    this.set('details', dataSource.details);

    new ReduxDataHelper(setState)
      .initializeContextPanel({ lookupKey: '1.1.1.1',
        meta: 'IP' })
      .setActiveTabName('Endpoint')
      .setDataSources([dataSource])
      .setLookupData([endpointData])
      .build();

    await render(hbs `{{context-panel/endpoint dataSource=details}}`);

    assert.equal(findAll('.rsa-context-panel__endpoint').length, 1, 'Testing number of datasource displayed');
  });

});
