import Component from 'ember-component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  tagName: 'li',

  classNames: ['filter-list__item'],

  classNameBindings: ['isActive'],

  filter: null,

  activeFilter: null,

  @computed('filter', 'activeFilter', 'reset')
  isActive: (filter, activeFilter, reset) => filter.id === activeFilter && !reset,

  click() {
    this.applyFilter(this.get('filter'));
  }
});
