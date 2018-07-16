import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, render, waitUntil, click } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import Immutable from 'seamless-immutable';

import engineResolver from 'ember-engines/test-support/engine-resolver-for';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

import endpoint from '../../state/schema';
import machines from '../../state/host.machines';

let initState;

module('Integration | Component | host-list/host-table', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });
  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('it renders data table with column sorted by name', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(initState)
      .columns(endpoint.schema)
      .build();
    await render(hbs`{{host-list/host-table}}`);
    assert.equal(find('.rsa-data-table-header-cell:nth-child(2)').textContent.trim(), 'Hostname', 'Second column should be hostname');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(4)').textContent.trim(), 'Agent Version', 'Fourth column should be Agent Version');
    assert.equal(find('.rsa-data-table-header-cell:nth-child(6)').textContent.trim(), 'Operating System', 'Sixth column should be Operating System');
  });

  test('check if the risk panel toogles when the same row is clicked consecutively', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(initState)
        .columns(endpoint.schema)
        .hostSortField(machines.machines.hostColumnSort)
        .hostList(machines.machines.hostList)
        .build();
    await render(hbs`{{host-list/host-table}}`);
    waitUntil(() => find('.rsa-data-table-body-rows'));
    await click('.host-table .rsa-data-table-body-row:nth-child(1)');
    let state = this.owner.lookup('service:redux').getState();
    assert.equal(state.endpoint.visuals.showRiskPanel, true, 'Risk panel visible');
    await click('.host-table .rsa-data-table-body-row:nth-child(1)');
    state = this.owner.lookup('service:redux').getState();
    assert.equal(state.endpoint.visuals.showRiskPanel, false, 'Clicking same row makes Risk panel close');
  });
});