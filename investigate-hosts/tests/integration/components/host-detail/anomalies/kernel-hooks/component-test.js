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
import anomaliesKernelHooks from '../../../state/anomalies.kernelHooks';

let initState;

module('Integration | Component | Anomalies/kernel-hooks', function(hooks) {
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

  test('Kernel Hooks content loaded', async function(assert) {
    new ReduxDataHelper(initState).anomalies(anomaliesKernelHooks).build();
    await render(hbs`{{host-detail/anomalies}}`);
    await click('.rsa-nav-tab:nth-child(2)');

    assert.equal(findAll('.simple-detail-display-wrapper').length, 1, 'Kernel Hooks content loaded');
    assert.equal(findAll('.rsa-data-table-body-rows .rsa-data-table-body-row').length, 3, 'Kernel Hooks data loaded');
  });

  test('Kernel Hooks column names', async function(assert) {
    new ReduxDataHelper(initState).anomalies(anomaliesKernelHooks).build();
    await render(hbs`{{host-detail/anomalies}}`);
    await click('.rsa-nav-tab:nth-child(2)');

    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(1)').textContent.trim(), 'Type', 'Column 1 is Type');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(2)').textContent.trim(), 'Driver Name', 'Column 2 is Driver Name');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(3)').textContent.trim(), 'Reputation Status', 'Column 3 is Reputation Status');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(4)').textContent.trim(), 'Signature', 'Column 4 is Signature');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(5)').textContent.trim(), 'Object Function', 'Column 5 is Object Function');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(6)').textContent.trim(), 'Hooked FileName', 'Column 6 is Hooked FileName');
  });
});