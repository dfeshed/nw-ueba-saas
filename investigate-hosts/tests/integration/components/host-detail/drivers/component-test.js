import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { find, findAll, render, click } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { revertPatch } from '../../../../helpers/patch-reducer';
import { patchReducer } from '../../../../helpers/vnext-patch';
import driversState from '../../state/driver-files';

let initState;

module('Integration | Component | Drivers', function(hooks) {
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

  test('Drivers table loaded', async function(assert) {
    new ReduxDataHelper(initState).drivers(driversState).build();
    await render(hbs`{{host-detail/drivers}}`);
    assert.equal(findAll('.simple-detail-display-wrapper').length, 1, 'Drivers content loaded');
    assert.equal(findAll('.rsa-data-table-body-rows .rsa-data-table-body-row').length, 1, 'Driver data loaded');
  });

  test('Driver column names', async function(assert) {
    new ReduxDataHelper(initState).drivers(driversState).build();
    await render(hbs`{{host-detail/drivers}}`);

    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(1)').textContent.trim(), '', 'Column 1 is checkbox');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(2)').textContent.trim(), 'Filename', 'Column 2 is Fileame');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(3)').textContent.trim(), 'Risk Score', 'Column 3 is Risk Score');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(4)').textContent.trim(), 'Reputation Status', 'Column 4 is Reputation Status');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(5)').textContent.trim(), 'File Status', 'Column 5 is File Status');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(6)').textContent.trim(), 'Signature', 'Column 6 is Signature');
    assert.equal(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(7)').textContent.trim(), 'Path', 'Column 7 is Path');
  });
  test('Select a driver and check file-action-bar', async function(assert) {
    const selected = [ {}, {}, {} ];
    new ReduxDataHelper(initState)
    .drivers(driversState)
    .selectedDriverList(selected)
    .build();

    await render(hbs`{{host-detail/drivers}}`);
    assert.equal(find('.file-status-button').classList.contains('is-disabled'), false, 'Edit file status button should be disabled');
    await click(find('.rsa-data-table-header-row .rsa-data-table-header-cell:nth-child(1) .rsa-form-checkbox-label .rsa-form-checkbox'));
    assert.equal(find('.file-status-button').classList.contains('is-disabled'), true, 'Edit file status button should be disabled');
  });
});