import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import hostListState from '../state/host.machines';
import endpoint from '../state/schema';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let initState;

module('Integration | Component | host-list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
  });

  test('it renders error page when endpointserver is offline', async function(assert) {
    new ReduxDataHelper(initState)
    .isEndpointServerOffline(true)
    .build();
    await render(hbs`{{host-list}}`);
    assert.equal(findAll('.host-list-items').length, 0, 'host list is not rendered');
    assert.equal(findAll('.error-page').length, 1, 'endpoint server is offline');
  });

  test('it renders error page when endpointserver is online', async function(assert) {
    new ReduxDataHelper(initState)
    .columns(endpoint.schema)
    .hostList(hostListState.machines.hostList)
    .hostSortField('machine.machineName')
    .isEndpointServerOffline(false)
    .build();
    await render(hbs`{{host-list}}`);
    assert.equal(findAll('.error-page').length, 0, 'endpoint server is online');
  });
  test('it renders host action bar by default', async function(assert) {
    await render(hbs`{{host-list}}`);
    assert.equal(findAll('.host-table__toolbar').length, 1, 'host table action bar is rendered by default');
  });
});
