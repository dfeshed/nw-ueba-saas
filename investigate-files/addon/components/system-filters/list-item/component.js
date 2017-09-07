import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import layout from './template';

export default Component.extend({
  layout,

  tagName: 'li',

  classNames: ['filter-list__item'],

  classNameBindings: ['isActive'],

  filter: null,

  activeFilter: null,

  @computed('filter', 'activeFilter', 'reset')
  isActive: (filter, activeFilter, reset) => filter.filterId === activeFilter && !reset,

  click() {
    this.applyFilter(this.get('filter'));
  }
});
