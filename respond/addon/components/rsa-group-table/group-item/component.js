import Component from 'ember-component';
import HasSizeAttr from 'respond/mixins/dom/has-size-attr';
import computed, { alias } from 'ember-computed-decorators';
import layout from './template';
import { htmlSafe } from 'ember-string';
import $ from 'jquery';

/**
 * @class GroupTable Row Component
 * Represents a data row for a single item in a group.
 * @public
 */
export default Component.extend(HasSizeAttr, {
  layout,
  classNames: ['rsa-group-table-group-item'],
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
  @alias('isSample')
  autoEnableSizeAttr: false,

  // Set target attr for size measurements.
  // @see respond/mixins/dom/has-size-attrs
  sizeAttr: 'table.groupItemSize',

  // The index of `group` relative to the table's `groups` array. Typically passed down from parent.
  @computed('relativeIndex', 'relativeIndexOffset')
  index(relativeIndex, offset) {
    return relativeIndex + offset;
  },

  // Computes the y-coordinate of the top of this group, in pixels. Typically passed down from parent.
  @computed('index', 'table.groupItemSize.outerHeight')
  style(index, itemHeight) {
    const top = index * itemHeight;
    const styleText = $.isNumeric(top) ? `transform: translateY(${top}px)` : '';
    return htmlSafe(`${styleText}`);
  }
});