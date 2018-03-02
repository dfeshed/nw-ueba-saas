import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { htmlSafe } from '@ember/string';
import $ from 'jquery';

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
  @computed('table.scrollerSize.innerWidth')
  scrollerStyle(width) {
    const styleText = $.isNumeric(width) ? `width: ${width}px` : '';
    return htmlSafe(styleText);
  },

  // Computes a width that ensures the header cells spread as wide horizontally as the body cells.
  @computed('table.totalColumnsWidth')
  placeholderStyle(width) {
    return htmlSafe(`width:${width}`);
  },

  // Computes a transform that will ensure the header cells stay horiz aligned with the table body's scrollLeft.
  @computed('table.scrollerPos.left')
  tableStyle(scrollLeft) {
    const px = $.isNumeric(scrollLeft) ? scrollLeft : 0;
    return htmlSafe(`left: -${px}px`);
  }
});
