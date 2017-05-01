import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import * as DataActions from 'respond/actions/data-creators';
import * as UIStateCreators from 'respond/actions/ui-state-creators';
import { CANNED_FILTER_TYPES } from 'respond/utils/canned-filter-types';
import { SORT_TYPES } from 'respond/utils/sort-types';
import computed, { alias, empty } from 'ember-computed-decorators';
import { priorityOptions, statusOptions } from 'respond/selectors/dictionaries';
import { hasSelectedClosedIncidents } from 'respond/selectors/incidents';

const stateToComputed = (state) => {
  const { respond: { incidents, users } } = state;
  const { incidentsFilters, incidentsSort } = incidents;

  return {
    // whether the user is in select/edit mode
    isInSelectMode: incidents.isInSelectMode,
    // whether the filter panel is open
    isFilterPanelOpen: incidents.isFilterPanelOpen,
    // the selected incident ids
    incidentsSelected: incidents.incidentsSelected,
    // the name of the canned filter currently applied
    appliedCannedFilterName: incidentsFilters.cannedFilter,
    // the name of the sort currently applied
    appliedSort: incidentsSort,
    // list of possible incident priorities
    priorityTypes: priorityOptions(state),
    // list of possible incident statuses
    statusTypes: statusOptions(state),
    // list of available users to assigne incidents to
    users: users.users,
    // true if there are any selected incidents that are closed
    hasSelectedClosedIncidents: hasSelectedClosedIncidents(state)
  };
};

const dispatchToActions = (dispatch) => {
  return {
    handleCannedFilterChange(cannedFilter) {
      dispatch(DataActions.updateIncidentFilters({
        cannedFilter: cannedFilter.name
      }));
    },

    handleSortChange(sort) {
      dispatch(DataActions.sortBy(sort.name));
    },

    handleDeselectAll() {
      dispatch(UIStateCreators.clearSelectedIncidents());
    }
  };
};

/**
 * Toolbar that provides search filtering and sorting functionality for searching / exploring incidents.
 * @class IncidentsToolbar
 * @public
 */
const IncidentsToolbar = Component.extend({
  tagName: 'hbox',
  classNames: 'rsa-respond-incidents-toolbar',
  classNameBindings: [ 'isInSelectMode', 'isFilterPanelOpen:more-filters-active'],

  // Action to be invoked by clicking the "Select" button
  toggleSelectModeAction: null,

  // Action to be invoked by clicking the "More Filters" button
  toggleMoreFiltersAction: null,

  /**
   * The list of canned filters available for filtering the incidents result set
   * @property cannedFilterOptions
   * @public
   */
  @computed()
  cannedFilterOptions() {
    return CANNED_FILTER_TYPES.map(this._resolveOptionLabel());
  },

  /**
   * Boolean true when there are no selected incidents (in edit/select mode)
   * @property hasNoSelections
   * @public
   */
  @empty('incidentsSelected') hasNoSelections: true,

  /**
   * The number of currently selected incidents (when in select-mode)
   * @public
   * @property selectionCount
   */
  @alias('incidentsSelected.length')
  selectionCount: null,

  /**
   * The currently selected canned filter (object reference) that is looked up using the known, currently selected
   * canned filter name. Used for ensuring the canned filter dropdown has the properly selected option.
   * @property selectedCannedFilterOption
   * @public
   * @param appliedCannedFilterName
   * @param cannedFilterOptions
   * @returns {*|Object}
   */
  @computed('appliedCannedFilterName', 'cannedFilterOptions')
  selectedCannedFilterOption(appliedCannedFilterName, cannedFilterOptions) {
    return cannedFilterOptions.findBy('name', appliedCannedFilterName);
  },

  /**
   * The list of options for sorting the incidents result set
   * @property sortOptions
   * @public
   */
  @computed()
  sortOptions() {
    return SORT_TYPES.map(this._resolveOptionLabel());
  },

  /**
   * The currently selected sort (object reference) that is looked up using the known, currently selected
   * sort name. Used for ensuring the sort dropdown has the properly selected option.
   * @property selectedSortOption
   * @public
   * @param appliedOption
   * @param options
   * @returns {*|Object}
   */
  @computed('appliedSort', 'sortOptions')
  selectedSortOption(appliedOption, options) {
    return options.findBy('name', appliedOption);
  },

  /**
   * Helper method: returns a function that takes an object and updates its label prop with a i18n translated version,
   * if a label key exists on the object.
   * @returns {function(*)}
   * @private
   */
  _resolveOptionLabel() {
    return (option) => {
      return {
        ...option,
        label: option.labelKey ? this.get('i18n').t(option.labelKey) : option.label
      };
    };
  },

  actions: {
    handleToggleFilterPanel() {
      this.sendAction('toggleFilterPanel');
    },

    handleToggleIsInSelectMode() {
      this.sendAction('toggleIsInSelectMode');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(IncidentsToolbar);