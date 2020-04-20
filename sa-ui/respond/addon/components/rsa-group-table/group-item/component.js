import { computed } from '@ember/object';
import Component from '@ember/component';
import HasSizeAttr from 'respond/mixins/dom/has-size-attr';
import Clickable from 'respond/mixins/dom/clickable';
import { alias } from '@ember/object/computed';
import layout from './template';
import { htmlSafe } from '@ember/string';
import { isNumeric } from 'component-lib/utils/jquery-replacement';

/**
 * @class GroupTable Row Component
 * Represents a data row for a single item in a group.
 * @public
 */
export default Component.extend(HasSizeAttr, Clickable, {
  layout,
  testId: 'groupTableItem',
  classNames: ['rsa-group-table-group-item'],
  classNameBindings: ['isSelected'],
  attributeBindings: ['testId:test-id', 'style'],

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
  autoEnableSizeAttr: computed('isSample', 'index', function() {
    return !!this.isSample && (this.index === 0);
  }),

  // Set target attr for size measurements.
  // @see respond/mixins/dom/has-size-attrs
  sizeAttr: 'table.groupItemSize',

  // The index of `group` relative to the table's `groups` array. Typically passed down from parent.
  index: computed('relativeIndex', 'relativeIndexOffset', function() {
    return this.relativeIndex + this.relativeIndexOffset;
  }),

  // Configure the payload that will be sent to click handlers.
  // @see respond/mixins/dom/clickable
  clickData: computed('item', function() {
    return { item: this.item };
  }),

  // Delegate click handler to the table parent component.
  // @see respond/mixins/dom/clickable
  clickAction: alias('table.itemClickAction'),

  // Delegate ctrl+click handler to the table parent component.
  // @see respond/mixins/dom/clickable
  ctrlClickAction: alias('table.itemCtrlClickAction'),

  // Delegate shift+click handler to the table parent component.
  // @see respond/mixins/dom/clickable
  shiftClickAction: alias('table.itemShiftClickAction'),

  // Computes the y-coordinate of the top of this group, in pixels. Typically passed down from parent.
  style: computed('index', 'table.groupItemSize.outerHeight', function() {
    const top = this.index * this.table?.groupItemSize?.outerHeight;
    const styleText = isNumeric(top) ? `top: ${top}px` : '';
    return htmlSafe(`${styleText}`);
  }),

  // Determines if this group is selected by searching for the group's id in the parent table's selections hash.
  isSelected: computed(
    'item.id',
    'table.selections.areGroups',
    'table.selectionsHash',
    function() {
      return !this.table?.selections?.areGroups && !!this.table?.selectionsHash && (this.item?.id in this.table?.selectionsHash);
    }
  )
});
