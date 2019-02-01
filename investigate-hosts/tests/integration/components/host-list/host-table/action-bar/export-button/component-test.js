import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';
import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import hostListState from '../../../../state/host.machines';

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
      .hostList(hostListState.machines.hostList)
      .hostSortField('machineIdentity.machineName')
      .build();
    await render(hbs`{{host-list/host-table/action-bar/export-button}}`);
    assert.equal(findAll('.rsa-form-button-wrapper button .rsa-loader').length, 1, 'loader is rendered');
  });

  test('it renders host table action bar export button when exportStatus is completed', async function(assert) {
    new ReduxDataHelper(setState)
      .hostList(hostListState.machines.hostList)
      .hostSortField('machineIdentity.machineName')
      .hostExportStatus('completed')
      .build();
    await render(hbs`{{host-list/host-table/action-bar/export-button}}`);
    assert.equal(findAll('.rsa-form-button-wrapper button .rsa-loader').length, 0, 'loader is not present');
    assert.equal(findAll('.rsa-form-button-wrapper button .rsa-icon-file-zipped-filled').length, 1, 'default export button is rendered');
    assert.equal(find('.rsa-form-button-wrapper button .rsa-icon-file-zipped-filled').getAttribute('title').trim(), 'Export to CSV', 'Export to CSV button is rendered.');
  });

  test('Export to CSV title when broker view', async function(assert) {
    const endpointQuery = {
      serverId: 'e82241fc-0681-4276-a930-dd6e5d00f152'
    };
    const services = {
      serviceData: [{ id: 'e82241fc-0681-4276-a930-dd6e5d00f152', displayName: 'TEST', name: 'endpoint-broker-server', version: '11.1.0.0' }],
      summaryData: { startTime: 0 },
      isServicesRetrieveError: false
    };
    new ReduxDataHelper(setState)
      .hostList(hostListState.machines.hostList)
      .hostSortField('machineIdentity.machineName')
      .hostExportStatus('completed')
      .endpointQuery(endpointQuery)
      .services(services)
      .build();
    await render(hbs`{{host-list/host-table/action-bar/export-button}}`);
    assert.equal(findAll('.export-button .rsa-icon-file-zipped-filled')[0].title, 'Export to CSV is not supported for Endpoint Broker', 'Title for broker view.');
  });

});
