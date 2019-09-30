import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { metaMapForColumns } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import _ from 'lodash';

const stateToComputed = (state) => ({
  /* TODO Add Column Group
   * use meta from language call untill API provides all meta regardless of service selected
   */
  allMeta: metaMapForColumns(state)
});

const ColumnGroupForm = Component.extend({

  classNames: ['column-group-form'],

  // original columnGroup with sorted columns
  columnGroup: null,

  // new columnGroup on create/edit
  newColumnGroup: null,

  // columns selected for the columnGroup
  displayedColumns: [],

  // list-manager function that accepts validated edited item
  editColumnGroup: () => {},

  /**
   * initialize a working copy of columns and leave original column group alone
   */
  didInsertElement() {
    this.set('newColumnGroup', {});
    const columnGroup = this.get('columnGroup');

    if (columnGroup?.columns) {
      this.set('displayedColumns', columnGroup.columns);
    }
  },

  @computed('allMeta', 'displayedColumns')
  availableMeta(allMeta, displayedColumns) {
    return _.differenceBy(allMeta, displayedColumns, 'field');
  },

  /**
   * call to update displayedColumns on every add/remove
   */
  _updateColumns(columns) {
    this.set('displayedColumns', columns);
    const newColumnGroup = this.get('newColumnGroup');
    newColumnGroup.columns = columns;
    this._checkDirtyChange();
  },

  /**
   * map columnGroup to server format
   */
  _prepareColumnGroup(columnGroup) {
    if (columnGroup?.columns) {
      const { name } = columnGroup;
      const columns = columnGroup.columns.map((col) => {
        return {
          metaName: col.field,
          displayName: col.title
        };
      });

      return { name, columns };
    }
    return columnGroup;
  },

  _checkDirtyChange() {
    let editedColumnGroup = null;
    const originalGroup = this.get('columnGroup');
    const newGroup = this.get('newColumnGroup');

    if (newGroup?.name && newGroup?.columns?.length) {
      const isNewGroupChanged = JSON.stringify(newGroup) !== JSON.stringify(originalGroup);
      editedColumnGroup = isNewGroupChanged ? newGroup : null;
      editedColumnGroup = this._prepareColumnGroup(editedColumnGroup);
    }

    // Calling editColumnGroup with null is an indicator to the called function that
    // the data being edited is currently invalid
    this.get('editColumnGroup')(editedColumnGroup);
  },

  actions: {

    handleNameChange(value) {
      const newColumnGroup = this.get('newColumnGroup');
      newColumnGroup.name = value;
      this._checkDirtyChange();
    },

    // adds candidate meta to sorted columns in display in correct sort order
    addMetaToColumns(meta) {
      const displayedColumns = _.cloneDeep(this.get('displayedColumns'));
      displayedColumns.splice(_.sortedIndexBy(displayedColumns, meta, (m) => m.field), 0, meta);
      this._updateColumns(displayedColumns);
    },

    removeMetaFromColumns(meta) {
      const displayedColumns = _.cloneDeep(this.get('displayedColumns'));
      this._updateColumns(_.filter(displayedColumns, (c) => c.field != meta.field));
    }

  }

});

export default connect(stateToComputed, undefined)(ColumnGroupForm);
