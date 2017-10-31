import Component from 'ember-component';
import layout from './template';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,

  tagName: 'li',

  classNames: ['filter-list__item'],

  classNameBindings: ['isActive', 'isHovering'],

  filter: null,

  activeFilter: null,

  applyFilter: '',

  deleteFilter: '',

  @computed('filter', 'activeFilter', 'reset')
  isActive: (filter, activeFilter, reset) => filter.id === activeFilter && !reset,

  mouseEnter() {
    this.set('isHovering', true);
  },

  mouseLeave() {
    this.set('isHovering', false);
  }
});
