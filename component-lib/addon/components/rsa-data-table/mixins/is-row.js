/**
 * @file Data Table Row mixin
 * Uses the ember-cli-csp-style addon to bind a component's DOM element to a `top` attribute.
 * @public
 */
import computed from 'ember-computed-decorators';
import SizeBindings from './size-bindings';
import DomIsReady from './dom-is-ready';
import HasTableParent from './has-table-parent';
import { htmlSafe } from '@ember/string';
import { set } from '@ember/object';
import $ from 'jquery';
import Mixin from '@ember/object/mixin';

export default Mixin.create(HasTableParent, DomIsReady, SizeBindings, {
  classNames: 'rsa-data-table-body-row',
  classNameBindings: ['isSelected'],
  attributeBindings: ['style'],

  @computed('top')
  style(top) {
    return htmlSafe(`top: ${top}px;`);
  },

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
  @computed('relativeIndex', 'relativeIndexOffset')
  index(relativeIndex, relativeIndexOffset) {
    return relativeIndex + relativeIndexOffset;
  },

  /**
   * Resolves to `true` if `index` matches the parent table's `selectedIndex`.
   * @type {boolean}
   * @private
   */
  @computed('index', 'table.selectedIndex')
  isSelected(index, selectedIndex) {
    return index === selectedIndex;
  },

  /**
   * The y-coordinate (in pixels) of this row relative to the table body's root DOM element.
   * @type {number}
   * @private
   */
  @computed('index', 'height')
  top(index, height) {
    return (height * index) || 0;
  },

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
    const fn = this.get('clickAction');
    if ($.isFunction(fn)) {
      fn(this.get('item'), this.get('index'), e, this.get('table'));
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
