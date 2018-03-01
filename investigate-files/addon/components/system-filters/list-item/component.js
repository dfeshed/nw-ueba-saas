import Component from '@ember/component';
import computed from 'ember-computed-decorators';

export default Component.extend({
  tagName: 'li',

  classNames: ['filter-list__item'],

  classNameBindings: ['isActive'],

  filter: null,

  activeFilter: null,

  isSystemFilter: false,

  @computed('filter', 'activeFilter', 'reset', 'isSystemFilter')
  isActive: (filter, activeFilter, reset, isSystemFilter) =>
    (filter.filterId === activeFilter && !reset && isSystemFilter),

  click() {
    this.applyFilter(this.get('filter'));
  }
});
