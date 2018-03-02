import Component from '@ember/component';
import HasSizeAttr from 'respond/mixins/dom/has-size-attr';
import Clickable from 'respond/mixins/dom/clickable';
import computed, { alias } from 'ember-computed-decorators';
import layout from './template';
import { htmlSafe } from '@ember/string';
import $ from 'jquery';

/**
 * @class GroupTable Row Component
 * Represents a data row for a single item in a group.
 * @public
 */
export default Component.extend(HasSizeAttr, Clickable, {
  layout,
  classNames: ['rsa-group-table-group-item'],
  classNameBindings: ['isSelected'],
  attributeBindings: ['style'],

  /**
   * Configurable name of the Ember.Component class to be used for rendering cells. This can be overwritten
   * per column in the column object's `componentClass` property.
   * @type {String}
   * @public
   */
  cellComponentClass: 'rsa-group-table/group-item-cell',

  // Reference to the group.items[] member that corresponds to this component. Typically passed down from parent.
  item: null,

  // The index of `item`, relative to `relativeIndexOffset`; that is, relative to the index of the first group item
  // that is currently rendered.  Typically passed down from parent.
  // This attr & `relativeIndexOffset` are offered as an alternative to setting the `index` attr directly. These
  // attrs are a useful alternative when doing lazy rendering.
  relativeIndex: 0,

  // An offset that, when added to `relativeIndex`, yields the absolute `index` of `item` among the group's items array.
  // Typically passed down from parent as the index of the first group item that is currently rendered.
  // This attr & `relativeIndex` are offered as an alternative to setting the `index` attr directly. These
  // attrs are a useful alternative when doing lazy rendering.
  relativeIndexOffset: 0,

  // Indicates whether this component will be used for DOM measuring purposes only. TYpically passed down from parent.
  isSample: false,

  // Enable size attrs only if this component will be measured.
  // @see respond/mixins/dom/has-size-attrs
  @computed('isSample', 'index')
  autoEnableSizeAttr(isSample, index) {
    return !!isSample && (index === 0);
  },

  // Set target attr for size measurements.
  // @see respond/mixins/dom/has-size-attrs
  sizeAttr: 'table.groupItemSize',

  // The index of `group` relative to the table's `groups` array. Typically passed down from parent.
  @computed('relativeIndex', 'relativeIndexOffset')
  index(relativeIndex, offset) {
    return relativeIndex + offset;
  },

  // Configure the payload that will be sent to click handlers.
  // @see respond/mixins/dom/clickable
  @computed('item')
  clickData(item) {
    return { item };
  },

  // Delegate click handler to the table parent component.
  // @see respond/mixins/dom/clickable
  @alias('table.itemClickAction')
  clickAction: null,

  // Delegate shift+click handler to the table parent component.
  // @see respond/mixins/dom/clickable
  @alias('table.itemCtrlClickAction')
  ctrlClickAction: null,

  // Delegate ctrl+click handler to the table parent component.
  // @see respond/mixins/dom/clickable
  @alias('table.itemShiftClickAction')
  shiftClickAction: null,

  // Computes the y-coordinate of the top of this group, in pixels. Typically passed down from parent.
  @computed('index', 'table.groupItemSize.outerHeight')
  style(index, itemHeight) {
    const top = index * itemHeight;
    const styleText = $.isNumeric(top) ? `top: ${top}px` : '';
    return htmlSafe(`${styleText}`);
  },

  // Determines if this group is selected by searching for the group's id in the parent table's selections hash.
  @computed('item.id', 'table.selections.areGroups', 'table.selectionsHash')
  isSelected(id, areGroups, hash) {
    return !areGroups && !!hash && (id in hash);
  }
});
