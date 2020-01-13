import { computed } from '@ember/object';
import Component from '@ember/component';
import layout from './template';
import { htmlSafe } from '@ember/string';
import { isNumeric } from 'component-lib/utils/jquery-replacement';

/**
 * @class Group Table Group Component
 * Represents a single group, including its header row and the rows for each of its items.
 * @public
 */
export default Component.extend({
  tagName: 'section',
  layout,
  classNames: ['rsa-group-table-group'],
  classNameBindings: ['isSample', 'isSelected'],
  attributeBindings: ['style'],

  // Reference to the group data object that corresponds to this component. Typically passed down from parent.
  group: null,

  // The index of `group` relative to the table's `groups` array. Typically passed down from parent.
  index: 0,

  // The y-coordinate of the top of this group, in pixels. Typically passed down from parent.
  top: 0,

  // Reference to the ancestor parent component; typically passed down from above.
  table: null,

  /**
   * Configurable name of the Ember Component to be used for rendering the group header row.
   * This component will automatically receive a `group` attr which corresponds to a member of the table's groups array.
   * @type {String}
   * @default 'rsa-group-table/group-header'
   * @public
   */
  headerComponentClass: 'rsa-group-table/group-header',

  /**
   * Configurable name of the Ember Component to be used for rendering the individual item rows.
   * This component will automatically receive an `item` attr which corresponds to a member of the group's items array.
   * @type {String}
   * @default 'rsa-group-table/group-item'
   * @public
   */
  itemComponentClass: 'rsa-group-table/group-item',

  /**
   * If truthy, indicates that this group will be used for DOM measuring purposes only, and not shown to the end-user.
   * This means we only need to render its header & first item.
   * @type {Boolean}
   * @public
   */
  isSample: false,

  // Encodes a CSS string to position the top of this Component's DOM. Enables absolute positioning, which is
  // is required for lazy rendering of rows.
  style: computed('top', function() {
    const styleText = isNumeric(this.top) ? `top:${this.top}px` : '';
    return htmlSafe(styleText);
  }),

  // Encodes a CSS string that preserves the vertical space needed above the group items for the group header.
  // Enables absolute positioning of the group header, which is required in order to support sticky headers.
  headerContainerStyle: computed('isSample', 'table.groupHeaderSize.outerHeight', function() {
    const styleText = (!this.isSample && isNumeric(this.table?.groupHeaderSize?.outerHeight)) ? `min-height:${this.table?.groupHeaderSize?.outerHeight}px` : '';
    return htmlSafe(styleText);
  }),

  // Encodes a CSS string that preserves the vertical space needed by the group items.
  // Enables us to absolutely position the group header at the bottom of this space as the user scrolls past the
  // bottom of the group, which is required in order to support sticky headers.
  itemsContainerStyle: computed(
    'isSample',
    'table.groupItemSize.outerHeight',
    'group.items.length',
    function() {
      const styleText = (!this.isSample && isNumeric(this.table?.groupItemSize?.outerHeight * this.group?.items?.length)) ? `min-height:${this.table?.groupItemSize?.outerHeight * this.group?.items?.length}px` : '';
      return htmlSafe(styleText);
    }
  ),

  /**
   * The index of the first group item within the (buffered) viewport.
   * If this group is the first group in the viewport, this index will match the table's firstGroupItemIndex;
   * otherwise it will be zero.
   * @type {number}
   * @private
   */
  firstItemIndex: computed(
    'isSample',
    'index',
    'table.firstGroupIndex',
    'table.firstGroupItemIndex',
    function() {
      if (this.isSample) {
        return 0;
      } else {
        return (this.index === this.table?.firstGroupIndex) ? this.table?.firstGroupItemIndex : 0;
      }
    }
  ),

  /**
   * The index of the last group item within the (buffered) viewport.
   * If this group is the last group in the viewport, this index will match the table's lastGroupItemIndex;
   * otherwise it will be -1, meaning the last index in this group's items.
   * @type {number}
   * @private
   */
  lastItemIndex: computed(
    'isSample',
    'index',
    'table.lastGroupIndex',
    'table.lastGroupItemIndex',
    function() {
      if (this.isSample) {
        return 0;
      } else {
        return (this.index === this.table?.lastGroupIndex) ? this.table?.lastGroupItemIndex : -1;
      }
    }
  ),

  /**
   * The subset of the group's items currently within the buffered viewport.
   *
   * Optimization: we want this component to be performant even when the count of items is large.
   * Therefore we don't wrap each item individually in another object, so that the Ember rendering engine
   * will recognize the object if it is already in the DOM.  Otherwise, if we did wrap, we'd have to keep
   * the wrapped object cached, which could introduce a large memory footprint for sufficiently large items set.
   *
   * @type {[]}
   * @private
   */
  itemsInViewport: computed('group.items.[]', 'firstItemIndex', 'lastItemIndex', function() {
    if ((this.lastItemIndex === -1) && (this.firstItemIndex === 0)) { // all items
      return this.group?.items;
    } else if (this.lastItemIndex > -1) { // from first item index to last item index
      return (this.group?.items || []).slice(this.firstItemIndex, this.lastItemIndex + 1);
    } else { // from first item index to end of items
      return (this.group?.items || []).slice(this.firstItemIndex);
    }
  }),

  // Determines if this group is selected by searching for the group's id in the parent table's selections hash.
  isSelected: computed(
    'group.id',
    'table.selections.areGroups',
    'table.selectionsHash',
    function() {
      return !!this.table?.selections?.areGroups && !!this.table?.selectionsHash && (this.group?.id in this.table?.selectionsHash);
    }
  )
});
