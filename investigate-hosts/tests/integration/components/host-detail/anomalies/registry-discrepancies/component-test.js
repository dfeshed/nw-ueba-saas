import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import anomaliesRegistryDiscrepancies from '../../../state/anomalies.registryDiscrepancies';
import machineIdentity from '../../../state/anomalies.machineIdentity';

let initState;

module('Integration | Component | Anomalies/Registry Discrepancies', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-hosts')
  });

  hooks.beforeEach(function() {
    initState = (state) => {
      patchReducer(this, Immutable.from(state));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.afterEach(function() {
    revertPatch();
  });

  test('Registry Discrepancies column names', async function(assert) {
    new ReduxDataHelper(initState).machineIdentity(machineIdentity).registryDiscrepancies(anomaliesRegistryDiscrepancies).build();
    await render(hbs`{{host-detail/anomalies}}`);
    await click('.rsa-nav-tab:nth-child(4)');

    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(1)').textContent.trim(), 'Hive', 'Column 1 is Hive');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(2)').textContent.trim(), 'Reason', 'Column 2 is Reason');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(3)').textContent.trim(), 'Registry Path', 'Column 3 is Registry Path');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(4)').textContent.trim(), 'Raw Type', 'Column 4 is Raw Type');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(5)').textContent.trim(), 'Raw Data', 'Column 5 is Raw Data');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(6)').textContent.trim(), 'Api Type', 'Column 6 is Api Type');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(7)').textContent.trim(), 'Api Data', 'Column 7 is Api Data');

  });
});