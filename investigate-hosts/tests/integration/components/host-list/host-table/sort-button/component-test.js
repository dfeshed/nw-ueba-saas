import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../../helpers/engine-resolver';

moduleForComponent('host-list/host-table/sort-button', 'Integration | Component | Sort Button', {
  integration: true,
  resolver: engineResolverFor('investigate-hosts'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
  }
});


test('it renders the sort button with direction of sort', function(assert) {

  this.set('sortField', 'machine.machineName');
  this.set('isSortDescending', 'false');
  this.set('column', {
    field: 'machine.machineName'
  });

  this.render(hbs`{{host-list/host-table/sort-button sortField=sortField column=column sSortDescending=isSortDescending}}`);

  assert.equal(this.$('.hideSort').length, 0, 'Sort button is displayed');
  assert.equal(this.$('.rsa-icon-arrow-up-7-filled').length, 1, 'Sort descending');
});

test('it hides sort button', function(assert) {

  this.set('sortField', 'machine.machineName1');
  this.set('isSortDescending', 'true');
  this.set('column', {
    field: 'machine.machineName'
  });

  this.render(hbs`{{host-list/host-table/sort-button sortField=sortField column=column sSortDescending=isSortDescending}}`);

  assert.equal(this.$('.hideSort').length, 1, 'Sort descending');
});


test('calls the sortBy action with sortConfig', function(assert) {

  this.set('sortField', 'machine.machineName');
  this.set('isSortDescending', 'false');
  this.set('column', {
    field: 'machine.machineName'
  });
  this.set('sortBy', function(sortField) {
    assert.equal(sortField.key, 'machine.machineName');
    assert.equal(sortField.descending, true);
  });

  this.render(hbs`{{host-list/host-table/sort-button sortField=sortField sortBy=(action sortBy) column=column sSortDescending=isSortDescending}}`);

  this.$('.column-sort').click();

});
