import { computed } from '@ember/object';
import Group from 'respond/components/rsa-group-table/group/component';
import { equal } from '@ember/object/computed';

export default Group.extend({
  classNames: ['rsa-alerts-table-alert'],
  classNameBindings: ['isFirst', 'isLast'],
  testId: 'alertsTableSection',
  attributeBindings: ['testId:test-id'],

  // True if this group is the first group in the entire table's `groups` array.
  isFirst: equal('index', 0),

  // True if this group is the last group in the entire table's `groups` array.
  isLast: computed('group', 'table.groups.lastObject', function() {
    return this.group === this.table?.groups?.lastObject;
  }),

  /**
   * A click on a non-link storypoint or event cell closes any open open recon/ueba overlay.
   */
  click() {
    if (this.get('closeOverlay')) {
      this.get('closeOverlay')();
    }
  }
});
