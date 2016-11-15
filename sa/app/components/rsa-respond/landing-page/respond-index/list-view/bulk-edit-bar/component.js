import Ember from 'ember';
import IncidentConstants from 'sa/incident/constants';
import computed from 'ember-computed-decorators';

const {
  Component,
  run,
  set,
  typeOf
} = Ember;

export default Component.extend({
  tagName: 'hbox',
  classNames: ['bulk-edit-bar'],

  // Value used to update the success message for the bulk edit
  totalFieldsUpdated: 0,

  // Boolean which determines whether or not the success message should show
  showSuccessMessage: false,

  updateObject: {},

  init() {
    this._super(...arguments);
    this.set('updateObject', {});
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
  @computed('updateObject', 'updateObject.{statusSort,prioritySort,assignee}')
  isBulkEditInProgress: {
    get(updateObject) {
      return Object.keys(updateObject).length > 0;
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
    const unAssigneeUser = {
      'id': -1
    };
    const arrUsers = [ unAssigneeUser ];

    if (users) {
      arrUsers.addObjects(users);
    }

    arrUsers.setEach('selected', false);
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

    if (selectionAvailableEvaluation) {
      const selectedUsersListItem = this.get('usersList').findBy('selected', true);
      const selectedPriorityListItem = this.get('priorityList').findBy('selected', true);

      if (typeOf(selectedUsersListItem) !== 'undefined') {
        set(selectedUsersListItem, 'selected', false);
        this.set('updateObject.assignee', null);
      }

      if (typeOf(selectedPriorityListItem) !== 'undefined') {
        set(selectedPriorityListItem, 'selected', false);
        this.set('updateObject.prioritySort', null);
      }
    }

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
   * @name toggleEditSelection
   * @description: Toggles the highlight in the selection on or off depending upon the choice made
   * @param {String} selectedObject: The value or the object from the collection that is selected
   * @param {Object} collection: The collection object
   * @returns {Boolean} The new value of `selectedObject.selected`
   * @private
   */
  toggleEditSelection(selectionValue, collection) {
    let areTheSameSelection, newSelectedValue;

    const selectedObject = (typeof selectionValue != 'object') ?
      collection.findBy('id', selectionValue) :
      collection.findBy('id', selectionValue.id);

    const previousSelected = collection.findBy('selected', true);

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
    const selectedStatusListItem = this.get('statusList').findBy('selected', true);
    const selectedPriorityListItem = this.get('priorityList').findBy('selected', true);
    const selectedUsersListItem = this.get('usersList').findBy('selected', true);

    if (typeOf(selectedStatusListItem) !== 'undefined') {
      set(selectedStatusListItem, 'selected', false);
    }

    if (typeOf(selectedPriorityListItem) !== 'undefined') {
      set(selectedPriorityListItem, 'selected', false);
    }

    if (typeOf(selectedUsersListItem) !== 'undefined') {
      set(selectedUsersListItem, 'selected', false);
    }

    this.set('updateObject', {});
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
     * @name handleCancelButton
     * @description Callback event handler for the cancel button
     * @public
     */
    handleCancelButton() {
      this.resetSelectionForm();
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
      const isEmberClass = typeof selectionValue.get === 'function';

      switch (fieldName) {
        case 'prioritySort':
        case 'statusSort':
          newSelectedValue = this.toggleEditSelection(selectionValue, collection);
          this.set(`updateObject.${ fieldName }`, newSelectedValue ? selectionValue : null);
          break;
        case 'assignee':
          selectionValue = {
            'id': (isEmberClass) ? selectionValue.get('id') : selectionValue.id,
            'firstName': (isEmberClass) ? selectionValue.get('firstName') : selectionValue.firstName,
            'lastName': (isEmberClass) ? selectionValue.get('lastName') : selectionValue.lastName,
            'email': (isEmberClass) ? selectionValue.get('email') : selectionValue.email
          };

          newSelectedValue = this.toggleEditSelection(selectionValue, this.get('usersList'));
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
      const checkedIncidents = this.get('incidents').filterBy('checked', true);
      const arrayOfSelectedIncidentIDs = checkedIncidents.map(function(item) {
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
      if (typeOf(this.get('updateObject').statusSort) !== 'undefined' &&
        this.get('updateObject').statusSort === null
      ) {
        delete this.get('updateObject').statusSort;
      }

      // We cannot currently pass in a null value; we must remove the parameter altogether.
      if (typeOf(this.get('updateObject').prioritySort) !== 'undefined' &&
        this.get('updateObject').prioritySort === null
      ) {
        delete this.get('updateObject').prioritySort;
      }

      if (Object.keys(this.get('updateObject')).length > 0) {
        this.sendAction('saveAction', this.get('updateObject'), arrayOfSelectedIncidentIDs);
        this.toggleSuccessMessage(checkedIncidents.length);
        this.resetSelectionForm();
      }
    }
  }
});
