import Ember from 'ember';
import IncidentHelper from 'sa/incident/helpers';
import { viewType } from 'sa/protected/respond/index/route';
import IncidentConstants from 'sa/incident/constants';
import computed, { equal } from 'ember-computed-decorators';
import { availableColumnsConfig, replayConfig, persistenceConfig, storageKey } from './config';
import ReplayManager from 'sa/utils/replay-manager';
import PersistenceManager from 'sa/utils/persistence-manager';

const {
  Logger,
  isPresent,
  isNone,
  Component,
  inject: {
    service
  },
  run,
  set,
  computed: emberComputed,
  Object: EmberObject
} = Ember;

const riskScoreFilterInitialValues = [0, 100];

export default Component.extend({
  tagName: 'hbox',

  timeFormat: service(),

  allIncidentsSelected: false,

  // disables the persitance save and restore. Used in tests.
  disablePersistence: false,

  // Number of buffered items to be rendered by data table
  // 0 is the default, but we override this in integration tests
  buffer: 0,

  // default sorted field for list view
  currentSort: 'riskScore',

  // Value used to update the success message for the bulk edit
  totalFieldsUpdated: 0,

  // Boolean which determines whether or not a bulk edit is in progress
  isBulkEditInProgress: false,

  // Array of selected priorities
  filteredPriorities: [],

  // Array of selected statuses
  filteredStatuses: [],

  // Array of selected categories
  filteredCategories: [],

  // Array of risk score values
  filteredRiskScores: [],

  // Full list of available columns to display
  availableColumnsConfig,

  // Replay Manager
  replay: null,

  // Persistence Manager
  persistence: null,

  // All Date/Time filter options
  dateTimeFilterOptions: IncidentConstants.dateTimeFilterOptions,

  // Boolean flag used to show or hide calender for date/time filtering
  showCustomDateTime: false,

  // Object consumed by the template to reflect date/time calender state
  startDate: {
    isError: false,
    startDateValue: null,
    errorMsg: null
  },

  // Object consumed by the template to reflect date/time calender state
  endDate: {
    isError: false,
    endDateValue: null,
    errorMsg: null
  },

  init() {
    this._super(...arguments);
    this.setProperties({
      'filteredTime': {},
      'filteredPriorities': [],
      'filteredStatuses': [],
      'filteredCategories': [],
      'filteredRiskScores': riskScoreFilterInitialValues
    });

    this.set('isBulkEditInProgress', false);
    if (!this.get('disablePersistence')) {
      this.setProperties({
        'replay': ReplayManager.create(),
        'persistence': PersistenceManager.create({ storageKey })
      });
      this.set('persistence.persistables', persistenceConfig.call(this));
      this.set('replay.replayables', replayConfig.call(this));
    }
  },

  didReceiveAttrs() {
    this._super(...arguments);
    if (!this.get('disablePersistence')) {
      if (this.get('persistence.state.isReplayable')) {
        this.set('replay.restoredState', this.get('persistence.state.value'));
        if (this.get('replay.isReady')) {
          this.get('replay').replay();
        }
      } else {
        this.get('persistence').persistInitialState();
      }

      this.get('persistence').destroyObservers();
      this.get('persistence').createObservers();
    }
  },

  /*
  Removes persistence observers
  */
  willDestroyElement() {
    this._super(...arguments);
    if (!this.get('disablePersistence')) {
      this.get('persistence').destroyObservers();
    }
  },

  // Initial risk score values
  riskScoreStart: riskScoreFilterInitialValues,

  // risk score filter value range
  riskScoreFilterRange: { 'min': [0], 'max': [100] },

  @computed('categoryTags.[]')
  normalizedTreeData: (categoryTags) => IncidentHelper.normalizeCategoryTags(categoryTags),

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
   * @name headerCheckbox
   * @description The boolean which determines whether or not the checkbox in the header row is checked
   * @public
   */
  @computed('allIncidents.results.@each.checked')
  headerCheckbox: {
    get() {
      const totalIncidentsLength = this.get('allIncidents.results.length');
      const selectedIncident = this.get('allIncidents.results').findBy('checked', true);

      let isHeaderSelected = false;
      if (selectedIncident) {
        // when all the incidents on screen are selected we also select the header. Any other case, the header gets deselected
        const allSelectedIncidents = this.get('allIncidents.results').filterBy('checked', true);
        isHeaderSelected = (allSelectedIncidents.length === totalIncidentsLength);
      }
      return isHeaderSelected;
    },
    set(value) {
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
  @computed('filteredStatuses')
  statusList(statuses) {
    const statusArray = IncidentConstants.incidentStatusIds.map((statusId) => {
      return EmberObject.extend({
        id: statusId,
        value: emberComputed({
          get: () => statuses.includes(statusId),
          set: (key, value) => {

            run.once(() => {
              this.updateFilterValue('status', 'filteredStatuses', statusId, value);
            });
            return value;
          }
        })
      }).create();
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
  @computed('filteredPriorities')
  priorityList(priorities) {
    const priorityArray = IncidentConstants.incidentPriorityIds.map((priorityId) => {
      return EmberObject.extend({
        id: priorityId,
        value: emberComputed({
          get: () => priorities.includes(priorityId),
          set: (key, value) => {
            run.once(() => {
              this.updateFilterValue('priority', 'filteredPriorities', priorityId, value);
            });
            return value;
          }
        })
      }).create();
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
   * @name selectedAssignees
   * @description Returns a list of one element with the current assignee id. This is consumed by ember-power-select
   * @public
   */
  @computed
  selectedAssignees: {
    get: () => [],
    set(values) {
      const assigneeIds = values.map((assignee) => assignee.id);
      run.once(() => {
        this.applyFilters('assigneeId', assigneeIds.slice());
      });
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
      run.once(() => {
        this.applyFilters('sources', filterFn);
      });
      return values;
    }
  },

  /**
   * @name use24hour
   * @description boolean flag to be consumed by the template for custom date/time display
   * @param key
   * @public
   */
  @equal('timeFormat.selected.key', 'HR24')
  use24hour: null,

  /**
   * @name selectedCategories
   * @description List of selected categories used to filter Incidents. This is consumed by rsa-tag-manager
   * @public
   */
  @computed
  selectedCategories: {
    get: () => [],
    set(values) {
      run.once(() => {
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

        this.set('filteredCategories', values);
        this.applyFilters('categories', filterValue);
      });
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
    const filters = this.get('allIncidents').filters();
    const filter = filters.findBy('field', fieldName);

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
   * this method applies filter by risk score. It's used by the slider's callback method.
   * Since it's called by a run.debounce method, it checks if the component is not destroyed.
   * @private
   */
  _filterByRiskScore(values) {
    if (!this.get('isDestroyed') && !this.get('isDestroying')) {
      this.set('filteredRiskScores', values);
      // cube's range filter doesn't include the `to` value.
      // eg: (val >= from && val < to);
      this.applyFilters('riskScore', { from: values[0], to: (values[1] + 1) });
    }
  },

  /**
   * @name sourcesList
   * @description Obtain beautified (user-friendly) list of sources that can be used in filter selection
   * @public
   */
  sourcesList: IncidentHelper.sourceLongNames(),

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
    const allIncidents = this.get('allIncidents.results');

    if (typeof forcedValue !== 'boolean') {
      newValue = this.toggleProperty('headerCheckbox');
    }
    allIncidents.setEach('checked', newValue);
  },

  /**
   * @name selectedDateFilterOption
   * @description Computed property that handles time filtering
   * @param filter Object containing information about time filter options selected by the user
   * @returns {string}
   * @public
   */
  @computed('filteredTime')
  selectedDateFilterOption(filter) {
    // Set default option
    let option = IncidentConstants.dateTimeOptions.ALL_DATA;
    if (isNone(filter.option)) {
      filter = { option };
    } else {
      option = filter.option;
    }

    this._filterByDateTime(filter);
    return option;
  },

  /**
   * @name _filterByDateTime
   * @description Filter by time based on selected option
   * Note: Custom option is removed (see IncidentConstants) until rsa-form-datetime component's bug fixes are addressed.
   * @param filter Object containing information about custom start and end time
   * @private
   */
  _filterByDateTime(filter) {

    const { option } = filter;

    // Update Date/Time filter view
    this.set('showCustomDateTime', option === IncidentConstants.dateTimeOptions.CUSTOM);

    // Reset custom time filter when custom filter is not selected
    if (option !== IncidentConstants.dateTimeOptions.CUSTOM) {
      this.setProperties({
        'startDate.startDateValue': null,
        'startDate.isError': false,
        'endDate.endDateValue': null,
        'endDate.isError': false
      });
    }

    // Filter by selection using UTC time in milliseconds since 1 January 1970
    let endTime = Date.now();

    // Compute startTime based on selected option
    let startTime = null;

    switch (option) {
      case IncidentConstants.dateTimeOptions.CUSTOM:

        // Display current selection inside date-time component
        if (isPresent(filter.startDateValue)) {
          this.set('startDate.startDateValue', filter.startDateValue);
        }
        if (isPresent(filter.endDateValue)) {
          this.set('endDate.endDateValue', filter.endDateValue);
        }

        // Apply filter if selected start and end times are valid
        if (!this.get('startDate.isError') && !this.get('endDate.isError')) {
          endTime = filter.endTime;
          startTime = filter.startTime;
        }
        break;
      case IncidentConstants.dateTimeOptions.TODAY: {
        const currentDate = new Date();
        const year = currentDate.getFullYear();
        const month = currentDate.getUTCMonth();
        const day = currentDate.getUTCDate();
        startTime = Date.UTC(year, month, day, 0, 0, 0, 0);
        break;
      }
      case IncidentConstants.dateTimeOptions.LAST_HOUR: {
        startTime = endTime - 3600000;
        break;
      }
      case IncidentConstants.dateTimeOptions.LAST_12_HOURS: {
        startTime = endTime - 43200000;
        break;
      }
      case IncidentConstants.dateTimeOptions.LAST_24_HOURS: {
        startTime = endTime - 86400000;
        break;
      }
      case IncidentConstants.dateTimeOptions.LAST_7_DAYS: {
        startTime = endTime - 604800000;
        break;
      }
      case IncidentConstants.dateTimeOptions.ALL_DATA: {
        startTime = null;
        break;
      }
      default: {
        Logger.logger(`Unexpected option selected. option: ${option}`);
        startTime = null;
      }
    }

    run.once(() => {
      if (startTime && endTime) {
        this.applyFilters('dateCreated', { from: startTime, to: endTime });
      } else if (option !== IncidentConstants.dateTimeOptions.CUSTOM) {
        this.applyFilters('dateCreated', null);
      }
    });
  },

  /**
   * @name _validateCustomTimeFilter
   * @description Evaluate selected custom start and end time.
   * Validity checks:
   * 1. Start and end time cannot be set in the future (ie. greater than current time).
   * 2. Start time must be less than end time.
   * 3. End time must be greater than start time.
   * @param updatedFilter
   * @private
   */
  _validateCustomTimeFilter(updatedFilter) {

    const { endTime, startTime } = updatedFilter;
    const currentTime = Date.now();

    let isValidStartTime = true;
    let isValidEndTime = true;

    // Do not allow future date selection.
    if (startTime > currentTime) {
      this._showStartTimeError(this.get('i18n').t('incident.list.filters.dateTimeFilterError'));
      isValidStartTime = false;
    }

    if (endTime > currentTime) {
      this._showEndTimeError(this.get('i18n').t('incident.list.filters.dateTimeFilterError'));
      isValidEndTime = false;
    }

    if (isValidStartTime && isValidEndTime) {

      // Start date cannot be greater than end date.
      if (startTime > endTime) {
        this._showStartTimeError(this.get('i18n').t('incident.list.filters.dateTimeFilterStartError'));
        isValidStartTime = false;
      }

      // End date cannot be less than start date.
      if (endTime < startTime) {
        this._showEndTimeError(this.get('i18n').t('incident.list.filters.dateTimeFilterEndError'));
        isValidEndTime = false;
      }

    }

    // Clear Start time error state
    if (isValidStartTime) {
      this.set('startDate.isError', false);
    }

    // Clear Start time error state
    if (isValidEndTime) {
      this.set('endDate.isError', false);
    }

    // Setting 'filteredTime' property triggers computed property that applies time filter
    this.set('filteredTime', updatedFilter);
  },

  /**
   * @name _showStartTimeError
   * @description update startDate object which is consumed by the template to update the view.
   * @param msg
   * @private
   */
  _showStartTimeError(msg) {
    this.setProperties({
      'startDate.isError': true,
      'startDate.errorMsg': msg
    });
  },

  /**
   * @name _showEndTimeError
   * @description update endDate object which is consumed by the template to update the view.
   * @param msg
   * @private
   */
  _showEndTimeError(msg) {
    this.setProperties({
      'endDate.isError': true,
      'endDate.errorMsg': msg
    });
  },


  actions: {

    /**
     * @name onCustomStartDateSelected
     * @description Process user's selection of custom start date
     * @param date
     * @public
     */
    onCustomStartDateSelected(date) {
      // Date must be selected (date-time component could return null)
      if (isNone(date)) {
        return;
      }

      const selectedStartTime = date.getTime();
      const filteredTime = this.get('filteredTime');
      const updatedFilter = {
        option: filteredTime.option,
        startTime: selectedStartTime,
        startDateValue: this.get('startDate.startDateValue'),
        endTime: filteredTime.endTime,
        endDateValue: filteredTime.endDateValue
      };

      this._validateCustomTimeFilter(updatedFilter);

    },

    /**
     * @name onCustomEndDateSelected
     * @description Process user's selection of custom end date
     * @param date
     * @public
     */
    onCustomEndDateSelected(date) {
      // Date must be selected (date-time component could return null)
      if (isNone(date)) {
        return;
      }

      const selectedEndTime = date.getTime();
      const filteredTime = this.get('filteredTime');
      const updatedFilter = {
        option: filteredTime.option,
        startTime: filteredTime.startTime,
        startDateValue: filteredTime.startDateValue,
        endTime: selectedEndTime,
        endDateValue: this.get('endDate.endDateValue')
      };

      this._validateCustomTimeFilter(updatedFilter);

    },

    /**
     * @name onDateTimeOptionSelected
     * @description Set new value for filteredTime based on selected option.
     * @public
     */
    onDateTimeOptionSelected(option) {
      // Setting 'filteredTime' property triggers computed property that applies time filter
      this.set('filteredTime', { option });
    },

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
        'selectedAssignees': [],
        'selectedSources': [],
        'selectedCategories': [],
        'riskScoreStart': riskScoreFilterInitialValues.slice()
      });
      // Setting 'filteredTime' property triggers computed property that applies time filter
      this.set('filteredTime', { option: IncidentConstants.dateTimeOptions.ALL_DATA });

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
      const specificIncident = this.get('allIncidents.results').findBy('id', itemID);
      set(specificIncident, 'checked', !specificIncident.checked);
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
     * Updates the filter based on the selected risk scores
     * Because the callback is called many times (at least 2 - one per each handler) by the sliders, we are using `run.debouce`
     * to only apply the filter once.
     * @public
     */
    onRiskScoreFilterUpdate(values) {
      run.debounce(this, this._filterByRiskScore, values, 200);
    },

    togglePriority(priority) {
      priority.toggleProperty('value');
    },

    toggleStatus(status) {
      status.toggleProperty('value');
    },

    toggleAllIncidents(incidents) {
      if (this.get('allIncidentsSelected')) {
        incidents.setEach('checked', false);
      } else {
        incidents.setEach('checked', true);
      }

      this.toggleProperty('allIncidentsSelected');
    }
  }
});
