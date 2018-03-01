import Component from '@ember/component';
import HasSizeAttr from 'respond/mixins/dom/has-size-attr';
import Clickable from 'respond/mixins/dom/clickable';
import layout from './template';
import computed, { alias } from 'ember-computed-decorators';
import { set } from '@ember/object';

/**
 * @class GroupTable GroupHeader Component
 * Represents the header row for a single group.
 * @public
 */
export default Component.extend(HasSizeAttr, Clickable, {
  tagName: 'header',
  layout,
  classNames: ['rsa-group-table-group-header'],
  classNameBindings: ['isLeaving:is-leaving:is-not-leaving', 'group.isOpen:is-open:is-not-open'],

  // Reference to the group data object that corresponds to this component. Typically passed down from parent.
  group: null,

  // The index of `group` relative to the table's `groups` array. Typically passed down from parent.
  index: 0,

  // Reference to the ancestor parent component; typically passed down from above.
  table: null,

  // Indicates whether this component will be used for DOM measuring purposes only. TYpically passed down from parent.
  isSample: false,

  // Enable size attrs only if this component will be measured.
  // @see respond/mixins/dom/has-size-attrs
  @alias('isSample')
  autoEnableSizeAttr: false,

  // Set target attr for size measurements.
  // @see respond/mixins/dom/has-size-attrs
  sizeAttr: 'table.groupHeaderSize',

  // Configure the payload that will be sent to click handlers.
  // @see respond/mixins/dom/clickable
  @computed('group')
  clickData(group) {
    return { group };
  },

  // Delegate click handler to the table parent component.
  // @see respond/mixins/dom/clickable
  @alias('table.groupClickAction')
  clickAction: null,

  // Delegate shift+click handler to the table parent component.
  // @see respond/mixins/dom/clickable
  @alias('table.groupCtrlClickAction')
  ctrlClickAction: null,

  // Delegate ctrl+click handler to the table parent component.
  // @see respond/mixins/dom/clickable
  @alias('table.groupShiftClickAction')
  shiftClickAction: null,

  // Indicates whether this group's bottom is near the top of the non-buffered viewport (i.e., within the height
  // of the header). Used for CSS bindings.
  // This information is used to implement sticky headers. When a group is leaving, we apply special CSS rules that
  // will give the appearance that the group's header is being "pushed" up as the user scroll down.
  @computed('group', 'table.groupAtTop')
  isLeaving(myGroup, groupAtTop) {
    const { group, isLeaving } = groupAtTop || {};
    return (myGroup === group) && !!isLeaving;
  },

  actions: {
    toggleIsOpen() {
      const group = this.get('group');
      const isOpen = this.get('group.isOpen');
      set(group, 'isOpen', !isOpen);
    }
  }
});
