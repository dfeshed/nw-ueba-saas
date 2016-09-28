import Ember from 'ember';
import IncidentHelper from 'sa/incident/helpers';
import { viewType } from 'sa/protected/respond/index/route';
import IncidentConstants from 'sa/incident/constants';
import computed from 'ember-computed-decorators';

const {
  Component,
  run,
  computed: emberComputed
} = Ember;

export default Component.extend({
  tagName: 'hbox',

  // default sorted field for list view
  currentSort: 'riskScore',

  // Array of selected priorities
  filteredPriorities: [],
  // Array of selected statuses
  filteredStatuses: [],

  // Config for the data table for the incidents list view landing page
  incidentListConfig: [
    {
      field: 'riskScore',
      title: 'incident.list.riskScore',
      dataType: 'custom',
      width: '7%',
      class: 'rsa-respond-list-riskscore',
      componentClass: 'rsa-content-badge-score',
      isDescending: true
    },
    {
      field: 'id',
      title: 'incident.list.id',
      class: 'incident-id',
      width: '6%',
      dataType: 'text'
    },
    {
      field: 'name',
      title: 'incident.list.name',
      width: '35%',
      class: 'rsa-respond-list-name',
      dataType: 'text'
    },
    {
      field: 'created',
      title: 'incident.list.createdDate',
      width: '10%',
      class: 'rsa-respond-list-created',
      dataType: 'date-time',
      componentClass: 'rsa-content-datetime'
    },
    {
      field: 'assigneeFirstLastName',
      title: 'incident.list.assignee',
      width: '10%',
      class: 'rsa-respond-list-assignee',
      dataType: 'text'
    },
    {
      field: 'statusSort',
      title: 'incident.list.status',
      width: '12%',
      class: 'rsa-respond-list-status',
      dataType: 'text'
    },
    {
      field: 'alertCount',
      title: 'incident.list.alertCount',
      width: '5%',
      class: 'rsa-respond-list-alertCount',
      dataType: 'text'
    },
    {
      field: 'sources',
      title: 'incident.list.sources',
      width: '10%',
      class: 'rsa-respond-list-sources',
      dataType: 'text'
    },
    {
      field: 'eventCount',
      title: 'incident.fields.events',
      width: '5%',
      class: 'rsa-respond-list-events',
      dataType: 'text'
    }
  ],

  init() {
    this._super(...arguments);
    this.setProperties({
      'filteredPriorities': [],
      'filteredStatuses': []
    });
  },

  @computed('categoryTags.[]')
  normalizedTreeData: (categoryTags) => IncidentHelper.normalizeCategoryTags(categoryTags),

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
            this.updateFilterValue('status', 'filteredStatuses', statusId, value);
            return value;
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
            this.updateFilterValue('priority', 'filteredPriorities', priorityId, value);
            return value;
          }
        })
      };
    });
    return priorityArray;
  },

  /**
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
      run.once(() => {
        this.applyFilters('assigneeId', (values || []).slice(0));
      });
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
      this.applyFilters('categories', filterValue);
      return values;
    }
  },

  /**
   * @name applyFilters
   * @description Filters the incident list cube
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

  actions: {
    // sets the current sorted column field name and the sort direction
    // and calls the sortAction in the route to do the actual sort for list view
    sortListView(column, direction) {
      column.set('isDescending', (direction === 'desc'));
      this.set('currentSort', column.field);
      this.sendAction('sortAction', column.field, direction, viewType.LIST_VIEW);
    },

    /**
     * @description Set all the filter options back to default values
     * @public
     */
    resetFilters() {
      this.get('statusList').setEach('value', false);
      this.get('priorityList').setEach('value', false);
      this.setProperties({
        'selectedAssignee': [],
        'selectedCategories': []
      });
    }
  }
});
