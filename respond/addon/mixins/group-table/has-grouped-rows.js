import Mixin from '@ember/object/mixin';

/**
 * @class HasGroupedRows Mixin
 * Equips a Component with a configurable `groups` attr.
 * @public
 */
export default Mixin.create({
  /**
   * The array of data groups to be rendered.  A "group" is a set of data records.
   *
   * By default, each group is expected to an object with the following characteristics:
   * - value: {*} the grouped value that is common to all data records in the group;
   * - items: {Object[]} the array of data records (POJOs) in this group;
   * - isOpen: {Boolean} indicates whether or not the group's `items` are shown (open) or hidden (closed).
   *
   * Typically each group is rendered as a group header (optional), followed by a set of data records.
   * The group's header typically shows the group's value, and an icon for opening & closing the group's items.
   * When the group is "open", the group's items are shown under the group header; otherwise the group's items
   * are not shown.
   *
   * @type {Object[]}
   * @public
   */
  groups: null
});
