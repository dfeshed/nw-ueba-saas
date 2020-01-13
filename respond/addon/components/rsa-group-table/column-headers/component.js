import { computed } from '@ember/object';
import Component from '@ember/component';
import layout from './template';
import { htmlSafe } from '@ember/string';
import { isNumeric } from 'component-lib/utils/jquery-replacement';

/**
 * @class Group Table Column Headers Component
 * Renders a set of column headers, one for each visible column of a parent Group Table component.
 * Optional component that provides a UI for table features such as resizing column widths and re-ordering columns.
 * @public
 */
export default Component.extend({
  tagName: 'header',
  layout,
  classNames: ['rsa-group-table-column-headers'],

  /**
   * Configurable name of the Ember Component class to be used for rendering the contents of each header cell.
   * @type {String}
   * @public
   */
  columnHeaderComponentClass: 'rsa-group-table/column-header',

  /**
   * Reference to the parent table component. Typically passed down from parent.
   * @type {Ember.Component}
   * @private
   */
  table: null,

  // Applies the scrollbox's width to the wrapper element to ensure horizontal alignment with scrollbox columns.
  scrollerStyle: computed('table.scrollerSize.innerWidth', function() {
    const styleText = isNumeric(this.table?.scrollerSize?.innerWidth) ? `width: ${this.table?.scrollerSize?.innerWidth}px` : '';
    return htmlSafe(styleText);
  }),

  // Computes a width that ensures the header cells spread as wide horizontally as the body cells.
  placeholderStyle: computed('table.totalColumnsWidth', function() {
    return htmlSafe(`width:${this.table?.totalColumnsWidth}`);
  }),

  // Computes a transform that will ensure the header cells stay horiz aligned with the table body's scrollLeft.
  tableStyle: computed('table.scrollerPos.left', function() {
    const px = isNumeric(this.table?.scrollerPos?.left) ? this.table?.scrollerPos?.left : 0;
    return htmlSafe(`left: -${px}px`);
  })
});
