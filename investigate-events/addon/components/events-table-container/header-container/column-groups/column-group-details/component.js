import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import _ from 'lodash';

export default Component.extend({

  classNames: ['column-group-details'],

  // original columnGroup
  columnGroup: null,

  // list-manager function that accepts validated edited item
  editColumnGroup: null,

  // need not show time and medium columns to the user as they are default.
  @computed('columnGroup')
  displayedColumnGroup(columnGroup) {

    if (columnGroup?.columns) {
      const displayedColumnGroup = _.cloneDeep(columnGroup);
      displayedColumnGroup.columns = displayedColumnGroup.columns.filter((column) => column.field !== 'time' && column.field !== 'medium');
      return displayedColumnGroup;
    }
    return columnGroup;
  },

  @computed('columnGroup')
  isEditing(columnGroup) {
    if (!_.isEmpty(columnGroup)) {
      // TODO Edit Group Items. Until then, custom columnGroups will also be read-only
      // return columnGroup.isEditable;
      return false;
    }
    return true;
  }

});
