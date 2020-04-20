import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import _ from 'lodash';

export default Component.extend({

  classNames: ['column-group-details'],

  // original columnGroup
  columnGroup: null,

  // list-manager function that accepts validated edited item
  editColumnGroup: null,

  @computed('columnGroup')
  displayedColumnGroup(columnGroup) {

    if (columnGroup?.columns) {
      const displayedColumnGroup = _.cloneDeep(columnGroup);
      // need not show time and medium columns to the user as they are default.
      displayedColumnGroup.columns = displayedColumnGroup.columns.filter((column) => column.field !== 'time' && column.field !== 'medium');
      return displayedColumnGroup;
    }
    return columnGroup;
  },

  @computed('columnGroup')
  isEditing(columnGroup) {
    if (!_.isEmpty(columnGroup)) {
      return columnGroup.isEditable;
    }
    return true;
  }

});
