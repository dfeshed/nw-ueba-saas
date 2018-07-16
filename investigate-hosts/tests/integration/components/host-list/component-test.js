import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import hostListState from '../state/host.machines';
import endpoint from '../state/schema';
import { patchReducer } from '../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

let initState;

module('Integration | Component | host-list', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('risk panel is not rendered on select icon click', async function(assert) {
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostListState.machines.hostList)
      .hostSortField('machine.machineName')
      .selectedHostList([])
      .build();

    await render(hbs`{{host-list}}`);
    await click('.rsa-form-checkbox-label:first-child');
    assert.equal(findAll('.show-risk-panel').length, 0, 'Risk panel is not visible on checkbox click');
  });

  test('risk panel is not rendered on select checkbox click', async function(assert) {
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostListState.machines.hostList)
      .hostSortField('machine.machineName')
      .selectedHostList([])
      .build();
    await render(hbs`{{host-list}}`);

    await click('.rsa-form-checkbox-label:first-child');
    assert.equal(findAll('.show-risk-panel').length, 0, 'Risk panel is not visible');
  });

  test('risk panel is rendered on row click', async function(assert) {
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .hostList(hostListState.machines.hostList)
      .hostSortField('machine.machineName')
      .build();
    await render(hbs`{{host-list}}`);
    await click('.rsa-data-table-body-row:first-child');
    assert.equal(findAll('.show-risk-panel').length, 1, 'Risk panel is visible');
  });
});
