import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import sort from 'fast-sort';
import _ from 'lodash';

export default Component.extend({

  classNames: ['column-group-details'],

  // original columnGroup
  columnGroup: null,

  // list-manager function that accepts validated edited item
  editColumnGroup: null,

  // computed columnGroup with columns sorted alphabetically by field
  @computed('columnGroup')
  sortedColumnGroup(columnGroup) {

    if (columnGroup?.columns) {
      const sortedColumnGroup = JSON.parse(JSON.stringify(columnGroup));
      sort(sortedColumnGroup.columns).by([{ asc: (column) => column.field.toUpperCase() }]);
      return sortedColumnGroup;
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
