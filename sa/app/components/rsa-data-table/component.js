import Ember from 'ember';
import DomWatcher from 'component-lib/mixins/dom/watcher';

const {
  computed,
  isEmpty,
  get,
  set,
  Component,
  $,
  Object: EmberObject
} = Ember;

const DEFAULT_COLUMN_WIDTH = 100;

export default Component.extend(DomWatcher, {
  tagName: 'section',
  classNames: 'rsa-data-table',

  /**
   * Used by descendant components to find their data table ancestor.
   * @type {boolean}
   * @private
   */
  isDataTable: true,

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
    return (columnsConfig && columnsConfig.map) ?
      columnsConfig.map((cfg) => {
        if (typeof cfg === 'string') {
          let [ field, title ] = cfg.split(':');
          cfg = { field, title };
        }
        if (typeof cfg === 'object') {
          let column = $.isFunction(cfg.get) ? cfg : EmberObject.create(cfg);
          if (isEmpty(get(column, 'width'))) {
            set(column, 'width', DEFAULT_COLUMN_WIDTH);
          }
          return column;
        } else {
          return null;
        }
      }).compact() : [];
  }).readOnly(),

  actions: {
    /**
     * Callback from child component(s) after user tries to reorder columns via drag-drop.
     * Responsible for invoking the (optional) configurable callback `onReorderColumns`.
     * If `onReorderColumns` is not given, or if it returns truthy, then this action will apply the requested change
     * to the `columns`. Otherwise, if the callback returns falsey, then this method will abort and exit silently.
     * @see ember-sortable addon
     * @public
     */
    reorderColumns(newColumns, draggedColumn) {
      let columns = this.get('columns');
      if (!columns || !columns.length || !this.get('enableReorderColumns')) {
        return;
      }
      let fromIndex = columns.indexOf(draggedColumn);
      let toIndex = newColumns.indexOf(draggedColumn);

      if (fromIndex !== toIndex) {

        let fn = this.get('onReorderColumns');
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
      let fn = this.get('onResizeColumn');
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
      let fn = this.get('onRowClick');
      if ($.isFunction(fn)) {
        fn.apply(this, arguments);
      }
    }
  }
});
