import $ from 'jquery';
import EmberObject from 'ember-object';
import computed from 'ember-computed';
import Component from 'ember-component';
import get from 'ember-metal/get';
import { isEmpty } from 'ember-utils';
import on from 'ember-evented/on';
import run from 'ember-runloop';
import set from 'ember-metal/set';
import DomWatcher from 'component-lib/mixins/dom/watcher';
import { EKMixin, keyUp } from 'ember-keyboard';

const DEFAULT_COLUMN_WIDTH = 100;
const DEFAULT_COLUMN_VISIBILITY = true;

export default Component.extend(DomWatcher, EKMixin, {
  tagName: 'section',
  classNames: 'rsa-data-table',
  classNameBindings: ['fitToWidth'],

  /**
   * Used by descendant components to find their data table ancestor.
   * @type {boolean}
   * @private
   */
  isDataTable: true,

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
                    const visibleColumnsLength = allColumns.filterBy('visible', true).length;
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
    return this.get('columns').filterBy('selected', true);
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
   * @description Respond to the user pressing down on the keyboard
   * if nothing is selected, select first record
   * if first record is selected, select last record
   * @public
   */
  selectNext: on(keyUp('ArrowDown'), function(e) {
    const fn = this.get('onRowClick');

    if ($.isFunction(fn)) {
      let selectedItemIndex, selectedItem, scrollTop;

      if (this.get('selectedIndex') === (this.get('items.length') - 1)) {
        selectedItemIndex = 0;
        selectedItem = this.get('items').objectAt(0);
        scrollTop = 0;
      } else {
        selectedItemIndex = this.get('selectedIndex') + 1;
        selectedItem = this.get('items').objectAt(selectedItemIndex);
        scrollTop = $('.rsa-data-table-body').scrollTop() + $('.rsa-data-table-body-row').outerHeight() + 1;
      }

      $('.rsa-data-table-body').animate({ scrollTop }, 250);
      fn(selectedItem, selectedItemIndex, e, this);
    }
  }),

  /**
   * @description Respond to the user pressing up on the keyboard
   * if nothing is selected, select last record
   * if last record is selected, select first record
   * @public
   */
  selectPrevious: on(keyUp('ArrowUp'), function(e) {
    const fn = this.get('onRowClick');

    if ($.isFunction(fn)) {
      let selectedItemIndex, selectedItem, scrollTop;

      if (this.get('selectedIndex') < 1) {
        selectedItemIndex = this.get('items.length') - 1;
        selectedItem = this.get('items').objectAt(selectedItemIndex);
        scrollTop = $('.rsa-data-table-body').outerHeight();
      } else {
        selectedItemIndex = this.get('selectedIndex') - 1;
        selectedItem = this.get('items').objectAt(selectedItemIndex);
        scrollTop = $('.rsa-data-table-body').scrollTop() - ($('.rsa-data-table-body-row').outerHeight() + 1);
      }

      $('.rsa-data-table-body').animate({ scrollTop }, 250);
      fn(selectedItem, selectedItemIndex, e, this);
    }
  }),

  didInsertElement() {
    this._super(...arguments);
    if (this.get('scrollToInitialSelectedIndex')) {
      run.schedule('afterRender', this, this._scrollToInitial);
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
      // don't want to try forever, if this recurses too much, just stop
      if (_callCount < 100) {
        // selectedIndex can change (essentially from not found to found)
        // as data flows in, so pull each time through scrollToInitial
        const selectedIndex = this.get('selectedIndex');
        // First row needed to measure height of
        // items so can calculate how far to scroll
        const $firstRow = this.$('.rsa-data-table-body-row');
        // Check selected index is a valid number before attempting scrollTop.
        // This ensures we don't calculate howFarToScrollTable on a negative index. run later
        // keeps us looping until we find a positive index
        if (selectedIndex >= 0 && $firstRow.length > 0) {
          const heightForAllTableRows = this.$('.rsa-data-table-body-rows').height();
          const howFarToScrollTable = $firstRow.outerHeight() * selectedIndex;
          // data could be flowing in over time, so the number of rows in the table may not
          // immediately be enough to scroll to the selected row.
          // if the height of the container surpasses where the item should be,
          // we can scroll to it and exit recursion. Otherwise let it try again later.
          if (heightForAllTableRows >= howFarToScrollTable) {
            this.$('.rsa-data-table-body').scrollTop(howFarToScrollTable);
            return;
          }
        } else {
          _callCount++;
          // If we are unable to scroll to the item, then try again in 100 millis
          run.later(this, this._scrollToInitial, _callCount, 100);
        }
      }
    };
  }(),

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
      set(column, 'width', width);
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
