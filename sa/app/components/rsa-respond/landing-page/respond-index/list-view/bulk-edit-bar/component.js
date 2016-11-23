import Ember from 'ember';
import IncidentConstants from 'sa/incident/constants';
import computed from 'ember-computed-decorators';

const {
  Component,
  run,
  get,
  typeOf,
  isNone,
  Object: EmberObject
} = Ember;

export default Component.extend({
  tagName: 'hbox',
  classNames: ['bulk-edit-bar'],

  // Value used to update the success message for the bulk edit
  totalFieldsUpdated: 0,

  // Boolean which determines whether or not the success message should show
  showSuccessMessage: false,

  // power-select selected options
  selected: {},

  init() {
    this._super(...arguments);
    this.set('selected', {});
  },

  // available list of statuses
  statusList: IncidentConstants.incidentStatusIds.map((statusId) => ({ id: statusId, selected: false })),

  // available list of priorities
  priorityList: IncidentConstants.incidentPriorityIds.map((priorityId) => ({ id: priorityId, selected: false })),

  /**
   * @name isBulkEditInProgress
   * @description Checks to see whether or not any values in the select lists have been chosen.
   * @public
   */
  @computed('selected.{statusSort,prioritySort,assignee}')
  isBulkEditInProgress: {
    get(selectedStatus, selectedPriority, selectedAssignee) {
      return !isNone(selectedStatus) || !isNone(selectedPriority) || !isNone(selectedAssignee);
    },
    set(value) {
      return value;
    }
  },

  /**
   * @name usersList
   * @description Creates an array of the user object and adds the selected parameter to each with the value of false.
   * @param {Object} users Current users object from the model
   * @public
   */
  @computed('users.[]')
  usersList(users) {
    const unassignedUser = EmberObject.create({
      'id': -1
    });
    const arrUsers = [ unassignedUser ];

    if (users) {
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
   * @param {Object} incidents:
   * @public
   */
  @computed('incidents.@each.checked')
  isSelectAvailable(incidents) {
    const checkedIncidents = incidents.filterBy('checked', true);
    const arrayOfSelectedIncidentIDs = checkedIncidents.map(function(item) {
      return item.id;
    });
    const foundClosedItem = checkedIncidents.findBy('statusSort', IncidentConstants.incStatus.CLOSED);
    const foundFalsePositiveItem = checkedIncidents.findBy('statusSort', IncidentConstants.incStatus.CLOSED_FALSE_POSITIVE);
    const selectionAvailableEvaluation = (
        arrayOfSelectedIncidentIDs.length === 0 ||
        typeOf(foundClosedItem) !== 'undefined' ||
        typeOf(foundFalsePositiveItem) !== 'undefined'
    );

    return selectionAvailableEvaluation;
  },

  /**
   * @name isStatusSelectorDisabled
   * @description Checks to see whether or not the status select list is available based on whether or not any incidents have been selected.
   * @param {Object} incidents:
   * @public
   */
  @computed('incidents.@each.checked')
  isStatusSelectorDisabled(incidents) {
    const selectedIncident = incidents.findBy('checked', true);
    const noIncidentSelected = typeOf(selectedIncident) === 'undefined';
    if (noIncidentSelected) {
      this.resetSelectionForm();
    }
    return noIncidentSelected;
  },

  /**
   * @name resetSelectionForm
   * @description: Resets the selection forms in the bulk edit.
   * @public
   */
  resetSelectionForm() {
    this.setProperties({
      'selected.prioritySort': null,
      'selected.statusSort': null,
      'selected.assignee': null
    });

    this.get('incidents').setEach('checked', false);
  },

  toggleSuccessMessage(totalCheckedEvents) {
    this.set('totalFieldsUpdated', totalCheckedEvents);
    this.set('showSuccessMessage', true);

    run.later(() => {
      if (!this.isDestroyed) {
        this.set('showSuccessMessage', false);
      }
    }, 15000);
  },

  actions: {
    /**
     * @name cancelEdit
     * @description Callback event handler for the cancel button
     * @public
     */
    cancelEdit() {
      this.resetSelectionForm();
    },

    /**
     * @name selectElement
     * @description Updates the select object according to which select list was changed.
     * @param {String} selectionValue: The value of the selected field
     * @param {String} fieldName: The name of the field to be updated
     * @param {Object} collection
     * @public
     */
    selectElement(fieldName, collection, selectionValue) {
      let selectedId = null;
      let selectedObj = null;

      if (!isNone(selectionValue)) {
        selectedId = get(selectionValue, 'id');
        selectedObj = collection.findBy('id', selectedId);
      }
      this.set(`selected.${ fieldName }`, selectedObj);
    },

    /**
     * @name validateBulkSave
     * @description Validates whether or not a bullk save action is possible and if so, invokes the appropriate route-action.
     * @public
     */
    validateBulkSave() {
      const checkedIncidents = this.get('incidents').filterBy('checked', true);
      const arrayOfSelectedIncidentIDs = checkedIncidents.map((item) => item.id);
      const updateObject = {};

      const selectedValues = this.get('selected');
      const priorityId = get(selectedValues, 'prioritySort.id');
      const statusId = get(selectedValues, 'statusSort.id');
      const assignee = get(selectedValues, 'assignee');

      if (!isNone(priorityId)) {
        updateObject.prioritySort = priorityId;
      }
      if (!isNone(statusId)) {
        updateObject.statusSort = statusId;
      }
      if (!isNone(assignee)) {
        updateObject.assignee = get(assignee, 'id') !== -1 ? selectedValues.assignee : {};
      }

      this.sendAction('saveAction', updateObject, arrayOfSelectedIncidentIDs);
      this.toggleSuccessMessage(checkedIncidents.length);
      this.resetSelectionForm();
    }
  }
});
