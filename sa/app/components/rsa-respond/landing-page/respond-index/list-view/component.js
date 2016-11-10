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
  set
} = Ember;

export default Component.extend({
  tagName: 'hbox',
  eventBus: service(),

  // Number of buffered items to be rendered by data table
  // 0 is the default, but we override this in integration tests
  buffer: 0,

  // default sorted field for list view
  currentSort: 'riskScore',

  // Value used to update the success message for the bulk edit
  totalFieldsUpdated: 0,

  // Boolean which determines whether or not a bulk edit is in progress
  isBulkEditInProgress: false,

  // Boolean used hide the bulk edit success message
  showBulkEditMessage: false,

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

    this.set('isBulkEditInProgress', false);
  },

  @computed('categoryTags.[]')
  normalizedTreeData: (categoryTags) => IncidentHelper.normalizeCategoryTags(categoryTags),

  /**
   * @name headerCheckbox
   * @description The boolean which determines whether or not the checkbox in the header row is checked
   * @public
   */
  @computed('allIncidents.results.@each.checked')
  headerCheckbox: {
    get() {
      let totalIncidentsLength = this.get('allIncidents.results.length');
      let selectedIncident = this.get('allIncidents.results').findBy('checked', true);

      let isHeaderSelected = false;
      if (selectedIncident) {
        // when all the incidents on screen are selected we also select the header. Any other case, the header gets deselected
        let allSelectedIncidents = this.get('allIncidents.results').filterBy('checked', true);
        isHeaderSelected = (allSelectedIncidents.length === totalIncidentsLength);
      }
      return isHeaderSelected;
    },
    set(value) {
      this.set('showBulkEditMessage', false);
      this.toggleAllCheckboxes(value);
      return value;
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
            let editInProgress = value && this.get('isBulkEditInProgress');

            if (!editInProgress) {
              this.updateFilterValue('status', 'filteredStatuses', statusId, value);
              return value;
            } else {
              this.openBulkEditModal();
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
            let editInProgress = value && this.get('isBulkEditInProgress');

            if (!editInProgress) {
              this.updateFilterValue('priority', 'filteredPriorities', priorityId, value);
              return value;
            } else {
              this.openBulkEditModal();
            }
          }
        })
      };
    });
    return priorityArray;
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
      let editInProgress = (values && values.length > 0 && this.get('isBulkEditInProgress'));

      if (!editInProgress) {
        run.once(() => {
          this.applyFilters('assigneeId', (values || []).slice());
        });
      } else {
        this.openBulkEditModal();
      }
      this.set('assigneeFilterActive', values.length > 0);
      return values;
    }
  },

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

      let editInProgress = values.length > 0 && this.get('isBulkEditInProgress');

      if (!editInProgress) {
        this.applyFilters('sources', filterFn);
      } else {
        this.openBulkEditModal();
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

      let editInProgress = values && values.length > 0 && this.get('isBulkEditInProgress');

      if (!editInProgress) {
        this.applyFilters('categories', filterValue);
      } else {
        this.openBulkEditModal();
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
   * @name availableSources
   * @description Obtain beautified (user-friendly) list of sources that can be used in filter selection
   * @public
   */
  availableSources: IncidentHelper.sourceLongNames(),

  /**
   * @name badgeStyle
   * @description define the badge style based on the incident risk score
   * @public
   */
  badgeStyle: (riskScore) => IncidentHelper.riskScoreToBadgeLevel(riskScore),

  /**
   * @name sourceShortName
   * @description returns the source's defined short-name
   * @public
   */
  sourceShortName: (source) => IncidentHelper.sourceShortName(source),

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
    }
    allIncidents.setEach('checked', newValue);
  },

  /**
   * @name openBulkEditModal
   * @description Opens the Unsaved Changes bulk edit modal
   * @public
   */
  openBulkEditModal() {
    this.get('eventBus').trigger('rsa-application-modal-open-bulk-edit-changes');
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
      this.set('showBulkEditMessage', false);
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
        this.toggleAllCheckboxes(false);
        this.sendAction('gotoIncidentDetail', incidentObject);
      }
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
