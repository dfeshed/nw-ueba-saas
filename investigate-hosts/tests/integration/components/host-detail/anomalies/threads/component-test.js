import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import anomaliesThreads from '../../../state/anomalies.threads';

let initState;

module('Integration | Component | Anomalies/Threads', function(hooks) {
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

  test('Threads content loaded', async function(assert) {
    new ReduxDataHelper(initState).anomalies(anomaliesThreads).build();
    await render(hbs`{{host-detail/anomalies}}`);
    await click('.rsa-nav-tab:nth-child(3)');

    assert.equal(findAll('.simple-detail-display-wrapper').length, 1, 'Threads content loaded');
    assert.equal(findAll('.rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'Threads data loaded');
  });

  test('Threads column names', async function(assert) {
    new ReduxDataHelper(initState).anomalies(anomaliesThreads).build();
    await render(hbs`{{host-detail/anomalies}}`);
    await click('.rsa-nav-tab:nth-child(3)');

    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(1)').textContent.trim(), 'Start Address', 'Column 1 is Start Address');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(2)').textContent.trim(), 'DLL Name', 'Column 2 is DLL Name');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(3)').textContent.trim(), 'Reputation Status', 'Column 3 is Reputation Status');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(4)').textContent.trim(), 'Process', 'Column 4 is Process');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(5)').textContent.trim(), 'Signature', 'Column 5 is Signature');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(6)').textContent.trim(), 'Thread ID', 'Column 6 is Thread ID');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(7)').textContent.trim(), 'Thread Environment Block', 'Column 7 is Thread Environment Block');
  });
});