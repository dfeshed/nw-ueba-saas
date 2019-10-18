import Component from '@ember/component';
import { connect } from 'ember-redux';
import { run } from '@ember/runloop';
import computed from 'ember-computed-decorators';
import _ from 'lodash';

import { metaMapForColumns } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { columnGroups } from 'investigate-events/reducers/investigate/column-group/selectors';

const _filterColumns = (columns, filterText) => {
  const filterTextLower = filterText.toLowerCase();
  return columns.filter((col) => {
    const stringToSearch = `${col.field} ${col.title}`.toLowerCase();
    return stringToSearch.includes(filterTextLower);
  });
};

const stateToComputed = (state) => ({
  columnGroups: columnGroups(state),
  allMeta: metaMapForColumns(state)
});

const ColumnGroupForm = Component.extend({

  classNames: ['column-group-form'],

  classNameBindings: ['isNameError'],

  // original columnGroup with sorted columns
  columnGroup: null,

  // new columnGroup on create/edit
  newColumnGroup: null,

  // columns selected for the columnGroup
  displayedColumns: [],

  // list-manager function that accepts validated edited item
  editColumnGroup: () => {},

  // Text used to filter the visible columns in the form
  columnFilterText: '',

  // Should filter text be selected?
  shouldSelectFilterText: undefined,

  isNameError: false,

  nameInvalidMessage: null,

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

  @computed('availableMeta', 'columnFilterText')
  filteredAvailableMeta(allMeta, filterText) {
    return _filterColumns(allMeta, filterText);
  },

  // Check if the filtered count doesn't equal the displayed count
  // if they don't, then some columns have been filtered out
  @computed('filteredAvailableMeta', 'availableMeta')
  areAvailableMetaFiltered(filtered, available) {
    return filtered.length !== available.length;
  },

  @computed('displayedColumns', 'columnFilterText')
  filteredDisplayedColumns(chosenColumns, filterText) {
    return _filterColumns(chosenColumns, filterText);
  },

  // Check if the filtered count doesn't equal the displayed count
  // if they don't, then some columns have been filtered out
  @computed('filteredDisplayedColumns', 'displayedColumns')
  areDisplayedColumnsFiltered(filtered, displayed) {
    return filtered.length !== displayed.length;
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

      const columns = columnGroup.columns.map((col, index) => {
        return {
          metaName: col.field,
          displayName: col.title,
          // the columns selected by the user follow the default columns ( time and medium ) in position
          position: index + 2
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

    if (!this.get('isNameError') && newGroup?.name && newGroup?.columns?.length) {
      const isNewGroupChanged = JSON.stringify(newGroup) !== JSON.stringify(originalGroup);
      editedColumnGroup = isNewGroupChanged ? newGroup : null;
      editedColumnGroup = this._prepareColumnGroup(editedColumnGroup);
    }

    // Calling editColumnGroup with null is an indicator to the called function that
    // the data being edited is currently invalid
    this.get('editColumnGroup')(editedColumnGroup);

  },

  _updateFilterSelection() {
    this.set('shouldSelectFilterText', true);

    // let rendering happen then set it back
    run.next(() => {
      this.set('shouldSelectFilterText', false);
    });
  },

  _validateForErrors(value) {
    const columnGroups = this.get('columnGroups') || [];
    // TODO edit unique name check for new items only
    const hasUniqueName = !columnGroups.find((item) => item.name == value);

    const isNameError = !hasUniqueName;
    const nameInvalidMessage = isNameError ? this.get('i18n').t('investigate.events.columnGroups.nameNotUnique') : null;

    this.set('isNameError', isNameError);
    this.set('nameInvalidMessage', nameInvalidMessage);
  },

  actions: {

    handleNameChange(value) {
      value = value.trim();
      const newColumnGroup = this.get('newColumnGroup');
      newColumnGroup.name = value;

      this._validateForErrors(value);

      this._checkDirtyChange();
    },

    // adds candidate meta to columns at the end of the list
    addMetaToColumns(meta) {
      this._updateFilterSelection();
      const displayedColumns = _.cloneDeep(this.get('displayedColumns'));
      displayedColumns.push(meta);
      this._updateColumns(displayedColumns);
    },

    removeMetaFromColumns(meta) {
      this._updateFilterSelection();
      const displayedColumns = _.cloneDeep(this.get('displayedColumns'));
      this._updateColumns(_.filter(displayedColumns, (c) => c.field != meta.field));
    },

    updateColumnFilterText(filterText) {
      this.element.querySelector('.group-details').scrollTop = 0;
      this.set('columnFilterText', filterText);
    },

    reorderColumns(newOrder) {
      this._updateColumns(newOrder);
    }
  }

});

export default connect(stateToComputed, undefined)(ColumnGroupForm);
