/**
 * @file Data Table Row mixin
 * @public
 */
import { schedule } from '@ember/runloop';
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
  classNameBindings: ['isSelected', 'isAtGroupingSize'],
  attributeBindings: ['style'],

  // determines if this row is at the limit of the group size
  // if true, this row will render a group label in didInsertElement
  @computed('index', 'table.groupingSize', 'table.enableGrouping')
  isAtGroupingSize(index, groupingSize, enableGrouping) {
    return enableGrouping && ((index + 1) % groupingSize === 0);
  },

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
   * If enableGrouping is true, will offset for group-label's inclusion
   * @type {number}
   * @private
   */
  @computed('index', 'height', 'table.groupingSize', 'table.enableGrouping')
  top(index, height, groupingSize, enableGrouping) {
    let top = (height * index) || 0;

    if (enableGrouping) {
      const previousLabelsRendered = parseInt(index / groupingSize, 10);

      if (previousLabelsRendered) {
        const groupLabelOffset = height * previousLabelsRendered;
        top = top + groupLabelOffset;
      }
    }

    return top;
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
  },

  // render a group label if table.enableGrouping is true
  didInsertElement() {
    this._super();
    if (this.get('table.enableGrouping')) {
      const index = this.get('index');
      const isAtGroupingSize = this.get('isAtGroupingSize');
      const length = this.get('table.items.length');
      const groupingSize = this.get('table.groupingSize');
      const adjustedIndex = index + 1;
      const hasMoreGroups = isAtGroupingSize && adjustedIndex < length;
      const nextGroupIsFull = adjustedIndex + groupingSize < length;

      if (hasMoreGroups) {
        const startNextGroup = adjustedIndex + 1;
        let endNextGroup = null;

        if (nextGroupIsFull) {
          endNextGroup = adjustedIndex + groupingSize;
        } else {
          endNextGroup = length;
        }

        const label = this.get('i18n').t('investigate.events.tableGroupLabel', {
          startNextGroup,
          endNextGroup
        });

        schedule('afterRender', () => {
          this.$().append(`<div class="group-label"><div>${label}<div></div>`);
        });
      }
    }
  }
});
