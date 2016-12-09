import Ember from 'ember';
import IncidentHelper from 'sa/incident/helpers';
import { viewType } from 'sa/protected/respond/index/route';
import IncidentConstants from 'sa/incident/constants';
import computed from 'ember-computed-decorators';
import { availableColumnsConfig, replayConfig, persistenceConfig, storageKey } from './config';
import ReplayManager from 'sa/utils/replay-manager';
import PersistenceManager from 'sa/utils/persistence-manager';

const {
  inject: {
    service
  },
  Component,
  run,
  computed: emberComputed,
  set,
  Object: EmberObject
} = Ember;

const riskScoreFilterInitialValues = [0, 100];

export default Component.extend({
  tagName: 'hbox',
  eventBus: service(),

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

  // Boolean used hide the bulk edit success message
  showBulkEditMessage: false,

  // Array of selected priorities
  filteredPriorities: [],

  // Array of selected statuses
  filteredStatuses: [],

  // Array of selected assignees
  filteredAssignees: [],

  // Array of selected categories
  filteredCategories: [],

  // Array of selected sources
  filteredSources: [],

  // Array of risk score values
  filteredRiskScores: [],

  // Full list of available columns to display
  availableColumnsConfig,

  // Replay Manager
  replay: null,

  // Persistence Manager
  persistence: null,

  init() {
    this._super(...arguments);
    this.setProperties({
      'filteredPriorities': [],
      'filteredStatuses': [],
      'filteredAssignees': [],
      'filteredCategories': [],
      'filteredSources': [],
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
    const statusArray = IncidentConstants.incidentStatusIds.map((statusId) => {
      return EmberObject.extend({
        id: statusId,
        value: emberComputed({
          get: () => this.get('filteredStatuses').includes(statusId),
          set: (key, value) => {
            const editInProgress = value && this.get('isBulkEditInProgress');

            if (!editInProgress) {
              run.once(() => {
                this.updateFilterValue('status', 'filteredStatuses', statusId, value);
              });
              return value;
            } else {
              this.openBulkEditModal();
            }
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
  @computed()
  priorityList() {
    const priorityArray = IncidentConstants.incidentPriorityIds.map((priorityId) => {
      return EmberObject.extend({
        id: priorityId,
        value: emberComputed({
          get: () => this.get('filteredPriorities').includes(priorityId),
          set: (key, value) => {
            const editInProgress = value && this.get('isBulkEditInProgress');

            if (!editInProgress) {
              run.once(() => {
                this.updateFilterValue('priority', 'filteredPriorities', priorityId, value);
              });
              return value;
            } else {
              this.openBulkEditModal();
            }
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
   * @name selectedAssignee
   * @description Returns a list of one element with the current assignee id. This is consumed by rsa-form-select
   * @public
   */
  @computed
  selectedAssignee: {
    get: () => [],
    set(values) {
      const editInProgress = (values && values.length > 0 && this.get('isBulkEditInProgress'));

      if (!editInProgress) {
        run.once(() => {
          this.set('filteredAssignees', values);
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

      const editInProgress = values.length > 0 && this.get('isBulkEditInProgress');

      if (!editInProgress) {
        this.set('filteredSources', values);
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

        const editInProgress = values && values.length > 0 && this.get('isBulkEditInProgress');

        if (!editInProgress) {
          this.set('filteredCategories', values);
          this.applyFilters('categories', filterValue);
        } else {
          this.openBulkEditModal();
        }
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
    const allIncidents = this.get('allIncidents.results');

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
        'selectedCategories': [],
        'riskScoreStart': riskScoreFilterInitialValues.slice()
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
      const specificIncident = this.get('allIncidents.results').findBy('id', itemID);
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
    },

    /**
     * Updates the filter based on the selected risk scores
     * Because the callback is called many times (at least 2 - one per each handler) by the sliders, we are using `run.debouce`
     * to only apply the filter once.
     * @public
     */
    onRiskScoreFilterUpdate(values) {
      run.debounce(this, this._filterByRiskScore, values, 200);
    }
  }
});