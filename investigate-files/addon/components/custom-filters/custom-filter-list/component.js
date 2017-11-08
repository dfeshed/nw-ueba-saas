import Component from 'ember-component';
import computed from 'ember-computed-decorators';

export default Component.extend({
  tagName: 'li',

  classNames: ['filter-list__item'],

  classNameBindings: ['isActive', 'isHovering'],

  filter: null,

  activeFilter: null,

  @computed('filter', 'activeFilter', 'reset', 'isSystemFilter')
  isActive: (filter, activeFilter, reset, isSystemFilter) => filter.id === activeFilter && !reset && !isSystemFilter,

  mouseEnter() {
    this.set('isHovering', true);
  },

  mouseLeave() {
    this.set('isHovering', false);
  }
});