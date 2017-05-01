import DataTableBodyRow from 'component-lib/components/rsa-data-table/body-row/component';
import computed from 'ember-computed-decorators';

/**
 * Extension of the Data Table default row class for supporting focus on the row
 * @public
 */
export default DataTableBodyRow.extend({
  classNameBindings: ['hasFocus'],
  /**
   * True if the row item is the same as the "focusedItem" that tracks which item is currently focused / highlighted by the user
   * @property hasFocus
   * @public
   */
  @computed('table.focusedItem', 'item')
  hasFocus(focusedItem, item) {
    return focusedItem === item;
  }
});