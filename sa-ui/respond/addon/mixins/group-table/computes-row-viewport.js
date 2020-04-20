import Mixin from '@ember/object/mixin';
import ComputesRowExtents from './computes-row-extents';
import { alias } from '@ember/object/computed';
import { debounce } from '@ember/runloop';
import { get, set, computed } from '@ember/object';

/**
 * @class ComputesRowViewport Mixin
 * @extends ComputesRowExtents Mixin
 * Computes the indices of the first and last groups & group items that fit within the current scroll viewport.
 *
 * This Mixin uses the attrs from ComputesRowExtents Mixin in conjunction with the attrs `scrollerPos.top` &
 * `scrollerSize.innerHeight`. If those 2 attrs are set, then this Mixin will compute which rows fit within the viewport area
 * defined by those 2 attrs. The indices (group & item) of those rows are written to the
 * attrs `firstGroupIndex`, `firstItemIndex`, `lastGroupIndex` & `lastItemIndex`.
 *
 * @public
 */
export default Mixin.create(ComputesRowExtents, {
  /**
   * The index of the first group which lies within the buffered viewport.
   * @type {Number}
   * @public
   */
  firstGroupIndex: alias('viewportIndices.firstGroupIndex'),

  /**
   * The index of the first group item which lies within the buffered viewport.
   * @type {Number}
   * @public
   */
  firstGroupItemIndex: alias('viewportIndices.firstGroupItemIndex'),

  /**
   * The index of the last group which lies within the buffered viewport.
   * @type {Number}
   * @public
   */
  lastGroupIndex: alias('viewportIndices.lastGroupIndex'),

  /**
   * The index of the last group item which lies within the buffered viewport.
   * @type {Number}
   * @public
   */
  lastGroupItemIndex: alias('viewportIndices.lastGroupItemIndex'),

  /**
   * Debounce interval (in millisec) for recalculating `viewport` once DOM change is detected.
   * @see Ember.run.debounce
   * @type {Number}
   * @public
   */
  viewportThrottle: 0,

  /**
   * Configurable number of pixels outside of the current scroll viewport which should be rendered.
   * Setting `viewportBuffer` to a higher number increases the number of rows above & below the viewport which will be
   * rendered. This decreases performance but reduces the chance of the user seeing a gap in rendered rows as the
   * user scrolls, which improves perceived performance.
   * @type {Number}
   * @public
   */
  viewportBuffer: 200,

  /**
   * Configurable y-coordinate (in pixels) of the top of the current scrollable viewport.
   * @type {Number}
   * @public
   */
  scrollerPos: computed({
    get() {
      return this._scrollerPos || {};
    },
    set(key, value) {
      this._scrollerPos = value || {};
      this.viewportDomDidChange();
      return value;
    }
  }),

  /**
   * Configurable height (in pixels) of the current scrollable viewport.
   * @type {Number}
   * @public
   */
  scrollerSize: computed({
    get() {
      return this._scrollerSize || {};
    },
    set(key, value) {
      this._scrollerSize = value || {};
      this.viewportDomDidChange();
      return value;
    }
  }),

  /**
   * The group currently positioned at the top of the (non-buffered) viewport.
   * This is the group which should be displayed in a sticky header at the top of the viewport.
   * @type {{ index: Number, group: Object, isLeaving: Boolean }}
   * @public
   */
  groupAtTop: computed('scrollerPos.top', 'groups', 'groupExtents', 'groupHeaderSize.outerHeight', function() {
    // Compute the index of the first group that intersects the non-buffered viewport.
    // TODO optimization: use a binary search instead of a linear `find`
    const firstGroup = this.groupExtents.find((extent) => extent.bottom >= this.scrollerPos?.top);
    const index = firstGroup ? firstGroup.index : 0;
    const group = (this.groups || [])[index];
    const isLeaving = firstGroup ? ((firstGroup.bottom - this.scrollerPos?.top) <= this.groupHeaderSize?.outerHeight) : false;
    return {
      index,
      group,
      isLeaving
    };
  }),

  /**
   * Triggers a recalculation of the viewport` after applying throttle.
   * Typically invoked by setters for `scrollTop` and `scrollboxInnerHeight`.
   * @private
   */
  viewportDomDidChange() {
    const throttle = this.get('viewportThrottle');
    if (throttle) {
      debounce(this, 'notifyPropertyChange', 'viewportDom', throttle);
    } else {
      this.notifyPropertyChange('viewportDom');
    }
  },

  /**
   * Computes the viewport indices, i.e. `firstGroupIndex`, `firstGroupItemIndex`, `lastGroupIndex` & `lastGroupItemIndex`.
   * These attr values correspond to the first & last groups & group items that fit within the viewport.
   * This computed property uses `viewportDom` as a dependency key, rather than `scrollerPos` &  `scrollerSize`, in order to support
   * debouncing; that is, in order to NOT recompute viewport every single time a scroll or resize event is triggered.
   * @type {{firstGroupIndex: number, firstGroupItemIndex: number, lastGroupIndex: number, lastGroupItemIndex: number}}
   * @private
   */
  viewportIndices: computed('viewportDom', 'viewportBuffer', 'groupExtents', 'groupHeaderSize', 'groupItemSize', function() {
    const grpHeaderSize = this.groupHeaderSize || {};
    const groupHeaderHeight = (grpHeaderSize.outerHeight) ? grpHeaderSize.outerHeight : 0;
    const grpItemSize = this.groupItemSize || {};
    const groupItemHeight = (grpItemSize.outerHeight) ? grpItemSize.outerHeight : 0;
    const { scrollerPos, scrollerSize } = this.getProperties('scrollerPos', 'scrollerSize');
    const { top } = scrollerPos || {};
    const scrollTop = (top) ? top : 0;
    const { innerHeight } = scrollerSize || {};
    const scrollHeight = (innerHeight) ? innerHeight : 0;
    const actual = {
      top: scrollTop,
      bottom: scrollTop + scrollHeight
    };

    const buffered = {
      top: actual.top - this.viewportBuffer,
      bottom: actual.bottom + this.viewportBuffer
    };

    // Compute the index of the first group that fits within the viewport.
    // TODO optimization: use a binary search instead of a linear `find`
    let firstGroup = this.groupExtents.find((extent) => extent.bottom >= buffered.top);
    firstGroup = firstGroup || this.groupExtents[0];
    const firstGroupIndex = firstGroup ? firstGroup.index : 0;

    // Compute the index of the first group item that fits within the viewport.
    let firstGroupItemIndex = 0;
    if (firstGroup && groupItemHeight) {
      const yDiff = Math.max(0, buffered.top - (firstGroup.top + groupHeaderHeight));
      firstGroupItemIndex = Math.floor(yDiff / groupItemHeight);
    }

    // Compute the index of the last group that fits within the viewport.
    const firstGroupBelow = this.groupExtents.slice(firstGroupIndex).find((extent) => extent.top > buffered.bottom);
    let lastGroupIndex = firstGroupBelow ? (firstGroupBelow.index - 1) : (this.groupExtents.length - 1);
    lastGroupIndex = Math.max(firstGroupIndex, lastGroupIndex);
    const lastGroup = this.groupExtents[lastGroupIndex];

    // Compute the index of the last group item that fits within the viewport.
    let lastGroupItemIndex = -1;
    if (lastGroup && groupItemHeight) {
      const yDiff = Math.max(0, buffered.bottom - (lastGroup.top + groupHeaderHeight));
      lastGroupItemIndex = Math.ceil(yDiff / groupItemHeight);
    }

    const initializedExtents = this.groupExtents.filter((extent) => extent.top === 0 && extent.bottom === -1);
    const defaultExtents = get(initializedExtents, 'length') === get(this.groupExtents, 'length');
    if (defaultExtents && this.groupItemSize === null && lastGroupItemIndex === -1) {
      lastGroupIndex = 0;
    }

    return {
      firstGroupIndex,
      firstGroupItemIndex,
      lastGroupIndex,
      lastGroupItemIndex
    };
  }),

  /**
   * Maps each member of `groups` to a wrapper object where we can store additional UI-related info about that group.
   * Caching these wrapper objects, rather than reconstructing them when needed, enables us to bind to these objects
   * from templates and avoid destroying & re-creating their corresponding components in DOM.
   * @type {{ group: Object, index: Number }[]}
   * @private
   */
  groupRefs: computed('groups.[]', function() {
    const groups = this.groups || [];
    const len = groups.length;
    return (groups || []).map((group, index) => ({ group, index, isLast: index === len - 1 }));
  }),

  /**
   * Returns the same array as `groupRefs` but ensures each array member has its `extent` set to the corresponding
   * member of `groupExtents`.
   * @type {{ group: Object, index: Number, extent: { top: Number, bottom: Number }}[]}
   * @private
   */
  groupRefsWithExtents: computed('groupRefs', 'groupExtents', function() {
    this.groupRefs.forEach((ref, index) => {
      set(ref, 'extent', this.groupExtents[index]);
    });
    return this.groupRefs;
  }),

  /**
   * Subset of `groupRefsWithExtents` which intersect the current scroll viewport.
   * @type {{ group: Object, index: Number, extent: { top: Number, bottom: Number }}[]}
   * @public
   */
  groupRefsInViewport: computed('groupRefsWithExtents', 'firstGroupIndex', 'lastGroupIndex', 'groupExtents', function() {
    return this.groupRefsWithExtents.slice(this.firstGroupIndex, this.lastGroupIndex + 1);
  })
});
