import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import * as DataActions from 'respond/actions/data-creators';
import { CANNED_FILTER_TYPES } from 'respond/utils/canned-filter-types';
import { SORT_TYPES } from 'respond/utils/sort-types';
import computed, { alias } from 'ember-computed-decorators';

const {
  inject: { service },
  Component,
  Logger,
  K
} = Ember;

const stateToComputed = ({ respond: { incidents } }) => {
  const { incidentsFilters, incidentsSort } = incidents;

  return {
    // the name of the canned filter currently applied
    appliedCannedFilterName: incidentsFilters.cannedFilter,

    // the name of the sort currently applied
    appliedSort: incidentsSort
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
  classNameBindings: [ 'isInSelectMode', 'isMoreFiltersActive:more-filters-active', 'isAltThemeActive:light-theme-active' ],
  i18n: service(),

  // Array of currently selected items. Used to display a counter while in "Select" mode.
  selections: null,

  // Indicates whether user clicked to toggle on "Select" mode, which shows Select-specific content
  isInSelectMode: false,

  // Action to be invoked by clicking the "Select" button
  toggleSelectModeAction: null,

  // Action to be invoked by clicking the "More Filters" button
  toggleMoreFiltersAction: null,

  // true if the "More Filters" button should be shown as active
  isMoreFiltersActive: false,

  // true if the alternate (color) theme is active
  isAltThemeActive: false,

  noop: K,

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
    handleSubjectChange() {
      Logger.warn('Implement handleSubjectChange()');
    },
    handleToggleFilterPanel() {
      this.sendAction('toggleFilterPanel');
    },
    handleToggleColorTheme() {
      this.sendAction('toggleTheme');
    },
    handleToggleIsInSelectMode() {
      this.sendAction('toggleIsInSelectMode');
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(IncidentsToolbar);