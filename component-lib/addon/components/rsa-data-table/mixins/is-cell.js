/**
 * @file Data Table Cell mixin
 * Uses the ember-cli-csp-style addon to bind a component's DOM element to a `_resolvedWidth` attribute,
 * which in turn is computed from either a `width` or `column.width` attribute.
 * @public
 */
import Ember from 'ember';
import CspStyleMixin from 'ember-cli-csp-style/mixins/csp-style';
import HasTableParent from './has-table-parent';

const {
  computed,
  isEmpty,
  Mixin
} = Ember;

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
      let match = String(value).match(/([\d\.]+)([^\d]*)/);
      let num = match && Number(match[1]);
      let units = (match && match[2]) || '';

      if (!isNaN(num)) {
        return { num, units };
      }
    }
  }
  return null;
}

export default Mixin.create(CspStyleMixin, HasTableParent, {
  classNameBindings: ['_resolvedWidthIsAuto:auto-width'],
  styleBindings: ['_resolvedWidth:width'],

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
  _resolvedWidth: computed('columnWidth', 'column.width', function() {
    let w = _parseNumberAndUnits(this.get('columnWidth')) ||
      _parseNumberAndUnits(this.get('column.width')) ||
      _parseNumberAndUnits(DEFAULT_WIDTH);
    return w.auto ? 'auto' : `${w.num}${w.units || 'px'}`;
  }),

  // Resolves to `true` when `resolvedWidth` is 'auto'.
  _resolvedWidthIsAuto: computed.equal('_resolvedWidth', 'auto')
});
