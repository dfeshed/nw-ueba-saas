import Component from '@ember/component';
import { run } from '@ember/runloop';
import computed from 'ember-computed-decorators';
import layout from './template';

/* global addResizeListener */
/* global removeResizeListener */

/**
 * @class Lazy List component
 * A List that lazily renders items as user scrolls. Doesn't "un-render" items unless items array is reset.
 *
 * The lazy rendering mechanism employed by this list is relatively un-restrictive, meaning that it does not require
 * much apriori information about the size of the list items. Indeed, the list items can have heterogeneous heights.
 * The downside, however, is that the scrolling experience will be jumpy when items are incrementally rendered to DOM.
 * Typically, the user will scroll and approach the bottom of the scroll range, at which point, new items will
 * be appended and the scroll thumb will appear to jump backwards.  This component is intended to support such
 * scenarios in which list item sizes are heterogeneous and "jumpy" scrolling is acceptable.  If item sizes are
 * uniform and smooth scrolling is desired, use `rsa-data-table` instead.
 *
 * @assumes javascript detect-element-resize library has imported globals addResizeListener & removeResizeListener
 * @see https://github.com/sdecima/javascript-detect-element-resize
 *
 * @public
 */
export default Component.extend({
  tagName: 'section',
  classNames: ['rsa-lazy-list'],
  layout,
  itemComponentClass: 'rsa-list/item',
  selections: null, // passed down to rsa-list child; @see rsa-list#selections

  /**
   * Configurable number of items to render in a single batch.
   *
   * The rendering of a batch can be triggered by scroll and resize events.  When such events are heard, the component
   * will measure if the scroll position is approaching a placeholder <div> at the bottom of the list.  If so, a
   * single additional batch is rendered.
   *
   * @type {number}
   * @public
   */
  batch: 10,

  /**
   * Same as `batch` but used only for the first batch of the `items`.
   * This is typically used to initialize the display with larger batch than usual.
   *
   * @type {number}
   * @public
   */
  firstBatch: 20,

  /**
   * Configurable amount of vertical proximity (in pixels) to the bottom of the scroll area within which the user must
   * scroll in order to trigger the render of the next batch of items.
   *
   * Enables us to support "rendering ahead", thus reducing the appearance of gaps below the last rendered item when
   * scrolling beyond it.
   *
   * @type {number}
   * @public
   */
  buffer: 100,

  // The index of the first un-rendered list item.
  playhead: 0,

  // The subset of `items` which should be rendered in DOM; namely, the items before `playhead`.
  @computed('items.[]', 'playhead')
  renderedItems(items, playhead) {
    return items.slice(0, playhead);
  },

  @computed('items.length', 'playhead')
  allItemsAreRendered(itemCount, playhead) {
    return itemCount <= playhead;
  },

  // Defines setter for `items`.
  items: {
    get() {
      return this._items || [];
    },
    set(value) {
      const changed = value !== this._items;
      if (changed) {
        this._items = value || [];
        this._itemsDidChange();
      }
      return this._items;
    }
  },

  // Determines if the placeholder <div> is within the scroll viewport (modulo the buffer).
  // If so, advances the `playhead` by `batch`, thus triggering the rendering of more items.
  _checkPlayhead() {
    const [ scroller ] = this._$scroller;
    const { scrollTop, scrollHeight, clientHeight } = scroller;
    const buffer = this.get('buffer');
    if (scrollTop + clientHeight > scrollHeight - buffer) {

      // The placeholder is either visible or within the buffer range.
      const itemCount = this.get('items.length') || 0;
      if (itemCount) {
        const { playhead, batch = 1 } = this.getProperties('playhead', 'batch');
        const newPlayhead = Math.min(itemCount, playhead + batch);
        if (newPlayhead > playhead) {

          // Advance the playhead, which will trigger the render of more items.
          this.set('playhead', newPlayhead);

          // The placeholder might still be visible/near after more items are rendered, so repeat.
          run.next(this, '_checkPlayhead');
        }
      }
    }
  },

  // Responds to a new `items` array reference by resetting the playhead (for lazy rendering only).
  _itemsDidChange() {
    this.set('playhead', this.get('firstBatch'));
  },

  // Attach a resize listener.
  _initSizeBindings() {
    const callback = run.bind(this, this._checkPlayhead);
    this.set('_sizeBindingsCallback', callback);
    addResizeListener(this._$scroller[0], callback);
  },

  // Detach the last resize listener, if any.
  _teardownSizeBindings() {
    if (this.get('_sizeBindingsCallback')) {
      removeResizeListener(this._$scroller[0], this.get('_sizeBindingsCallback'));
      this.set('_sizeBindingsCallback', null);
    }
  },

  // Attach a scroll listener.
  _initScrollBindings() {
    const callback = run.bind(this, this._checkPlayhead);
    this.set('_scrollBindingsCallback', callback);
    this._$scroller.on('scroll', callback);
  },

  // Detach the last scroll listener, if any.
  _teardownScrollBindings() {
    if (this.get('_scrollBindingsCallback')) {
      this._$scroller.off('scroll', this.get('_scrollBindingsCallback'));
      this.set('_scrollBindingsCallback', null);
    }
  },

  // Obtains and caches a reference to the scrollable list DOM node.
  _initScroller() {
    this._$scroller = this.$('.rsa-list');
  },

  // Releases handle to list DOM node.
  _teardownScroller() {
    this._$scroller = null;
  },

  // Wires up dom listeners; manually fires callback to initialize state.
  afterRender() {
    this._super(...arguments);
    this._initScroller();
    this._initScrollBindings();
    this._initSizeBindings();
    this._itemsDidChange();
    this._checkPlayhead();
  },

  didInsertElement() {
    this._super(...arguments);
    run.schedule('afterRender', this, 'afterRender');
  },

  willDestroyElement() {
    this._teardownSizeBindings();
    this._teardownScrollBindings();
    this._teardownScroller();
    this._super(...arguments);
  }
});