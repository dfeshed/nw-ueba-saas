import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import layout from 'component-lib/components/rsa-data-table/body-row/template';
import computed, { alias } from 'ember-computed-decorators';
import SortableItemMixin from 'ember-sortable/mixins/sortable-item';

/**
 * Extension of the Data Table default row class for supporting selecting and dragging of rows
 * @public
 */
export default DataTableBodyRow.extend(SortableItemMixin, {
  classNameBindings: ['isSelected'],
  layout,
  @alias('item') model: null, // used by ember-sortable
  @alias('table.sortableGroup') group: null, // used by ember-sortable
  handle: '.rsa-data-table-body-cell', // used by ember-sortable
  /**
   * Tracks which item is currently selected / highlighted by the user
   * @property isSelected
   * @public
   */
  @computed('table.selectedItemId', 'item.id')
  isSelected(selectedItemId, itemId) {
    return selectedItemId === itemId;
  }
});