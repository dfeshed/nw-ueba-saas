import Ember from 'ember';
import layout from '../templates/components/rsa-carousel';
import DomWatcher from '../mixins/dom/watcher';

const {
  Component,
  computed
} = Ember;

export default Component.extend(DomWatcher, {
  layout,
  tagName: 'section',
  classNames: 'rsa-carousel',
  classNameBindings: ['allowMultipleRows'],

  /**
   * The properties of this component's `element` to be watched.
   * @see sa/mixins/dom/watcher
   * @private
   */
  watchBindings: ['clientWidth'],

  /**
   * Array of data to be displayed.
   * @type {object[]}
   * @public
   */
  items: null,

  /**
   * If true, indicates that carousel items are allowed to wrap onto additional rows, thereby fitting
   * on a single "page".  This is not common, but can be used to display rows of items in a carousel-like
   * layout without the navigation UI of the carousel.
   * @type {boolean}
   * @default false
   * @public
   */
  allowMultipleRows: false,

  /**
   * The index (0-based) of the page that is currently visible.
   * Returns -1 if no page currently displayed.
   * Should never be greater than the total # of pages available minus 1.
   * @type {number}
   * @public
   */
  currentPageIndex: computed('pages.length', {
    get(/* key */) {
      return Math.min(this._currentPageIndex || 0, this.get('pages.length') - 1);
    },
    set(key, value) {
      this._currentPageIndex = Math.min(value || 0, this.get('pages.length') - 1);
      return this._currentPageIndex;
    }
  }),

  /**
   * Width (in pixels) of an arrow's DOM.
   * Assumes both arrows have the same size, and that the size is independent of the carousel's size.
   * Assumes that the size can be measured by measuring a certain DOM node with a special CSS class.
   * @type {number}
   * @readonly
   * @private
   */
  _arrowWidth: computed('domIsReady', function() {
    return this.get('domIsReady') ? this.$('.js-carousel__arrow').outerWidth() : 0;
  }),

  /**
   * Maximum available width (in pixels) for the viewport's DOM.
   * Assumes that the max available width is the inner width of the component, minus the width of its arrows' DOM.
   * Assumes that both arrows have the same width size.
   * @type {number}
   * @readonly
   * @private
   */
  _viewportWidthLimit: computed('clientWidth', '_arrowWidth', function() {
    return (this.get('clientWidth') - 2 * this.get('_arrowWidth')) || 0;
  }),

  /**
   * If true, indicates that `items` is empty.
   * @type {boolean}
   * @private
   */
  _isItemsEmpty: computed.empty('items'),

  /**
   * Width (in pixels) of each item's DOM.
   * Assumes all data items have the same size, and that the size is independent of the carousel's size. Also assumes
   * that the size can be measured by measuring an inner node with a special CSS class.
   * @type {number}
   * @readonly
   * @private
   */
  _itemWidth: computed('domIsReady', '_isItemsEmpty', function() {
    return (this.get('domIsReady') && !this.get('_isItemsEmpty') && this.$('.js-carousel__measure-item').innerWidth()) || 0;
  }),

  /**
   * Height (in pixels) of each item's DOM.
   * Assumes all data items have the same size, and that the size is independent of the carousel's size. Also assumes
   * that the size can be measured by measuring an inner node with a special CSS class.
   * @type {number}
   * @readonly
   * @private
   */
  _itemHeight: computed('domIsReady', '_isItemsEmpty', function() {
    return (this.get('domIsReady') && !this.get('_isItemsEmpty') && this.$('.js-carousel__measure-item').innerHeight()) || 0;
  }),

  /**
   * Maximum number of columns of (whole) items that can fit in the current viewport size without being truncated.
   * @type {number}
   * @readonly
   * @private
   */
  _columnsPerPage: computed('_itemWidth', '_viewportWidthLimit', function() {
    let _itemWidth = this.get('_itemWidth');
    return _itemWidth ? Math.floor(this.get('_viewportWidthLimit') / _itemWidth) : 0;
  }),

  /**
   * An array of page objects, each of which has the following properties:
   * `firstItemIndex`: the first item index that would fit on that corresponding page;
   * `lastItemIndex`: the last item index that would fit on that corresponding page.
   * Note that if `allowMultipleRows` is true, then all items will fit on the first page.
   * @type {object[]}
   * @readonly
   * @public
   */
  pages: computed('_columnsPerPage', 'items.length', function() {
    let { items, allowMultipleRows, _columnsPerPage } = this.getProperties('items', 'allowMultipleRows', '_columnsPerPage');
    let len = (items && items.length) || 0;
    let pages = [];

    if (len) {
      if (allowMultipleRows) {
        pages.pushObject({ firstItemIndex: 0, lastItemIndex: len - 1 });
      } else {
        let pageCount = (len && _columnsPerPage) ? Math.ceil(len / _columnsPerPage) : 0;
        let i;

        for (i = 0; i < pageCount; i++) {
          let firstItemIndex = _columnsPerPage * i;
          let lastItemIndex = Math.min(firstItemIndex + _columnsPerPage - 1, len - 1);

          pages.pushObject({
            firstItemIndex,
            lastItemIndex
          });
        }
      }
    }
    return pages;
  }),

  /**
   * If true, indicates that there are at least two pages worth of items in the current view.
   * @type {boolean}
   * @readonly
   * @public
   */
  hasMultiplePages: computed('pages.length', function() {
    return this.get('pages.length') > 1;
  }),

  /**
   * An object from `pages` corresponding to the currently visible page.
   * @type {object}
   * @readonly
   * @public
   */
  currentPage: computed('currentPageIndex', 'pages', function() {
    return this.get('pages')[this.get('currentPageIndex')];
  }),

  /**
   * The subset of the component's `items` array that would fit on the currently visible page.
   * The resultant array will be equipped with an 'index' property, which will be assigned the index of the first item.
   * Additionally, placeholder "items" will be appended to this array (if needed) so that they will be invisibly
   * rendered in the DOM in order to ensure the proper alignment of the currently visible items.  Placeholder items
   * are only needed when the # of currently visible items is less than the number of items that fit in one page.
   * @type {object[]}
   * @public
   */
  currentPageItems: computed('currentPage', 'items.[]', '_columnsPerPage', function() {
    let { currentPage, items, _columnsPerPage } = this.getProperties('currentPage', 'items', '_columnsPerPage');
    let pageItems = (items && currentPage) ? items.slice(currentPage.firstItemIndex, currentPage.lastItemIndex + 1) : [];
    let len = pageItems.length;

    // Add placeholder items to fill in empty spots in last row (if any).
    let diff = (_columnsPerPage && len % _columnsPerPage) ?
      (_columnsPerPage - (pageItems.length % _columnsPerPage)) : 0;
    if (diff > 0) {
      let width = this.get('_itemWidth');
      let height = this.get('_itemHeight');
      let i;

      for (i = 0; i < diff; i++) {
        pageItems.push({ isPlaceholder: true, width, height });
      }
    }

    // Set the resultant array's 'index' to its starting item's index.
    pageItems.set('index', currentPage && currentPage.firstItemIndex);

    return pageItems;
  }),

  actions: {
    /**
     * Attempts to navigate to the previous page before the current page.
     * If there is no previous page, then attempts to "wrap around" to the last page, if any.
     * @public
     */
    previousPage() {
      let curr = this.get('currentPageIndex');
      if (curr) {
        this.decrementProperty('currentPageIndex');
      } else {
        let len = this.get('pages.length');
        if (len) {
          this.set('currentPageIndex', len - 1);
        }
      }
    },

    /**
     * Attempts to navigate to the next page after the current page.
     * If there is no next page, then attempts to "wrap around" to the first page, if any.
     * @public
     */
    nextPage() {
      let curr = this.get('currentPageIndex');
      let len = this.get('pages.length');

      if (len) {
        this.set('currentPageIndex', (curr < len - 1) ? (curr + 1) : 0);
      }
    }
  }
});
