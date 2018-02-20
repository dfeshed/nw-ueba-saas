import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import customFilterData from '../../../state/custom-filter-data';

moduleForComponent('custom-filters/custom-filter-list', 'Integration | Component | custom filters/custom filter list', {
  integration: true,
  resolver: engineResolverFor('investigate-files')
});

test('Title attribute & text of the custom filter passed should be rendered', function(assert) {
  assert.expect(3);
  this.set('filter', customFilterData.fileFilters.data[0]);
  this.set('applyCustomFilter', () => {});
  this.render(hbs`{{custom-filters/custom-filter-list filter=filter applyCustomFilter=applyCustomFilter}}`);
  const result = this.$('.filter-list__item-label');
  assert.equal(result.length, 1, 'Length of filter list item rendered');
  assert.equal(result.text().trim(), 'entropy_less_than_3', 'Text of the filter list item');
  assert.equal(result.attr('title'), 'entropy_less_than_3', 'Title attribute of filter list item');
});

test('Computed property upon setting custom-filter list-item', function(assert) {
  this.set('filter', customFilterData.fileFilters.data[0]);
  this.set('activeFilter', '5a6830ec3f11d6700d9ca761');
  this.set('reset', false);
  this.set('isSystemFilter', false);
  this.set('applyCustomFilter', 'applyCustomFilter');
  this.render(hbs`{{custom-filters/custom-filter-list filter=filter activeFilter=activeFilter reset=reset isSystemFilter=isSystemFilter applyCustomFilter=applyCustomFilter}}`);
  assert.equal(this.$('.filter-list__item').hasClass('is-active'), true, 'Computed property calculated correctly upon setting custom-filter list-item');
});

test('Computed property upon resetting system-filter list-item', function(assert) {
  this.set('filter', customFilterData.fileFilters.data[0]);
  this.set('activeFilter', '5a6830ec3f11d6700d9ca761');
  this.set('reset', true);
  this.set('isSystemFilter', false);
  this.set('applyCustomFilter', 'applyCustomFilter');
  this.render(hbs`{{custom-filters/custom-filter-list filter=filter activeFilter=activeFilter reset=reset isSystemFilter=isSystemFilter applyCustomFilter=applyCustomFilter}}`);
  assert.equal(this.$('.filter-list__item').hasClass('is-active'), false, 'Computed property calculated correctly upon resetting custom-filter list-item');
});

test('Computed property when custom-filter id & active filter id is different', function(assert) {
  this.set('filter', customFilterData.fileFilters.data[0]);
  this.set('activeFilter', '5a6830ec3f11d6700d9ca762');
  this.set('reset', false);
  this.set('isSystemFilter', false);
  this.set('applyCustomFilter', 'applyCustomFilter');
  this.render(hbs`{{custom-filters/custom-filter-list filter=filter activeFilter=activeFilter reset=reset isSystemFilter=isSystemFilter applyCustomFilter=applyCustomFilter}}`);
  assert.equal(this.$('.filter-list__item').hasClass('is-active'), false, 'Computed property calculated correctly when active filter and passed filter id are different');
});

test('Computed property when system filter boolean is set for custom filter', function(assert) {
  this.set('filter', customFilterData.fileFilters.data[0]);
  this.set('activeFilter', '5a6830ec3f11d6700d9ca761');
  this.set('reset', false);
  this.set('isSystemFilter', true);
  this.set('applyCustomFilter', 'applyCustomFilter');
  this.render(hbs`{{custom-filters/custom-filter-list filter=filter activeFilter=activeFilter reset=reset isSystemFilter=isSystemFilter applyCustomFilter=applyCustomFilter}}`);
  assert.equal(this.$('.filter-list__item').hasClass('is-active'), false, 'Computed property calculated correctly when system filter boolean is set for custom filter');
});

test('Mouse hover on custom filter', function(assert) {
  assert.expect(2);
  this.set('filter', customFilterData.fileFilters.data[0]);
  this.set('applyCustomFilter', 'applyCustomFilter');
  this.render(hbs`{{custom-filters/custom-filter-list filter=filter applyCustomFilter=applyCustomFilter}}`);
  this.$('.filter-list__item-label').mouseenter(); // Mouse entering custom filter item
  assert.equal(this.$('.filter-list__item').hasClass('is-hovering'), true, 'Mouse entered/hovered on custom filter');
  this.$('.filter-list__item-label').mouseleave(); // Mouse leaving custom filter item
  assert.equal(this.$('.filter-list__item').hasClass('is-hovering'), false, 'Mouse unhovered on custom filter');
});