import Component from '@ember/component';
import layout from './template';

/**
 * @class Group Table Header Component
 * Renders a row of column headers for a Group Table component.
 * Each column header is a cell which enables such features as resizing column widths and re-ordering columns.
 * @public
 */
export default Component.extend({
  layout,
  classNames: ['rsa-group-table-group-item-cell'],

  // Reference to the group.item data object that corresponds to this component. Typically passed down from parent.
  item: null,

  // The index of `group` relative to the table's `groups` array. Typically passed down from parent.
  index: 0,

  /**
   * The object representing the corresponding table column for this component. Typically passed down from parent.
   * @type {Ember.Object}
   * @public
   */
  column: null,

  /**
   * The 0-based index of `column` among the visible columns of the table. Typically passed down from parent.
   * @type {Ember.Object}
   * @public
   */
  columnIndex: null
});
