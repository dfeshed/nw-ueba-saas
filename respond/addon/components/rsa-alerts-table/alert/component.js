import Group from 'respond/components/rsa-group-table/group/component';
import computed, { equal } from 'ember-computed-decorators';

export default Group.extend({
  classNames: ['rsa-alerts-table-alert'],
  classNameBindings: ['isFirst', 'isLast'],

  // True if this group is the first group in the entire table's `groups` array.
  @equal('index', 0)
  isFirst: false,

  // True if this group is the last group in the entire table's `groups` array.
  @computed('group', 'table.groups.lastObject')
  isLast(group, lastGroup) {
    return group === lastGroup;
  }
});
