/**
 * @file Data Table Cell mixin
 * Uses the ember-cli-csp-style addon to bind a component's DOM element to a `_resolvedWidth` attribute,
 * which in turn is computed from either a `width` or `column.width` attribute.
 * @public
 */
import HasTableParent from './has-table-parent';
import computed, { equal } from 'ember-computed-decorators';
import { htmlSafe } from 'ember-string';
import { isEmpty } from '@ember/utils';
import Mixin from '@ember/object/mixin';

// Default column width if none given.
const DEFAULT_WIDTH = 100;

// Parses a given width value into a number and units (if any).
// If the value 'auto' is given, returns `{ auto: true }`.
// Example: parses '24.5%' into `{ number: 24.5, units: '%' }`.
function _parseNumberAndUnits(value) {
  if (!isEmpty(value)) {
    if (value === 'auto') {
      return { auto: true };
    } else {
      const match = String(value).match(/([\d\.]+)([^\d]*)/);
      const num = match && Number(match[1]);
      const units = (match && match[2]) || '';

      if (!isNaN(num)) {
        return { num, units };
      }
    }
  }
  return null;
}

export default Mixin.create(HasTableParent, {
  classNameBindings: ['_resolvedWidthIsAuto:auto-width', 'isError', 'isSorted:is-sorted', 'sortDir'],

  attributeBindings: ['style'],

  @computed('_resolvedWidth')
  style(_resolvedWidth) {
    return htmlSafe(`width: ${_resolvedWidth};`);
  },


  /**
   * Optional width for this cell.
   * Can be a string with units (e.g., '24px', '50%') or a number (24). If number, 'px' is assumed.
   * @type {number|string}
   * @public
   */
  columnWidth: null,

  /**
   * Optional column model that corresponds to this cell.
   * If the table's columns were defined declaratively, this won't be defined.
   * @type {object}
   * @public
   */
  column: null,

  /**
   * A boolean to set when the cell should be in the error state
   * @type {boolean}
   * @public
   */
  isError: false,

  /**
   * Data object corresponding to this cell's row.
   * @type {object}
   * @public
   */
  item: null,

  /**
   * Row index; i.e., index of `item` relative to the table's entire `items` array (NOT to the first visible row).
   * @type {number}
   * @public
   */
  index: 0,

  /**
   * Computes the width (as a CSS string value) to be applied to component's DOM element.
   * If the `width` attribute is defined, it gets precedence; otherwise look for a width in `column`.
   * If both are missing, resort to a hard-coded default.
   * In any case, ensure the width has some sort of unit (if none, assume pixels).
   * @type string
   * @private
   */
  @computed('columnWidth', 'column.width')
  _resolvedWidth(definedColumnWidth, columnInstanceWidth) {
    const w = _parseNumberAndUnits(definedColumnWidth) ||
    _parseNumberAndUnits(columnInstanceWidth) ||
    _parseNumberAndUnits(DEFAULT_WIDTH);
    return w.auto ? 'auto' : `${w.num}${w.units || 'px'}`;
  },

  // Resolves to `true` when `resolvedWidth` is 'auto'.
  @equal('_resolvedWidth', 'auto') _resolvedWidthIsAuto: null,

  @equal('column.field', 'currentSort.field') isSorted: null,

  @computed('isSorted', 'currentSort.direction')
  sortDir(isSorted, currentDir) {
    if (isSorted) {
      return currentDir;
    }
  }

});
