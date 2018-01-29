import Component from 'ember-component';
import set from 'ember-metal/set';
import HasTableParent from 'component-lib/components/rsa-data-table/mixins/has-table-parent';
import DomIsReady from 'component-lib/components/rsa-data-table/mixins/dom-is-ready';
import SizeBindings from 'component-lib/components/rsa-data-table/mixins/size-bindings';
import ScrollBindings from 'component-lib/components/rsa-data-table/mixins/scroll-bindings';
import computed, { alias, empty } from 'ember-computed-decorators';
import layout from './template';

export default Component.extend(HasTableParent, DomIsReady, SizeBindings, ScrollBindings, {
  layout,
  tagName: 'section',
  classNames: 'rsa-data-table-body',

  /**
   * Name of the Ember.Component class to be used for rendering each row.
   * @default 'rsa-data-table/body-row'
   * @type {string}
   * @public
   */
  rowComponentClass: 'rsa-data-table/body-row',

  /**
   * Alias for the `items` data array of the parent `rsa-data-table`.
   * @type {object[]}
   * @public
   */
  @alias('table.items')
  items: null,

  /**
   * Configurable optional number of extra data items that should be render above and below the viewport, despite the
   * fact that they don't fit within the viewport. Buffering improves UX, reducing the likelihood that the user
   * will scroll faster than the UI can render and thereby see gaps in the list.
   * @type number
   * @default 0
   * @public
   */
  buffer: 0,

  /**
   *  Configurable option not to show "No Results" when items are empty
   * @type boolean
   * @default true
   * @public
   */
  showNoResultMessage: true,

  /**
   * Check along-side showNoResultMessage
   * if status is passed in (recommended), _noResultMessage will not overlap with spinner
   * If not, then the usual showNoResultMessage prop is used
   * @param status The status of the data fetch
   * @private
   */
  @computed('status', 'showNoResultMessage')
  shouldShowNoResultMessage(status, showNoResultMessage) {
    if (!status) {                // if status has not been passed in, default showNoResultMessage
      return showNoResultMessage;
    }
    if (!showNoResultMessage) {   // if showNoResultMessage is explicitly passed boolean false
      return false;
    }
    if (status === 'streaming') { // if status has been passed, but is streaming, don't show noResults message
      return false;
    }
    return true;
  },

  /**
   * The message to display when there are no results in the table
   * @param message The optional message to display
   * @returns {*|string} either your passed message or 'No Results'
   * @private
   */
  @computed('noResultsMessage')
  _noResultsMessage(message) {
    return message || this.get('i18n').t('tables.noResults');
  },

  /**
   * If true, indicates that there are no `items`.
   * @type boolean
   * @private
   */
  @empty('items')
  _isItemsEmpty: null,

  /**
   * Height (in pixels) of each data item's DOM.
   * Assumes all data items have the same row height. Therefore just measure one specific row's height.
   * @type number
   * @readonly
   * @private
   */
  _rowHeight: 0,

  /**
   * Height (in pixels) of all the data items added up together.
   * This is the minimum height we need to be scroll vertically in order to see all the items.
   * @type number
   * @readonly
   * @private
   */
  @computed('_rowHeight', 'items.length')
  _minScrollHeight(rowHeight, itemsLength) {
    return rowHeight * itemsLength || 0;
  },

  /**
   * The index of the first visible data item in the viewport.
   * @readonly
   * @type number
   * @private
   */
  @computed('table.lazy', '_rowHeight', 'scrollTop')
  _firstIndex(lazy, rowHeight, scrollTop) {
    if (lazy) {
      return rowHeight ? parseInt(scrollTop / rowHeight, 10) : 0;
    } else {
      return 0;
    }
  },

  /**
   * The index of the first data item within the buffer before the viewport.
   * Assumes the first item in `items` is always rendered in the template and therefore never included here.
   * @readonly
   * @type number
   * @private
   */
  @computed('buffer', '_firstIndex')
  _firstBufferedIndex(buffer, firstIndex) {
    return Math.max(1, firstIndex - buffer);
  },

  /**
   * The index of the last visible data item in the viewport.
   * @readonly
   * @type number
   * @private
   */
  @computed('table.lazy', '_firstIndex', '_rowHeight', 'clientHeight')
  _lastIndex(lazy, firstIndex, rowHeight, clientHeight) {
    if (lazy) {
      return rowHeight ? firstIndex + Math.ceil(clientHeight / rowHeight) : 0;
    } else {
      return this.get('items.length') || 0;
    }
  },

  /**
   * The index of the last data item within the buffer after the viewport.
   * @readonly
   * @type number
   * @private
   */
  @computed('buffer', '_lastIndex', 'items.length')
  _lastBufferedIndex(buffer, lastIndex, itemsLength) {
    return Math.min((itemsLength || 0) - 1, lastIndex + buffer);
  },

  /**
   * The subset of `items` that currently fall within the component's viewport and/or buffer.
   * @type {object[]}
   * @private
   */
  @computed('items.[]', '_firstBufferedIndex', '_lastBufferedIndex')
  _visibleItems(items, first, last) {
    const len = items && items.length;

    if (!len) {
      return [];
    }

    return items.slice(first, last + 1);
  },

  /**
   * Stores a reference to this component in the `body` attribute of the parent `rsa-data-table` component.
   * This allows other components within the `rsa-data-table` hierarchy to access this component's properties.
   * For example, the `rsa-data-table/header` needs to access this component's `scrollLeft` so that it can sync its
   * DOM element's scrollLeft with it.
   * @private
   */
  init() {
    this._super(...arguments);
    set(this.get('table'), 'body', this);

    // We need to watch this component's scroll, so that the table's header can stay in sync with it.
    // @see mixins/scroll-bindings
    this.set('scrollBindingsEnabled', true);

    // We only need to watch the component's size if lazy rendering is enabled.
    this.set('sizeBindingsEnabled', this.get('table.lazy'));
  }
})
;
