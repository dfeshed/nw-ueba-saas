import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import layout from './template';

export default Component.extend({
  layout,

  tagName: 'li',

  classNames: ['filter-list__item'],

  classNameBindings: ['isActive', 'isHovering'],

  filter: null,

  selectedFilterId: null,

  applyCustomFilter: null,

  @computed('filter', 'selectedFilterId')
  isActive: (filter, selectedFilterId) => filter.id === selectedFilterId,

  mouseEnter() {
    this.set('isHovering', true);
  },

  mouseLeave() {
    this.set('isHovering', false);
  }
});
