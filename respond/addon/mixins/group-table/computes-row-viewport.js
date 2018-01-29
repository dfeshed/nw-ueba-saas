import Mixin from 'ember-metal/mixin';
import ComputesRowExtents from './computes-row-extents';
import computed, { alias } from 'ember-computed-decorators';
import { debounce } from 'ember-runloop';
import set from 'ember-metal/set';

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
  @alias('viewportIndices.firstGroupIndex')
  firstGroupIndex: 0,

  /**
   * The index of the first group item which lies within the buffered viewport.
   * @type {Number}
   * @public
   */
  @alias('viewportIndices.firstGroupItemIndex')
  firstGroupItemIndex: 0,

  /**
   * The index of the last group which lies within the buffered viewport.
   * @type {Number}
   * @public
   */
  @alias('viewportIndices.lastGroupIndex')
  lastGroupIndex: 0,

  /**
   * The index of the last group item which lies within the buffered viewport.
   * @type {Number}
   * @public
   */
  @alias('viewportIndices.lastGroupItemIndex')
  lastGroupItemIndex: 0,

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
  @computed
  scrollerPos: {
    get() {
      return this._scrollerPos || {};
    },
    set(value) {
      this._scrollerPos = value || {};
      this.viewportDomDidChange();
      return value;
    }
  },

  /**
   * Configurable height (in pixels) of the current scrollable viewport.
   * @type {Number}
   * @public
   */
  @computed
  scrollerSize: {
    get() {
      return this._scrollerSize || {};
    },
    set(value) {
      this._scrollerSize = value || {};
      this.viewportDomDidChange();
      return value;
    }
  },

  /**
   * The group currently positioned at the top of the (non-buffered) viewport.
   * This is the group which should be displayed in a sticky header at the top of the viewport.
   * @type {{ index: Number, group: Object, isLeaving: Boolean }}
   * @public
   */
  @computed('scrollerPos.top', 'groups', 'groupExtents', 'groupHeaderSize.outerHeight')
  groupAtTop(scrollTop, groups, groupExtents, groupHeaderOuterHeight) {
    // Compute the index of the first group that intersects the non-buffered viewport.
    // TODO optimization: use a binary search instead of a linear `find`
    const firstGroup = groupExtents.find((extent) => extent.bottom >= scrollTop);
    const index = firstGroup ? firstGroup.index : 0;
    const group = (groups || [])[index];
    const isLeaving = firstGroup ? ((firstGroup.bottom - scrollTop) <= groupHeaderOuterHeight) : false;
    return {
      index,
      group,
      isLeaving
    };
  },

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
  @computed('viewportDom', 'viewportBuffer', 'groupExtents', 'groupHeaderSize', 'groupItemSize')
  viewportIndices(viewportDom, viewportBuffer, groupExtents, groupHeaderSize, groupItemSize) {
    const grpHeaderSize = groupHeaderSize || {};
    const groupHeaderHeight = (grpHeaderSize.outerHeight) ? grpHeaderSize.outerHeight : 0;
    const grpItemSize = groupItemSize || {};
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
      top: actual.top - viewportBuffer,
      bottom: actual.bottom + viewportBuffer
    };

    // Compute the index of the first group that fits within the viewport.
    // TODO optimization: use a binary search instead of a linear `find`
    let firstGroup = groupExtents.find((extent) => extent.bottom >= buffered.top);
    firstGroup = firstGroup || groupExtents[0];
    const firstGroupIndex = firstGroup ? firstGroup.index : 0;

    // Compute the index of the first group item that fits within the viewport.
    let firstGroupItemIndex = 0;
    if (firstGroup && groupItemHeight) {
      const yDiff = Math.max(0, buffered.top - (firstGroup.top + groupHeaderHeight));
      firstGroupItemIndex = Math.floor(yDiff / groupItemHeight);
    }

    // Compute the index of the last group that fits within the viewport.
    const firstGroupBelow = groupExtents.slice(firstGroupIndex).find((extent) => extent.top > buffered.bottom);
    let lastGroupIndex = firstGroupBelow ? (firstGroupBelow.index - 1) : (groupExtents.length - 1);
    lastGroupIndex = Math.max(firstGroupIndex, lastGroupIndex);
    const lastGroup = groupExtents[lastGroupIndex];

    // Compute the index of the last group item that fits within the viewport.
    let lastGroupItemIndex = -1;
    if (lastGroup && groupItemHeight) {
      const yDiff = Math.max(0, buffered.bottom - (lastGroup.top + groupHeaderHeight));
      lastGroupItemIndex = Math.ceil(yDiff / groupItemHeight);
    }

    return {
      firstGroupIndex,
      firstGroupItemIndex,
      lastGroupIndex,
      lastGroupItemIndex
    };
  },

  /**
   * Maps each member of `groups` to a wrapper object where we can store additional UI-related info about that group.
   * Caching these wrapper objects, rather than reconstructing them when needed, enables us to bind to these objects
   * from templates and avoid destroying & re-creating their corresponding components in DOM.
   * @type {{ group: Object, index: Number }[]}
   * @private
   */
  @computed('groups.[]')
  groupRefs(groups) {
    groups = groups || [];
    const len = groups.length;
    return (groups || []).map((group, index) => ({ group, index, isLast: index === len - 1 }));
  },

  /**
   * Returns the same array as `groupRefs` but ensures each array member has its `extent` set to the corresponding
   * member of `groupExtents`.
   * @type {{ group: Object, index: Number, extent: { top: Number, bottom: Number }}[]}
   * @private
   */
  @computed('groupRefs', 'groupExtents')
  groupRefsWithExtents(refs, extents) {
    refs.forEach((ref, index) => {
      set(ref, 'extent', extents[index]);
    });
    return refs;
  },

  /**
   * Subset of `groupRefsWithExtents` which intersect the current scroll viewport.
   * @type {{ group: Object, index: Number, extent: { top: Number, bottom: Number }}[]}
   * @public
   */
  @computed('groupRefsWithExtents', 'firstGroupIndex', 'lastGroupIndex', 'groupExtents')
  groupRefsInViewport(refs, first, last) {
    return refs.slice(first, last + 1);
  }
});