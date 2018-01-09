import computed, { alias } from 'ember-computed-decorators';
import SortableItemMixin from 'ember-sortable/mixins/sortable-item';
import Component from '@ember/component';
/**
 * Extension of the Data Table default row class for supporting selecting and dragging of rows
 * @public
 */
export default Component.extend(SortableItemMixin, {
  tagName: 'tr',
  classNameBindings: ['isSelected'],
  @alias('rule') model: null, // used by ember-sortable
  handle: 'tr', // used by ember-sortable
  /**
   * Tracks which item is currently selected / highlighted by the user
   * @property isSelected
   * @public
   */
  @computed('selectedItemId', 'rule.id')
  isSelected(selectedItemId, itemId) {
    return selectedItemId === itemId;
  },

  onRowClick() {},

  click() {
    this.get('onRowClick').apply(this, arguments);
    return false;
  }
});