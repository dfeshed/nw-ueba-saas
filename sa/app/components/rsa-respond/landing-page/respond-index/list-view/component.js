import Ember from 'ember';
import IncidentHelper from 'sa/incident/helpers';
import { viewType } from 'sa/protected/respond/index/route';
import IncidentConstants from 'sa/incident/constants';
import computed from 'ember-computed-decorators';
import ListViewConfig from './config';

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

  // Index of the last added column
  lastAddedColumnIndex: 99,

  // Full list of available columns to display
  availableColumnsConfig: ListViewConfig.availableColumnsConfig,

  init() {
    this._super(...arguments);
    this.setProperties({
      'filteredPriorities': [],
      'filteredStatuses': []
    });
  },

  /**
   * @description Selected and sorted columns for the data table
   * @public
   */
  @computed('availableColumnsConfig.@each.visible')
  incidentListConfig(availableColumnsConfig) {
    let list = availableColumnsConfig.filterBy('visible', true);
    this.setColumnWidth(list);
    return list.sortBy('displayIndex');
  },

  /**
   * @description List of all the columns. It runs only once since the list of available columns is static.
   * Each column defines a computed-property to handle column selection
   * @public
   */
  @computed('availableColumnsConfig')
  allColumns(columns) {
    columns.forEach((col) => {
      col.reopen({
        selected: emberComputed({
          get: () => col.get('visible'),
          set: (key, value) => {
            run.once(() => {
              if (value === true) {
                // displayIndex of last selected column has the higher value
                let lastAddedColumnIndex = this.incrementProperty('lastAddedColumnIndex');
                col.set('displayIndex', lastAddedColumnIndex);
              } else {
                // When unselecting columns we prevents to unselect all of them
                let visibleColumnsLength = columns.filterBy('visible', true).length;
                if (visibleColumnsLength === 1) {
                  col.set('selected', true);
                  return value;
                }
              }
              col.set('visible', value);
            });
            return value;
          }
        })
      });
    });
    return columns;
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

  /**
   * @description Define the width of all the columns based on the following rule:
   * Note: If a column has a `minWidth` attribute is considered as flexible.
   * - Those columns with no `minWidth` attribute will keep their defined width.
   * - Flexible columns will adjust their width based on the available
   * space and the `minWidth` attribute after all the fix columns were processed.
   * @private
   */
  setColumnWidth(list) {
    let sumFixedWidth = 0;
    let sumFlexibleWidth = 0;
    let columnWidth,
      availableWidth;

    let fixedWidthColumns = list.filter((item) => !item.minWidth);
    let flexibleWidthColumns = list.filter((item) => item.minWidth);

    fixedWidthColumns.forEach((column) => {
      sumFixedWidth += this.extractColumnWidth(column.get('width'));
    });

    flexibleWidthColumns.forEach((column) => {
      sumFlexibleWidth += this.extractColumnWidth(column.get('minWidth'));
    });

    availableWidth = 100 - sumFixedWidth;
    flexibleWidthColumns.forEach((column) => {
      columnWidth = this.extractColumnWidth(column.get('minWidth'));
      columnWidth = Math.round(availableWidth * columnWidth / sumFlexibleWidth);
      column.set('width', `${ columnWidth }%`);
    });
  },

  /**
   * Extract the width value from the columns
   * @private
   */
  extractColumnWidth(columnWidth) {
    if (typeof columnWidth === 'string') {
      return parseInt(columnWidth.replace('%', ''), 10);
    } else {
      return columnWidth;
    }
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