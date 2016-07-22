import Ember from 'ember';
import HasTableParent from 'sa/components/rsa-data-table/mixins/has-table-parent';
import DomIsReady from 'sa/components/rsa-data-table/mixins/dom-is-ready';
import SizeBindings from 'sa/components/rsa-data-table/mixins/size-bindings';
import ScrollBindings from 'sa/components/rsa-data-table/mixins/scroll-bindings';

const {
  computed,
  set,
  Component
} = Ember;

export default Component.extend(HasTableParent, DomIsReady, SizeBindings, ScrollBindings, {
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
  items: computed.alias('table.items'),

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
   * If true, indicates that there are no `items`.
   * @type boolean
   * @private
   */
  _isItemsEmpty: computed.empty('items'),

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
  _minScrollHeight: computed('_rowHeight', 'items.length', function() {
    return this.get('_rowHeight') * this.get('items.length') || 0;
  }),

  /**
   * The index of the first visible data item in the viewport.
   * @readonly
   * @type number
   * @private
   */
  _firstIndex: computed('table.lazy', '_rowHeight', 'scrollTop', function() {
    if (this.get('table.lazy')) {
      let rowHeight = this.get('_rowHeight');
      return rowHeight ? parseInt(this.get('scrollTop') / rowHeight, 10) : 0;
    } else {
      return 0;
    }
  }),

  /**
   * The index of the first data item within the buffer before the viewport.
   * Assumes the first item in `items` is always rendered in the template and therefore never included here.
   * @readonly
   * @type number
   * @private
   */
  _firstBufferedIndex: computed('buffer', '_firstIndex', function() {
    return Math.max(1, this.get('_firstIndex') - this.get('buffer'));
  }),

  /**
   * The index of the last visible data item in the viewport.
   * @readonly
   * @type number
   * @private
   */
  _lastIndex: computed('table.lazy', '_firstIndex', '_rowHeight', 'clientHeight', function() {
    if (this.get('table.lazy')) {
      let rowHeight = this.get('_rowHeight');
      return rowHeight ? this.get('_firstIndex') + Math.ceil(this.get('clientHeight') / rowHeight) : 0;
    } else {
      return this.get('items.length') || 0;
    }
  }),

  /**
   * The index of the last data item within the buffer after the viewport.
   * @readonly
   * @type number
   * @private
   */
  _lastBufferedIndex: computed('buffer', '_lastIndex', 'items.length', function() {
    return Math.min((this.get('items.length') || 0) - 1, this.get('_lastIndex') + this.get('buffer'));
  }),

  /**
   * The subset of `items` that currently fall within the component's viewport and/or buffer.
   * @type {object[]}
   * @private
   */
  _visibleItems: computed('items.[]', '_firstBufferedIndex', '_lastBufferedIndex', function() {
    let items = this.get('items');
    let len = items && items.length;

    if (!len) {
      return [];
    }

    let first = this.get('_firstBufferedIndex');
    let last = this.get('_lastBufferedIndex');

    return items.slice(first, last + 1);
  }),

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
});
