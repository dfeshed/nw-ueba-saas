import Mixin from '@ember/object/mixin';
import HasGroupedRows from './has-grouped-rows';
import { get, computed } from '@ember/object';
import { isNumeric } from 'component-lib/utils/jquery-replacement';

/**
 * @class ComputesRowExtents Mixin
 * @extends HasGroupedRows Mixin
 * Enables an Object to compute the tops & bottoms of its groups as computed properties, given a group header height
 * & a group item height.
 *
 * Once the configurable `groupHeaderSize` & `groupItemSize` attrs are set to the DOM sizes of a group header &
 * a group item respectively, this mixin computes the extents (tops & bottoms) of the group DOMs. It makes a few
 * assumptions in order to do this in a performant way; namely: it assumes all group headers share the same height,
 * and all groups' items share the same height.  This assumption allow us to keep the math simple & fast.
 *
 * The `groupExtents` computes the top & bottom (y-coordinates) in pixels of each group's DOM, taking into account
 * which groups are open.  (In other words, item heights are only counted for open groups; header heights are
 * counted for all groups, both open and closed.) The `totalRowsHeight` computes the total height of all groups.
 *
 * @public
 */
export default Mixin.create(HasGroupedRows, {
  /**
   * Size (in pixels) of the DOM of a single group's header.
   * Since we assume all group header rows have the same height, we can just store a single value here.
   * @type {{ outerHeight: Number }}
   * @public
   */
  groupHeaderSize: null,

  /**
   * Size (in pixels) of the DOM of a single data item within a group.
   * Since we assume all item rows have the same height, we can just store a single value here.
   * @type {{ outerHeight: Number }}
   * @public
   */
  groupItemSize: null,

  /**
   * An array of the counts of items in each of the `groups`. This is cached for performance.
   *
   * @assumes Each group's `items` array won't change length unless the `items` property is re-assigned a new array.
   * @type {Number[]}
   * @private
   */
  groupItemCounts: computed('groups.@each.items', function() {
    return (this.groups || []).map((group) => (get(group, 'items.length') || 0));
  }),

  /**
   * An array of the heights of each of the `groups`.
   * Computes 2 heights per group: a height when the group is open, a height when the group is closed.
   * @type {{ openHeight: Number, closedHeight: Number }[]}
   * @private
   */
  groupHeights: computed('groupItemCounts', 'groupHeaderSize.outerHeight', 'groupItemSize.outerHeight', function() {
    const groupHeaderSizeOuterHeight = this.groupHeaderSize?.outerHeight || 0;
    const groupItemSizeOuterHeight = this.groupItemSize?.outerHeight || 0;
    return this.groupItemCounts.map((count) => ({
      openHeight: groupHeaderSizeOuterHeight + count * groupItemSizeOuterHeight,
      closedHeight: groupHeaderSizeOuterHeight
    }));
  }),

  /**
   * An array of the tops & bottoms (in pixels) of each of the `groups`.
   * The tops & bottoms are recomputed each time a group is toggled open/closed.
   * @type {{ index: Number, top: Number, bottom: Number }[]}
   * @public
   */
  groupExtents: computed('groups.@each.isOpen', 'groupHeights', function() {
    const extents = [];
    let y = 0;
    (this.groups || []).forEach((group, index) => {
      const isOpen = get(group, 'isOpen');
      const heights = this.groupHeights[index];
      const height = isOpen ? heights.openHeight : heights.closedHeight;
      extents.push({
        index,
        top: y,
        bottom: y + height - 1
      });
      y += height;
    });
    return extents;
  }),

  /**
   * The total pixel height of open rows as a String, including the units suffix 'px'.
   * That is, the sum of the heights of all group headers + the heights of the items in each of the groups that are
   * currently open.
   * @example '52px'
   * @type {Number}
   * @public
   */
  totalRowsHeight: computed('groupExtents.lastObject.bottom', function() {
    return isNumeric(this.groupExtents?.lastObject?.bottom) ? `${this.groupExtents?.lastObject?.bottom + 1}px` : '';
  })
});
