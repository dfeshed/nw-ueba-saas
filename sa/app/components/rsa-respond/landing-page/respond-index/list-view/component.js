import Ember from 'ember';
import IncidentHelper from 'sa/incident/helpers';
import { viewType } from 'sa/protected/respond/index/route';
import IncidentConstants from 'sa/incident/constants';
import computed from 'ember-computed-decorators';
import ListViewConfig from './config';

const {
  inject: {
    service
  },
  Component,
  run,
  computed: emberComputed,
  set,
  isNone
} = Ember;

export default Component.extend({
  tagName: 'hbox',
  eventBus: service(),

  // Contains the values to be updated by bulk edit.
  updateObject: {},

  // default sorted field for list view
  currentSort: 'riskScore',

  // The boolean which determines whether or not the checkbox in the header row is checked
  headerCheckbox: false,

  // Value used to update the success message for the bulk edit
  totalFieldsUpdated: 0,

  // Boolean which determines whether or not the success message should show
  showSuccessMessage: false,

  // Array of selected priorities
  filteredPriorities: [],
  // Array of selected statuses
  filteredStatuses: [],

  // Full list of available columns to display
  availableColumnsConfig: ListViewConfig.availableColumnsConfig,

  init() {
    this._super(...arguments);
    this.setProperties({
      'filteredPriorities': [],
      'filteredStatuses': []
    });

    this.set('updateObject', {});
  },

  @computed('categoryTags.[]')
  normalizedTreeData: (categoryTags) => IncidentHelper.normalizeCategoryTags(categoryTags),

  /**
   * @name validateEditInProgress
   * @description Validates if there is a bulk edit operation in progress. If it is, it pop-ups the modal dialog.
   * @private
   */
  validateEditInProgress() {
    if (Object.keys(this.get('updateObject')).length === 0) {
      this.set('showSuccessMessage', false);
      return false;
    } else {
      this.send('openBulkEditModal');
      return true;
    }
  },
  /**
   * @name statusList
   * @description Returns a list of available status. Each element has:
   *  - id: the id of the status,
   *  - value: a computed property used to handle user interaction
   * @type number[]
   * @public
   */
  @computed()
  statusList() {
    let statusArray = IncidentConstants.incidentStatusIds.map((statusId) => {
      return {
        id: statusId,
        value: emberComputed({
          get: () => this.get('filteredStatuses').includes(statusId),
          set: (key, value) => {
            let editInProgress = (value && this.validateEditInProgress());
            if (!editInProgress) {
              this.updateFilterValue('status', 'filteredStatuses', statusId, value);
              return value;
            }
          }
        })
      };
    });
    return statusArray;
  },

  /**
   * @name priorityList
   * @description Returns a list of available priorities. Each element has:
   *  - id: the id of the priority,
   *  - value: a computed property used to handle user interaction
   * @public
   */
  @computed()
  priorityList() {
    let priorityArray = IncidentConstants.incidentPriorityIds.map((priorityId) => {
      return {
        id: priorityId,
        value: emberComputed({
          get: () => this.get('filteredPriorities').includes(priorityId),
          set: (key, value) => {
            let editInProgress = value && this.validateEditInProgress();
            if (!editInProgress) {
              this.updateFilterValue('priority', 'filteredPriorities', priorityId, value);
              return value;
            }
          }
        })
      };
    });
    return priorityArray;
  },

  /**
   * @name usersList
   * @description Creates an array of the user object and adds the selected parameter to each with the value of false.
   * @param {Object} users Current users object from the model
   * @public
   */
  @computed('users.[]')
  usersList(users) {
    let unAssigneeUser = {
      'id': -1,
      'selected': false
    };

    let arrUsers = [ unAssigneeUser ];

    if (users && users.setEach) {
      users.setEach('selected', false);
      arrUsers.addObjects(users);
    }

    return arrUsers;
  },

  /**
   * @name isSelectAvailable
   * @description Determines whether or not the sort and priority select lists are available given three factors:
   * 1) Have any incidents been selected
   * 2) Do any of the selected incidents have a closed state
   * 3) Do any of the incidents have a false positive state.
   * If either the priorty sort or the select sort are not available, then their value within the updateObject is deleted.
   * @param {Object} allIncidents:
   * @public
   */
  @computed('allIncidents.results.@each.checked')
  isSelectAvailable(allIncidents) {
    let checkedIncidents = allIncidents.filterBy('checked', true);
    let arrayOfSelectedIncidentIDs = checkedIncidents.map(function(item) {
      return item.id;
    });
    let foundClosedItem = checkedIncidents.findBy('statusSort', IncidentConstants.incStatus.CLOSED);
    let foundFalsePositiveItem = checkedIncidents.findBy('statusSort', IncidentConstants.incStatus.CLOSED_FALSE_POSITIVE);
    let selectionAvailableEvaluation = (
        arrayOfSelectedIncidentIDs.length === 0 ||
        typeof foundClosedItem !== 'undefined' ||
        typeof foundFalsePositiveItem !== 'undefined'
    );

    if (selectionAvailableEvaluation) {
      let selectedUsersListItem = this.get('usersList').findBy('selected', true);
      let selectedPriorityListItem = this.get('priorityList').findBy('selected', true);

      if (typeof selectedUsersListItem !== 'undefined') {
        set(selectedUsersListItem, 'selected', false);
        this.set('updateObject.assignee', null);
      }

      if (typeof selectedPriorityListItem !== 'undefined') {
        set(selectedPriorityListItem, 'selected', false);
        this.set('updateObject.prioritySort', null);
      }
    }

    return selectionAvailableEvaluation;
  },

  /**
   * @name isStatusSelectAvailable
   * @description Checks to see whether or not the status select list is available based on whether or not any incidents have been selected.
   * @param {Object} allIncidents:
   * @public
   */
  @computed('allIncidents.results.@each.checked')
  isStatusSelectAvailable(allIncidents) {
    let checkedBoxes = allIncidents.findBy('checked', true);
    return (typeof checkedBoxes !== 'object');
  },

  /**
   * @name haveValuesChanged
   * @description Checks to see whether or not any values in the select lists have been chosen.
   * @private
   */
@computed('updateObject.{statusSort,prioritySort,assignee}')
  haveValuesChanged() {
    let doesStatusParamExist = !isNone(this.get('updateObject.statusSort'));
    let doesPriorityParamExist = !isNone(this.get('updateObject.prioritySort'));
    let doesAssigneeParamExist = !isNone(this.get('updateObject.assignee'));
    return (doesStatusParamExist || doesPriorityParamExist || doesAssigneeParamExist);
  },

  /**
   * @name updateFilterValue
   * @description updates the `filterProperty` array filter with the `id` and then triggers an update in the incident list
   * @param {String} fieldName: cube's field to filter by
   * @param {String} filterProperty: internal property name (filteredPriorities | filteredStatuses) which collects the values for the specific fieldName
   * @param {Number} id: The field id that will be added or removed from the `filterProperty` array
   * @param {Boolean} addElement: true if the id will be added to the `filterProperty` array. Otherwise it's removed
   * @public
   */
  updateFilterValue(fieldName, filterProperty, id, addElement) {
    if (addElement) {
      this.get(filterProperty).addObject(id);
    } else {
      this.get(filterProperty).removeObject(id);
    }
    this.applyFilters(fieldName, this.get(filterProperty).slice(0));
  },

  /**
   * @name selectedAssignee
   * @description Returns a list of one element with the current assignee id. This is consumed by rsa-form-select
   * @public
   */
  @computed
  selectedAssignee: {
    get: () => [],
    set(values) {
      let editInProgress = (values && values.length > 0 && this.validateEditInProgress());
      if (!editInProgress) {
        run.once(() => {
          this.applyFilters('assigneeId', (values || []).slice());
        });
      }
      this.set('assigneeFilterActive', values.length > 0);
      return values;
    }
  },

  // Obtain beautified (user-friendly) list of sources that can be used in filter selection
  availableSources: IncidentHelper.sourceLongNames(),

  /**
   * @name selectedSources
   * @description List of selected sources used to filter Incidents.
   * @public
   */
  @computed
  selectedSources: {
    get: () => [],
    set(values) {
      // by default no filter is selected. Empty array resets any pre-existing filter
      let filterFn = null;
      if (values.length > 0) {
        // Custom filter function that will be applied to each incident for purpose of determining if that incident
        // has at least one of the selected sources
        filterFn = (incidentSources) => incidentSources.any((source) => values.includes(source));
      }

      let editInProgress = (values.length > 0 && this.validateEditInProgress());
      if (!editInProgress) {
        this.applyFilters('sources', filterFn);
      }
      // Apply CSS style change
      this.set('sourceFilterActive', values.length > 0);
      return values;
    }
  },

  /**
   * @name selectedCategories
   * @description List of selected categories used to filter Incidents. This is consumed by rsa-tag-manager
   * @public
   */
  @computed
  selectedCategories: {
    get: () => [],
    set(values) {
      // by default no filter is selected. Empty array resets any pre-existing filter
      let filterValue = [];
      if (values.length > 0) {
        // if there are categories to apply filters, then we use a function to determinate if the incident has
        // at least on of the selected categories
        // filtering categories is performed by category id or both name and parent.
        filterValue = (incidentCategories) => {
          return values.any((category) => {
            return incidentCategories.any((incidentCat) => {
              return incidentCat.id === category.id || (incidentCat.parent === category.parent && incidentCat.name === category.name);
            });
          });
        };
      }
      let editInProgress = (values && values.length > 0 && this.validateEditInProgress());
      if (!editInProgress) {
        this.applyFilters('categories', filterValue);
      }
      return values;
    }
  },

  /**
   * @name applyFilters
   * @description Filters the incident list cube
   * @param {String} fieldName:
   * @param {String} values:
   * @public
   */
  applyFilters(fieldName, values) {
    let filters = this.get('allIncidents').filters();
    let filter = filters.findBy('field', fieldName);

    if (!values || values.length === 0) {
      filters.removeObject(filter);
    } else {
      if (!filter) {
        filters.addObject({ field: fieldName, value: values });
      } else {
        filter.value = values;
      }
    }

    // Refresh the incidents list based on updated filters
    this.get('allIncidents').filter(filters, true);
  },

  /**
   * @name badgeStyle
   * @description define the badge style based on the incident risk score
   * @public
   */
  badgeStyle(riskScore) {
    return IncidentHelper.riskScoreToBadgeLevel(riskScore);
  },

  /**
   * @name sourceShortName
   * @description returns the source's defined short-name
   * @public
   */
  sourceShortName(source) {
    return IncidentHelper.sourceShortName(source);
  },

  /**
   * @name groupByIp
   * @description Returns a printable version of a IP array based on the input size:
   * - If zero elements or null reference is passed, it returns a '-'
   * - If the array has 1 element, it returns its value
   * - If more than 1 element is in the array, the size of the array is returned
   * @param array
   * @public
   */
  groupByIp: IncidentHelper.groupByIp,

  /**
   * @name toggleSelection
   * @description: Toggles the highlight in the selection on or off depending upon the choice made
   * @param {String} selectedObject: The value or the object from the collection that is selected
   * @param {Object} collection: The collection object
   * @returns {Boolean} The new value of `selectedObject.selected`
   * @private
   */
  toggleSelection(selectionValue, collection) {
    let areTheSameSelection,
      selectedObject,
      previousSelected,
      newSelectedValue;


    selectedObject = (typeof selectionValue != 'object') ?
      collection.findBy('id', selectionValue) :
      collection.findBy('id', selectionValue.id);

    previousSelected = collection.findBy('selected', true);

    if (previousSelected) {
      areTheSameSelection = previousSelected.id === selectedObject.id;

      if (!areTheSameSelection) {
        set(previousSelected, 'selected', false);
      }

      newSelectedValue = !areTheSameSelection;
    } else {
      newSelectedValue = true;
    }

    set(selectedObject, 'selected', newSelectedValue);
    return newSelectedValue;
  },

  /**
   * @name resetSelectionForm
   * @description: Resets the selection forms in the bulk edit.
   * @public
   */
  resetSelectionForm() {
    let selectedStatusListItem = this.get('statusList').findBy('selected', true);
    let selectedPriorityListItem = this.get('priorityList').findBy('selected', true);
    let selectedUsersListItem = this.get('usersList').findBy('selected', true);

    if (typeof selectedStatusListItem !== 'undefined') {
      set(selectedStatusListItem, 'selected', false);
    }

    if (typeof selectedPriorityListItem !== 'undefined') {
      set(selectedPriorityListItem, 'selected', false);
    }

    if (typeof selectedUsersListItem !== 'undefined') {
      set(selectedUsersListItem, 'selected', false);
    }

    this.set('updateObject', {});
    this.toggleAllCheckboxes(false);
  },

  /**
   * @name toggleAllCheckboxes
   * @description: Toggles all the checkboxes on or off depending upon their current state or if a value for forcedValue is passed in.
   * @param {Boolean} forcedValue: A value for forcing all of the select boxes on or off.
   * @public
   */
  toggleAllCheckboxes(forcedValue) {
    let newValue = forcedValue;
    let allIncidents = this.get('allIncidents.results');

    if (typeof forcedValue !== 'boolean') {
      newValue = this.toggleProperty('headerCheckbox');
      this.set('showSuccessMessage', false);
    } else {
      this.set('headerCheckbox', false);
    }
    allIncidents.setEach('checked', newValue);
  },

  toggleSuccessMessage(totalCheckedEvents) {
    let self = this;
    this.set('totalFieldsUpdated', totalCheckedEvents);
    this.set('showSuccessMessage', true);

    run.later((function() {
      self.set('showSuccessMessage', false);
    }), 15000);
  },

  actions: {
    // sets the current sorted column field name and the sort direction
    // and calls the, sortAction in the route to do the actual sort for list view
    sortListView(column, direction) {
      column.set('isDescending', (direction === 'desc'));
      this.set('currentSort', column.field);
      this.sendAction('sortAction', column.field, direction, viewType.LIST_VIEW);
    },

    /**
     * @name resetFilters
     * @description Set all the filter options back to default values
     * @public
     */
    resetFilters() {
      this.get('statusList').setEach('value', false);
      this.get('priorityList').setEach('value', false);
      this.setProperties({
        'selectedAssignee': [],
        'selectedSources': [],
        'selectedCategories': []
      });
    },

    /**
     * @name handleCancelButton
     * @description Callback event handler for the cancel button
     * @public
     */
    handleCancelButton() {
      this.resetSelectionForm();
    },

    /**
     * @name handleHeaderCheckboxClick
     * @description by clicking on the checkbox in the header, all of the other checkboxes will be selected
     * @public
     */
    handleHeaderCheckboxClick(forcedValue) {
      this.toggleAllCheckboxes(forcedValue);
    },

    /**
     * @name toggleCheckBox
     * @description by clicking on the checkbox in the header, all of the other checkboxes will be selected
     * @param {String} itemID: The ID of the incident that was checked.
     * @public
     */
    toggleCheckBox(itemID) {
      let specificIncident = this.get('allIncidents.results').findBy('id', itemID);
      set(specificIncident, 'checked', !specificIncident.checked);

      let totalCheckedIncidents = this.get('data.allIncidents.results').filterBy('checked', true);
      let totalUncheckedIncidents = this.get('data.allIncidents.results').filterBy('checked', false);

      this.set('showSuccessMessage', false);

      if (totalCheckedIncidents.length === 0) {
        this.resetSelectionForm();
      } else {
        if (totalUncheckedIncidents.length > 0) {
          this.set('headerCheckbox', false);
        }
      }
    },

    /**
     * @name updateSelectElement
     * @description Updates the updateObject according to which select list was changed.
     * If the value that is currently stored is the same as the new value selected,
     * then the value is removed from the updateObject.
     * @param {String} selectionValue: The value of the selected field
     * @param {String} fieldName: The name of the field to be updated
     * @param {Object} collection
     * @public
     */
    updateSelectElement(selectionValue, fieldName, collection) {
      let newSelectedValue;
      let isEmberClass = typeof selectionValue.get === 'function';

      switch (fieldName) {
        case 'prioritySort':
        case 'statusSort':
          newSelectedValue = this.toggleSelection(selectionValue, collection);
          this.set(`updateObject.${ fieldName }`, newSelectedValue ? selectionValue : null);
          break;
        case 'assignee':
          selectionValue = {
            'id': (isEmberClass) ? selectionValue.get('id') : selectionValue.id,
            'firstName': (isEmberClass) ? selectionValue.get('firstName') : selectionValue.firstName,
            'lastName': (isEmberClass) ? selectionValue.get('lastName') : selectionValue.lastName,
            'email': (isEmberClass) ? selectionValue.get('email') : selectionValue.email
          };

          newSelectedValue = this.toggleSelection(selectionValue, this.get('usersList'));
          this.set(`updateObject.${ fieldName }`, newSelectedValue ? selectionValue : null);
          break;
        default:
          break;
      }
    },

    /**
     * @name validateBulkSave
     * @description Validates whether or not a bullk save action is possible and if so, invokes the appropriate route-action.
     * @public
     */
    validateBulkSave() {
      let checkedIncidents = this.get('data.allIncidents.results').filterBy('checked', true);
      let arrayOfSelectedIncidentIDs = checkedIncidents.map(function(item) {
        return item.id;
      });

      // There are three potential permutations that this code needs to deal with:
      // 1) this.get('updateObject').assignee.id === null AND this.get('updateObject').assignee.id is undefined, delete the paramter.
      // 2) this.get('updateObject').assignee.id === -1, set its value to NULL.
      // 3) this.get('updateObject').assignee.id is defined, do nothing.
      if (this.get('updateObject.assignee')) {
        this.get('updateObject.assignee.id') === -1 ? this.set('updateObject.assignee', null) : '';
      } else {
        delete this.get('updateObject').assignee;
      }

      // We cannot currently pass in a null value; we must remove the parameter altogether.
      if (
        typeof this.get('updateObject').statusSort !== 'undefined' &&
        this.get('updateObject').statusSort === null
      ) {
        delete this.get('updateObject').statusSort;
      }

      // We cannot currently pass in a null value; we must remove the parameter altogether.
      if (
        typeof this.get('updateObject').prioritySort !== 'undefined' &&
        this.get('updateObject').prioritySort === null
      ) {
        delete this.get('updateObject').prioritySort;
      }

      if (Object.keys(this.get('updateObject')).length > 0) {
        this.sendAction('bulkSaveAction', this.get('updateObject'), arrayOfSelectedIncidentIDs);
        this.toggleSuccessMessage(checkedIncidents.length);
        this.resetSelectionForm();
      }
    },

    /**
     * @name handleRowClick
     * @description Handles the event for the row click
     * @param {String} columnDataType
     * @param {Object} incidentObject
     * @public
     */
    handleRowClick(columnDataType, incidentObject) {
      if (columnDataType !== 'checkbox') {
        this.resetSelectionForm();
        this.sendAction('gotoIncidentDetail', incidentObject);
      }
    },

    /**
     * @name openBulkEditModal
     * @description Opens the Unsaved Changes bulk edit modal
     * @public
     */
    openBulkEditModal() {
      this.get('eventBus').trigger('rsa-application-modal-open-bulk-edit-changes');
    },

    /**
     * @name closeBulkEditModal
     * @description Closes the Unsaved Changes bulk edit modal
     * @public
     */
    closeBulkEditModal() {
      this.get('eventBus').trigger('rsa-application-modal-close-bulk-edit-changes');
      this.send('resetFilters');
    }
  }
});
