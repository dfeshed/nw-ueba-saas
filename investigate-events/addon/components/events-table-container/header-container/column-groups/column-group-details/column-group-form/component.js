import Component from '@ember/component';
import { connect } from 'ember-redux';
import { run } from '@ember/runloop';
import computed from 'ember-computed-decorators';
import _ from 'lodash';

import { hasUniqueName } from 'investigate-events/util/validations';
import { metaMapForColumns } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { columnGroups } from 'investigate-events/reducers/investigate/column-group/selectors';
import { CONTENT_TYPE_PUBLIC } from 'investigate-events/constants/profiles';
import { COLUMN_THRESHOLD, COLUMN_VISIBILITY_THRESHOLD, STANDARD_COLUMN_WIDTH } from 'investigate-events/constants/columnGroups';

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

  // name of the columnGroup being created/edited
  columnGroupName: null,

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

  // initialize form data when a columnGroup has begun edit
  // reset form data when columnGroup is created, updated or reset
  didReceiveAttrs() {
    if (this.get('columnGroup')) {
      this.initializeFormData();
    }
  },

  /**
   * initialize a working copy of columns and leave original column group alone
   */
  initializeFormData() {
    if (this.get('isNameError')) {
      this.set('isNameError', false);
      this.set('nameInvalidMessage', null);
    }
    const columnGroup = this.get('columnGroup');
    this.set('displayedColumns', columnGroup?.columns || []);
    this.set('columnGroupName', columnGroup?.name);
    this._broadcastChangedGroup();
  },

  @computed('displayedColumns')
  selectedMetaDetails(displayedColumns) {
    const atThreshold = displayedColumns.length >= COLUMN_THRESHOLD;
    return {
      atThreshold,
      message: atThreshold ? this.get('i18n').t('investigate.events.columnGroups.selectionThresholdMessage') : null
    };
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
    this._broadcastChangedGroup();
  },

  /**
   * map columnGroup to server format
   */
  _prepareColumnGroup(columnGroup) {

    columnGroup.contentType = CONTENT_TYPE_PUBLIC;
    delete columnGroup?.isEditable;

    if (columnGroup?.columns) {
      columnGroup.columns = columnGroup.columns.map((col, index) => {
        return {
          metaName: col.field,
          displayName: col.title,
          // TODO add back when needed and reliable. the columns selected by the user follow the default columns ( time and medium ) in position
          // position: index + 2,
          visible: index < COLUMN_VISIBILITY_THRESHOLD,
          width: col.width || STANDARD_COLUMN_WIDTH
        };
      });
    }

    return columnGroup;
  },

  _broadcastChangedGroup() {
    const { columnGroup: originalGroup, columnGroupName, displayedColumns } = this.getProperties('columnGroup', 'columnGroupName', 'displayedColumns');

    const trimmedGroupName = columnGroupName?.trim();

    let editedColumnGroup = _.cloneDeep(originalGroup) || {};
    editedColumnGroup.name = trimmedGroupName;
    editedColumnGroup.columns = displayedColumns;
    editedColumnGroup = this._prepareColumnGroup(editedColumnGroup);

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

    const isNameError = !hasUniqueName(value, this.get('columnGroup')?.id, columnGroups);
    const nameInvalidMessage = isNameError ? this.get('i18n').t('investigate.events.columnGroups.nameNotUnique') : null;

    this.set('isNameError', isNameError);
    this.set('nameInvalidMessage', nameInvalidMessage);
  },

  actions: {

    handleNameChange(value) {
      this.set('columnGroupName', value);
      this._validateForErrors(value);
      this._broadcastChangedGroup();
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
      this.element.querySelector('.column-group-details').scrollTop = 0;
      this.set('columnFilterText', filterText);
    },

    reorderColumns(newOrder) {
      this._updateColumns(newOrder);
    }
  }

});

export default connect(stateToComputed)(ColumnGroupForm);
