/* global addResizeListener */
/* global removeResizeListener */
import $ from 'jquery';
import { assert } from '@ember/debug';
import computed from 'ember-computed';
import Component from '@ember/component';
import EmberObject, { get, set, observer } from '@ember/object';
import { isEmpty } from '@ember/utils';
import { run, once } from '@ember/runloop';
import DomWatcher from 'component-lib/mixins/dom/watcher';

const DEFAULT_COLUMN_WIDTH = 100;
const DEFAULT_COLUMN_VISIBILITY = true;

export default Component.extend(DomWatcher, {
  tagName: 'section',
  classNames: 'rsa-data-table',
  classNameBindings: ['fitToWidth'],
  whitespace: 14,
  groupLabelHeight: 28,


  /**
   * Enables the rendering of a group-label based on groupingSize
   * @type {boolean}
   * @private
   */
  enableGrouping: false,

  /**
   * Configures the size of groupings if enableGrouping is true
   * @type {integer}
   * @private
   */
  groupingSize: 100,

  /**
   * Initial width as set by column config
   * @type {integer}
   * @private
   */
  defaultWidth: 0,

  /**
   * Flag which is set to true in case width has any units
   * other than 'px' since we don't need to adjust the column
   * widths if the units are other than 'px'
   * @type {boolean}
   * @private
   */
  needNotAdjustWidth: false,


  /**
   * Used by descendant components to find their data table ancestor.
   * @type {boolean}
   * @private
   */
  isDataTable: true,

  /**
   * Used for cell width adjustment in case view port width is more than total cell width.
   * @type {Array}
   * @private
   */
  columnWidths: [],

  /**
   * If truthy, indicates that rows should match the width of the table, growing if need be.
   * Otherwise, rows will only grow as wide as needed to accommodate their columns' widths.
   * Typically `fitToWidth` is used when 1 or more column widths are `auto`.
   * @type {boolean}
   * @default false
   * @private
   */
  fitToWidth: false,

  /**
   * If true, indicates that table will lazily render its data rows; that is, it will only render the DOM for
   * those rows which are visible within the table body's viewport (i.e., the scrollable area).
   * @type {boolean}
   * @default false
   * @public
   */
  lazy: false,

  /**
   * If true, indicates that column headers (if any) can be drag-moved. Note that this is only supported when
   * columns are defined imperatively (via `columnsConfig`). If columns are defined declaratively (via a template),
   * then column dragging will be disabled.
   * @type {boolean}
   * @public
   */
  enableReorderColumns: true,

  /**
   * If true, triggers onRowClick with the up and down keyboard keys
   * @type {boolean}
   * @public
   */
  keyboardActivated: true,

  /**
   * Optional configurable callback to be invoked when user attempts to reorder columns.
   * This property allows the component's consumer to implement custom logic that determines whether or not a requested
   * reorder should be applied, and/or how the reorder is applied to the underlying data.
   * If this callback is not defined, then this component will reorder the columns as requested by simply reordering
   * the objects in the `columns` property. Otherwise, if defined, this callback will be invoked first,
   * and this component will only reorder the `columns` array if the callback returns truthy.
   * When this callback is invoked, it will be passed the following input arguments:
   * @param {object[]} oldColumns The `columns` array in its current order.
   * @param {object[]} newColumns  The `columns` array in its newly requested order.
   * @param {object} draggedColumn The `columns` member whose position within `columns` is being moved.
   * @param {number}fromIndex The original index of `draggedColumn` (-1 if not found).
   * @param {number} toIndex The newly requested index of `draggedColumn` (-1 if not found).
   * @returns {boolean}
   * @type {function}
   * @public
   */
  onReorderColumns: null,

  /**
   * If true, indicates that column headers (if any) can be drag-resized.  Note that this is only supported when
   * columns are defined imperatively (via `columnsConfig`). If columns are defined declaratively (via a template),
   * then column resizing will be disabled.
   * @type {boolean}
   * @public
   */
  enableResizeColumn: true,

  /**
   * Optional configurable callback to be invoked when user attempts to resize a column.
   * This property allows the component's consumer to implement custom logic that determines whether or not a requested
   * resize should be applied, and/or how the resize is applied to the underlying data.
   * If this callback is not defined, then this component will resize the column as requested by simply updating
   * the `width` of the corresponding column object in the `columns` property. Otherwise, if defined, this callback
   * will be invoked first, and this component will only update the column object if the callback returns truthy.
   * When this callback is invoked, it will be passed the following input arguments:
   * @param {object[]} column The object from the `columns` array about to be resized.
   * @param {number} width  The newly requested width for the column, in pixels.
   * @returns {boolean}
   * @type {function}
   * @public
   */
  onResizeColumn: null,

  /**
   * Optional configurable callback to be invoked when user clicks on a body row.
   * @param {object} item The item from the `items` data array that corresponds to the clicked row.
   * @param {number} index  The index of `item` relative to the `items` array.
   * @param {object} e The click DOM event, in a jQuery wrapper.
   * @type {function}
   * @public
   */
  onRowClick: null,

  /**
   * The data array. Typically either an array of POJOs or Ember.Objects (or some subclass thereof).
   * Note that, due to lazy rendering, not all `items` may be rendered in the DOM simultaneously.
   * @type []
   * @public
   */
  items: null,

  /**
   * Index of `items` member which is currently selected, if any; -1 otherwise.
   * @type {number}
   * @default -1
   * @public
   */
  selectedIndex: -1,

  /**
   * Whether or not to scroll to the selectedIndex when first rendering the table.
   * @type {boolean}
   * @default false
   * @public
   */
  scrollToInitialSelectedIndex: false,

  /**
   * Optional configuration that specifies which columns are to be displayed.
   * The following formats for `columns` are supported:
   * (1) A comma-delimited list of field names (e.g., `name,created,desc`). Each comma-delimited value should be
   * the name of a property in the `items` array. In this format, if column headers will be displayed (by including a
   * `{{rsa-data-table/header}}` block inside `{{#rsa-data-table}}`), you can include column titles here by
   * following each field name with a colon (`:`) and title (e.g., `name,created:Created Date,desc:Description`).
   * (2) An array of strings. Similar to (1), but the string values are in an array rather than delimited by commas
   * (e.g., `['name', 'created:Created Date', 'desc:Description']`).
   * (3) An array of objects.  Each object can be either a POJO or an Ember.Object. Each object represents a column
   * to be displayed, with the following properties:
   * (i) `field`: The name of the JSON field from which to read the display value. Required.
   * (ii) `title`: Optional string to display in the column title. Only used if `{{rsa-data-table/header}}` is included
   * in the `{{#rsa-data-table}}` block).
   * (iii) `width`: Optional default width for this column. If missing, a default width will be applied.
   * (iv) `componentClass`: Optional name of the Ember component to be used to render this column's values. Defaults
   * to `rsa-data-table/body-cell` if not provided. The component will be assigned 2 attributes at render time: `item`
   * (the data record from `items` to be rendered) and `column` (this column configuration object which corresponds
   * to the column to be rendered).
   * (v) `visible=true`: Optional. If false the column is not selected in the column-selector dialog. Only used if `{{rsa-data-table/header}}` is included
   * in the `{{#rsa-data-table}}` block with `enableColumnSelector=true`).
   * @type {string|string[]|object[]}
   * @public
   */
  columnsConfig: null,

  /**
   * If true, indicates that `columnsConfig` was not provided. Therefore we assume that the columns were defined
   * declaratively within the markup of the `{{#rsa-data-table}}` block, using table cell components.
   * @type {boolean}
   * @public
   */
  columnsDefinedDeclaratively: computed.not('columnsConfig'),

  /**
   * An array of Ember.Objects, each one representing a current column definition.
   * Each Ember.Object represents a column to be displayed, with properties: `field`, `title`, `width` & `componentClass`.
   * @see `columnsConfig` for more details about these properties.
   * @type {object[]}
   * @readonly
   * @public
   */
  columns: computed('columnsConfig', function() {
    let columnsConfig = this.get('columnsConfig');
    if (typeof columnsConfig === 'string') {
      columnsConfig = columnsConfig.split(',');
    }
    // Complete table need to re-render in case column config is changing.
    // Ensuring table starts from left (Scrollbar needs to start from begining).
    this.$('.rsa-data-table-body').scrollLeft(0);
    // Need to reset the flag for new column groups
    this.set('needNotAdjustWidth', false);
    if (!columnsConfig || !columnsConfig.map) {
      return [];
    } else {
      let lastAddedColumnIndex = 0;
      const columns = columnsConfig.map((cfg) => {
        if (typeof cfg === 'string') {
          const [field, title] = cfg.split(':');
          cfg = { field, title };
        }
        if (typeof cfg === 'object') {
          const column = $.isFunction(cfg.get) ? cfg : EmberObject.create(cfg);

          if (isEmpty(get(column, 'width'))) {
            set(column, 'width', DEFAULT_COLUMN_WIDTH);
          }
          set(column, 'defaultWidth', get(column, 'width'));

          if (isEmpty(get(column, 'visible'))) {
            set(column, 'visible', DEFAULT_COLUMN_VISIBILITY);
          }

          set(column, 'displayIndex', lastAddedColumnIndex++);

          column.reopen({
            selected: computed({
              get: () => column.get('visible'),
              set: (key, value) => {
                run.once(() => {
                  const allColumns = this.get('columns');
                  if (value === true) {
                    // Newest selected columns are added at the end of the list table
                    allColumns.removeObject(column);
                    allColumns.addObject(column);
                  } else {
                    // When unselecting columns we make sure that at least one colum remains visible
                    const visibleColumnsLength = allColumns.filterBy('visible', true).filter((c) => {
                      return c.field != 'checkbox';
                    }).length;
                    if (visibleColumnsLength === 1) {
                      column.set('selected', true);
                      return value;
                    }
                  }
                  column.set('visible', value);
                });
                return value;
              }
            })
          });
          return column;
        } else {
          return null;
        }
      });
      return columns.compact();
    }
  }).readOnly(),

  /**
   * @name visibleColumns
   * @description Returns a list of visible and sorted columns
   * @public
   */
  visibleColumns: computed('columns.@each.selected', function() {
    const columnWidths = [];
    const columns = this.get('columns').filterBy('selected', true).sortBy('displayIndex');
    const newCols = columns.map((column) => {
      if (get(column, 'width') != get(column, 'defaultWidth')) {
        set(column, 'width', get(column, 'defaultWidth'));
      }

      const width = get(column, 'width');
      columnWidths.push(width);

      // No need to adjust width in case of any other units except 'px'
      // hence setting the flag for same
      if (isNaN(width) && !width.includes('px')) {
        this.set('needNotAdjustWidth', true);
      }

      return column;
    });

    this.set('columnWidths', columnWidths);
    this._applyColumnWidth(newCols);

    return newCols;
  }),

  /**
   * @description This method is to adjust widths of
   * remaining cells whenever user tries to resize any
   * cell manually.
   * @public
   */
  _adjustWidthDiff(resizeColumn, resizeWidth) {
    const diff = get(resizeColumn, 'width') - resizeWidth;
    const columns = this.get('columns').filterBy('selected', true);
    const resizedColumns = columns.filterBy('resizedOnce', true).length;
    const len = columns.length > resizedColumns ? (columns.length - resizedColumns) : 1;
    const adjust = Math.ceil(diff / len) + 1;

    columns.forEach((column, index) => {
      if (column.displayIndex === resizeColumn.displayIndex) {
        set(column, 'width', resizeWidth);
        set(column, 'resizedOnce', true);
      } else if (!get(column, 'resizedOnce') || (len === 1 && index === (columns.length - 1))) {
        // We need to divide the extra width of the resized column amongst all other columns
        // Adjustment is done to the columns whose width is not resized by the user
        // or if all columns are resized once and further we resize one of them ,
        // then the extra adjustment should add up to the last column
        this._needToAdjust(adjust, column);
      }
    });
  },

  /**
   * @description This method returns true if we need to divide
   * the extra width of the resized column amongst all other
   * columns.That will happen only when:
   *   When scrollwidth is less than total Viewable width
   *   Also if all columns are resized once and further we resize one of them ,
   *   then the extra adjustment should add up to the last column
   * @public
   */
  _needToAdjust(adjust, column) {
    const [ domElement ] = this.$('.rsa-data-table-body-row');
    if (adjust >= 0 && domElement && domElement.scrollWidth <= domElement.clientWidth) {
      const width = adjust + get(column, 'width');
      set(column, 'width', width);
    }
  },

  /**
   * @description This method is to adjust the cell width
   * in case total cell width is less than viewPort width.
   * This adjustment will be done only in following 2 scenarios
   *   Scenario1: User does not provide any width
   *   Scenario2: User provides width in 'px' or without any unit
   * @public
  */
  _applyColumnWidth(columns) {
    // No need to adjust width in case of any other units except 'px'
    if (this.get('needNotAdjustWidth')) {
      return;
    }
    const w = this.get('element.clientWidth') || 0;
    this.set('currentClientWidth', w);
    const columnWidth = this.get('columnWidths');
    const sum = columnWidth.reduce((a, b) => a + b, 0);
    const hasCheckbox = columns.any((c) => c.dataType === 'checkbox');
    const noOfColumns = hasCheckbox ? columns.length - 1 : columns.length;
    const resizedColumns = columns.filterBy('resizedOnce', true).length;
    // Get view port width.
    const rowWidth = this.$().width();
    const diff = rowWidth - sum;
    // Need to adjust width only if view port is more than total cell width.
    if (diff > 0) {
      // Need to adjust only difference from view port.
      const len = noOfColumns > resizedColumns ? (noOfColumns - resizedColumns) : 1;
      columns.forEach((column, index) => {
        if (column.dataType === 'checkbox') {
          return;
        }
        const adjustWidth = Math.ceil(diff / len - this.whitespace) + 1;
        // Every time cell width will be addition of original width + adjustWidth.
        if (!get(column, 'resizedOnce')) {
          const width = adjustWidth + columnWidth[index];
          set(column, 'width', width);
        } else if (noOfColumns === resizedColumns && index == (noOfColumns - 1)) {
          // if all columns are resized once and further we resize one of them ,
          // then the extra adjustment should add up to the last column
          const width = adjustWidth + get(column, 'width');
          set(column, 'width', width);
        }
      });
    }
  },

  /**
   * @description Returns the sum height of all rendered grouping labels.
   * @public
   */
  prevGroupingLabelsHeight: computed('selectedIndex', 'groupingSize', 'groupLabelHeight', 'enableGrouping', function() {
    const {
      selectedIndex,
      groupingSize,
      groupLabelHeight,
      enableGrouping
    } = this.getProperties('selectedIndex', 'groupingSize', 'groupLabelHeight', 'enableGrouping');

    if (enableGrouping && (selectedIndex <= groupingSize)) {
      return 0;
    } else {
      return ((selectedIndex / groupingSize) - 1) * groupLabelHeight;
    }
  }),

  /**
   * @description Returns a list of all columns sorted by its default order regardless if the user moved columns,
   * nor changed selected columns
   * @public
   */
  sortedColumns: computed('columns.@each.displayIndex', function() {
    return this.get('columns').sortBy('displayIndex');
  }),

  /**
   * @description Calls _scrollToInitial when new data is loaded into the table.
   * The intent is to keep the selectedRow in view if new records push it out of view.
   * @private
   */
  _scrollTopWillChange: observer('items.length', function() {
    once(this, () => {
      this._scrollToInitial();
    });
  }),

  /**
   * @description Respond to the user pressing down on the keyboard
   * if a dropdown is not in view, proceed with the following
   * if nothing is selected, select first record
   * if first record is selected, select last record
   * @public
   */
  selectNext(e) {
    const { nodeName, classList } = e.target;

    if (nodeName === 'BODY' || classList.contains('rsa-data-table')) {
      const fn = this.get('onRowClick');

      if ($.isFunction(fn)) {
        let selectedItemIndex, selectedItem, scrollTop;
        const items = this.get('items');

        if (this.get('selectedIndex') === (items.get('length') - 1)) {
          selectedItemIndex = 0;
          selectedItem = items.objectAt(0);
          scrollTop = 0;
        } else {
          selectedItemIndex = this.get('selectedIndex') + 1;
          selectedItem = items.objectAt(selectedItemIndex);
          scrollTop = (selectedItemIndex * $('.rsa-data-table-body-row').outerHeight()) + this.get('prevGroupingLabelsHeight');
        }

        $('.rsa-data-table-body').animate({ scrollTop }, 0);
        fn(selectedItem, selectedItemIndex, e, this);
      }
    }
  },

  /**
   * @description Respond to the user pressing up on the keyboard
   * if a dropdown is not in view, proceed with the following
   * if nothing is selected, select last record
   * if last record is selected, select first record
   * @public
   */
  selectPrevious(e) {
    const { nodeName, classList } = e.target;

    if (nodeName === 'BODY' || classList.contains('rsa-data-table')) {
      const fn = this.get('onRowClick');

      if ($.isFunction(fn)) {
        let selectedItemIndex, selectedItem, scrollTop;

        if (this.get('selectedIndex') < 1) {
          selectedItemIndex = this.get('items.length') - 1;
          selectedItem = this.get('items').objectAt(selectedItemIndex);
          // when vertical scroll is present, it will scroll to bottom,
          // which was not happening before.
          scrollTop = $('.rsa-data-table-body')[0].scrollHeight;
        } else {
          selectedItemIndex = this.get('selectedIndex') - 1;
          selectedItem = this.get('items').objectAt(selectedItemIndex);
          scrollTop = (selectedItemIndex * $('.rsa-data-table-body-row').outerHeight()) + this.get('prevGroupingLabelsHeight');
        }

        $('.rsa-data-table-body').animate({ scrollTop }, 0);
        fn(selectedItem, selectedItemIndex, e, this);
      }
    }
  },

  init() {
    const unsupportedGrouping = this.get('enableGrouping') && !this.get('lazy');
    if (unsupportedGrouping) {
      assert('Grouping is not supported for non-lazy tables and has been disabled.');
      this.set('enableGrouping', false);
    }
    this._super(...arguments);
  },

  didInsertElement() {
    this._super(...arguments);
    // Need to recalculate column widths on change of data-table's viewport width
    this._resizeListener = this.elementDidResize.bind(this);
    addResizeListener(this.element, this._resizeListener);
    if (this.get('scrollToInitialSelectedIndex')) {
      run.schedule('afterRender', this, this._scrollToInitial);
    }

    const _boundKeyUpListener = this._onKeyUp.bind(this);
    this.set('_boundKeyUpListener', _boundKeyUpListener);
    window.addEventListener('keyup', _boundKeyUpListener);
  },

  willDestroyElement() {
    this._super(...arguments);
    if (this._resizeListener) {
      removeResizeListener(this.element, this._resizeListener);
      this._resizeListener = null;
    }
    window.removeEventListener('keyup', this.get('_boundKeyUpListener'));
  },

  _onKeyUp(e) {
    // do not do anything if keyboard not activated,
    // can be turned off by consuming component
    if (this.get('keyboardActivated')) {
      if (e.keyCode === 38) {
        // up arrow
        this.selectPrevious(e);
      } else if (e.keyCode === 40) {
        // down arrow
        this.selectNext(e);
      }
    }
  },

  /**
   * Scroll to the selected event so that the event is visible on the
   * screen with a highlighted background.
   * @private
   */
  _scrollToInitial: function() {
    let _callCount = 0;
    return function() {
      // Don't want to try forever, if this recurses too much, just stop
      if (_callCount < this.get('items.length')) {
        // selectedIndex can change (essentially from not found to found)
        // as data flows in, so pull each time through scrollToInitial
        const selectedIndex = this.get('selectedIndex');

        // First row needed to measure height of items so can calculate how far
        // to scroll
        const $firstRow = this.$('.rsa-data-table-body-row:first-child');
        // Check selected index is a valid number before attempting scrollTop.
        // This ensures we don't calculate howFarToScrollTable on a negative index.
        if (selectedIndex >= 0 && !!$firstRow) {
          const heightForAllTableRows = this.$('.rsa-data-table-body-rows').height();
          let howFarToScrollTable = $firstRow.outerHeight() * selectedIndex;
          // Data could be flowing in over time, so the number of rows in the
          // table may not immediately be enough to scroll to the selected row.
          // If the height of the container surpasses where the item should be,
          // we can scroll to it and exit recursion. Otherwise let it try again later.

          if (this.get('enableGrouping') && (selectedIndex > this.get('groupingSize'))) {
            howFarToScrollTable = howFarToScrollTable + this.get('prevGroupingLabelsHeight');
          }

          if (heightForAllTableRows >= howFarToScrollTable) {
            this.$('.rsa-data-table-body').scrollTop(howFarToScrollTable);
            return;
          }
        }
        _callCount++;
        // If we are unable to scroll to the item, then try again in 100 millis
        run.later(this, this._scrollToInitial, _callCount, 100);
      }
    };
  }(),

  /**
   * @description This method is to re-calculate the cell width
   * when the width of data-table's viewport changes
   * @public
   */
  elementDidResize() {
    run.later(() => {
      if (this.get('isDestroying') || this.get('isDestroyed') || !this.element) {
        // The element has been destroyed since the time when the delay started
        return;
      }
      const w = this.get('element.clientWidth') || 0;
      if (!this.get('currentClientWidth') || w !== this.get('currentClientWidth')) {
        const columns = this.get('columns').filterBy('selected', true).sortBy('displayIndex');
        this._applyColumnWidth(columns);
      }
    }, 250);
  },

  actions: {
    /**
     * Callback from child component(s) after user tries to reorder columns via drag-drop.
     * Responsible for invoking the (optional) configurable callback `onReorderColumns`.
     * If `onReorderColumns` is not given, or if it returns truthy, then this action will apply the requested change
     * to the `visibleColumns`. Otherwise, if the callback returns falsey, then this method will abort and exit silently.
     * @see ember-sortable addon
     * @public
     */
    reorderColumns(newColumns, draggedColumn) {
      const columns = this.get('visibleColumns');
      if (!columns || !columns.length || !this.get('enableReorderColumns')) {
        return;
      }
      const fromIndex = columns.indexOf(draggedColumn);
      const toIndex = newColumns.indexOf(draggedColumn);

      if (fromIndex !== toIndex) {

        const fn = this.get('onReorderColumns');
        if ($.isFunction(fn)) {
          if (!fn.apply(this, [columns, newColumns, draggedColumn, fromIndex, toIndex])) {
            return;
          }
        }
        columns.removeObject(draggedColumn);
        columns.insertAt(toIndex, draggedColumn);
      }
    },

    /**
     * Callback from child component(s) after user tries to resize column via drag-drop.
     * Responsible for invoking the (optional) configurable callback `onResizeColumn`.
     * If `onResizeColumn` is not given, or if it returns truthy, then this action will apply the requested change
     * to the given column object. Otherwise, if the callback returns falsey, then this method will abort and exit silently.
     * @public
     */
    resizeColumn(column, width) {
      if (!column || !this.get('enableResizeColumn')) {
        return;
      }
      const fn = this.get('onResizeColumn');
      if ($.isFunction(fn)) {
        if (!fn.apply(this, [column, width])) {
          return;
        }
      }
      if (this.get('needNotAdjustWidth')) {
        set(column, 'width', width);
        return;
      }
      this._adjustWidthDiff(column, width);
    },

    /**
     * Callback from child component(s) after user clicks within a row component's DOM.
     * Responsible for invoking the (optional) configurable callback `onRowClick`, with the same arguments that were
     * passed into this action.
     * @public
     */
    rowClick(/* item, index, e */) {
      const fn = this.get('onRowClick');
      if ($.isFunction(fn)) {
        fn.apply(this, arguments);
      }
    }
  }
});
