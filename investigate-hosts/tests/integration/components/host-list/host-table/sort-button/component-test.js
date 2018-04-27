import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { findAll, render, click } from '@ember/test-helpers';
import engineResolver from '../../../../../helpers/engine-resolver';

module('Integration | Component | host-list/host-table/sort-button', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolver('investigate-hosts')
  });

  test('it renders the sort button with direction of sort', async function(assert) {
    assert.expect(2);
    this.set('sortField', 'machine.machineName');
    this.set('isSortDescending', true);
    this.set('column', {
      field: 'machine.machineName'
    });

    await render(hbs`{{host-list/host-table/sort-button sortField=sortField column=column isSortDescending=isSortDescending}}`);

    assert.equal(findAll('.hideSort').length, 0, 'Sort button is displayed');
    assert.equal(findAll('.rsa-icon-arrow-down-7-filled').length, 1, 'Sort descending');
  });

  test('it hides sort button', async function(assert) {
    assert.expect(1);
    this.set('sortField', 'machine.machineName1');
    this.set('isSortDescending', false);
    this.set('column', {
      field: 'machine.machineName'
    });

    await render(hbs`{{host-list/host-table/sort-button sortField=sortField column=column isSortDescending=isSortDescending}}`);

    assert.equal(findAll('.hideSort').length, 1, 'Sort descending');
  });

  test('calls the sortBy action with sortConfig', async function(assert) {
    assert.expect(2);
    this.set('sortField', 'machine.machineName');
    this.set('isSortDescending', false);
    this.set('column', {
      field: 'machine.machineName'
    });
    this.set('sortBy', function(sortField) {
      assert.equal(sortField.key, 'machine.machineName');
      assert.equal(sortField.descending, true);
    });

    await render(hbs`{{host-list/host-table/sort-button sortField=sortField sortBy=(action sortBy) column=column isSortDescending=isSortDescending}}`);

    await click('.column-sort');

  });
});