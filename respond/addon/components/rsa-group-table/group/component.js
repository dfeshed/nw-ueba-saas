import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import layout from './template';
import { htmlSafe } from 'ember-string';
import $ from 'jquery';

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
  @computed('top')
  style(top) {
    const styleText = $.isNumeric(top) ? `top:${top}px` : '';
    return htmlSafe(styleText);
  },

  // Encodes a CSS string that preserves the vertical space needed above the group items for the group header.
  // Enables absolute positioning of the group header, which is required in order to support sticky headers.
  @computed('isSample', 'table.groupHeaderSize.outerHeight')
  headerContainerStyle(isSample, headerHeight) {
    const styleText = (!isSample && $.isNumeric(headerHeight)) ? `min-height:${headerHeight}px` : '';
    return htmlSafe(styleText);
  },

  // Encodes a CSS string that preserves the vertical space needed by the group items.
  // Enables us to absolutely position the group header at the bottom of this space as the user scrolls past the
  // bottom of the group, which is required in order to support sticky headers.
  @computed('isSample', 'table.groupItemSize.outerHeight', 'group.items.length')
  itemsContainerStyle(isSample, itemHeight, itemCount) {
    const styleText = (!isSample && $.isNumeric(itemHeight * itemCount)) ? `min-height:${itemHeight * itemCount}px` : '';
    return htmlSafe(styleText);
  },

  /**
   * The index of the first group item within the (buffered) viewport.
   * If this group is the first group in the viewport, this index will match the table's firstGroupItemIndex;
   * otherwise it will be zero.
   * @type {number}
   * @private
   */
  @computed('isSample', 'index', 'table.{firstGroupIndex,firstGroupItemIndex}')
  firstItemIndex(isSample, index, firstGroupIndex, firstGroupItemIndex) {
    if (isSample) {
      return 0;
    } else {
      return (index === firstGroupIndex) ? firstGroupItemIndex : 0;
    }
  },

  /**
   * The index of the last group item within the (buffered) viewport.
   * If this group is the last group in the viewport, this index will match the table's lastGroupItemIndex;
   * otherwise it will be -1, meaning the last index in this group's items.
   * @type {number}
   * @private
   */
  @computed('isSample', 'index', 'table.{lastGroupIndex,lastGroupItemIndex}')
  lastItemIndex(isSample, index, lastGroupIndex, lastGroupItemIndex) {
    if (isSample) {
      return 0;
    } else {
      return (index === lastGroupIndex) ? lastGroupItemIndex : -1;
    }
  },

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
  @computed('group.items.[]', 'firstItemIndex', 'lastItemIndex')
  itemsInViewport(items, first, last) {
    if ((last === -1) && (first === 0)) {   // all items
      return items;
    } else if (last > -1) {       // from first item index to last item index
      return (items || []).slice(first, last + 1);
    } else {                      // from first item index to end of items
      return (items || []).slice(first);
    }
  },

  // Determines if this group is selected by searching for the group's id in the parent table's selections hash.
  @computed('group.id', 'table.selections.areGroups', 'table.selectionsHash')
  isSelected(id, areGroups, hash) {
    return !!areGroups && !!hash && (id in hash);
  }
});
