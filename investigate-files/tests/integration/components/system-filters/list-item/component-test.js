import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolver from '../../../../helpers/engine-resolver';

moduleForComponent('system-filters/list-item', 'Integration | Component | System Filters/list item', {
  integration: true,
  resolver: engineResolver('investigate-files')
});

const filterList = [
  {
    favourite: 'true',
    label: 'investigateFiles.filter.windows',
    filterId: '1',
    expression: { propertyName: 'machineOsType', restrictionType: 'IN', propertyValues: [{ value: 'windows' }] }
  }
];

test('Title attribute & text of the system filter passed should be rendered', function(assert) {
  assert.expect();
  this.set('filter', filterList[0]);
  this.render(hbs`{{system-filters/list-item filter=filter}}`);
  assert.equal(this.$('.filter-list__item-label').length, 1, 'Length of filter list item rendered');
  assert.equal(this.$('.filter-list__item-label').text().trim(), 'WINDOWS', 'Text of the filter list item');
  assert.equal(this.$('.filter-list__item-label').attr('title'), 'WINDOWS', 'Title attribute of filter list item');
});

test('Computed property upon setting system-filter list-item', function(assert) {
  this.set('filter', filterList[0]);
  this.set('activeFilter', '1');
  this.set('reset', false);
  this.set('isSystemFilter', true);
  this.render(hbs`{{system-filters/list-item filter=filter activeFilter=activeFilter reset=reset isSystemFilter=isSystemFilter}}`);
  assert.equal(this.$('.filter-list__item').hasClass('is-active'), true, 'Computed property calculated correctly upon setting system-filter list-item');
});

test('Computed property upon resetting system-filter list-item', function(assert) {
  this.set('filter', filterList[0]);
  this.set('activeFilter', '1');
  this.set('reset', true);
  this.set('isSystemFilter', true);
  this.render(hbs`{{system-filters/list-item filter=filter activeFilter=activeFilter reset=reset isSystemFilter=isSystemFilter}}`);
  assert.equal(this.$('.filter-list__item').hasClass('is-active'), false, 'Computed property calculated correctly upon resetting system-filter list-item');
});