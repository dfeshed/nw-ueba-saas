/**
 * @file Data Table Row mixin
 * Uses the ember-cli-csp-style addon to bind a component's DOM element to a `top` attribute.
 * @public
 */
import Ember from 'ember';
import CspStyleMixin from 'ember-cli-csp-style/mixins/csp-style';
import SizeBindings from './size-bindings';
import DomIsReady from './dom-is-ready';
import HasTableParent from './has-table-parent';

const {
  computed,
  set,
  Mixin,
  $
} = Ember;

export default Mixin.create(HasTableParent, DomIsReady, SizeBindings, CspStyleMixin, {
  classNames: 'rsa-data-table-body-row',
  styleBindings: ['top[px]'],

  /**
   * Data object corresponding to this row.
   * @type {object}
   * @public
   */
  item: null,

  /**
   * The measured row height for the current table display.
   * This quantity is measured by the table from a sample row, and then passed down to every table row so
   * that the rows can use it to position themselves vertically.
   * Note that the table assumes that all rows have the same height.
   * @type {number}
   * @public
   */
  height: 0,

  /**
   * The index of this row relative to the first visibly rendered row.
   * When lazy rendering is disabled, `relativeIndex` should match the absolute index.
   * When lazy rendering is enabled, `relativeIndex` may be less than or equal to the absolute index.
   * @type {number}
   * @public
   */
  relativeIndex: 0,

  /**
   * The index of the first row that is currently visibly rendered in the table's viewport.
   * When lazy rendering is disabled, `relativeIndexOffset` is always zero.
   * When lazy rendering is enabled, `relativeIndexOffset` will be greater than or equal to zero.
   * @type {number}
   * @public
   */
  relativeIndexOffset: 0,

  /**
   * Index of `item`, relative to the table's entire `items` array; NOT relative to the first visible item index.
   * Computed as the sum of `relativeIndex` and `relativeIndexOffset`.
   * @type {number}
   * @private
   */
  index: computed('relativeIndex', 'relativeIndexOffset', function() {
    return this.get('relativeIndex') + this.get('relativeIndexOffset');
  }),

  /**
   * The y-coordinate (in pixels) of this row relative to the table body's root DOM element.
   * @type {number}
   * @private
   */
  top: computed('index', 'height', function() {
    return (this.get('height') * this.get('index')) || 0;
  }),

  /**
   * Configurable action to be invoked when a click event is triggered in the row DOM.
   * @type {function}
   * @public
   */
  clickAction: null,

  /**
   * Delegates handling of click DOM events to the configurable `clickAction` callback.
   * @param {object} e The click DOM event, wrapped in a jQuery object.
   * @public
   */
  click(e) {
    let fn = this.get('clickAction');
    if ($.isFunction(fn)) {
      fn(this.get('item'), this.get('index'), e);
    }
  },

  /**
   * If true, indicates that this row's size is typical of all rows, and therefore can be measured in order
   * to determine the standard row height for all rows in this data table.
   * @type {boolean}
   * @public
   */
  isSizeSample: false,

  // When size bindings are enabled, this callback will be notified of changes in size.
  // Responsible for updating the table's `_rowHeight` property, which is used to layout rows via absolute positioning.
  sizeDidChange() {
    set(this.get('table.body'), '_rowHeight', this.$().outerHeight(true));
  },

  // Enables size bindings only if this is a "size sample" row.
  init() {
    this.set('sizeBindingsEnabled', !!this.get('isSizeSample'));
    this._super(...arguments);
  }
});
