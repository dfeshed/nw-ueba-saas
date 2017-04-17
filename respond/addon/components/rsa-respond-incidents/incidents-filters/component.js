import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import * as DataActions from 'respond/actions/data-creators';
import { priorityOptions, statusOptions } from 'respond/selectors/dictionaries';
import computed from 'ember-computed-decorators';
import { SINCE_WHEN_TYPES } from 'respond/utils/since-when-types';

const stateToComputed = (state) => {
  const {
    respond: {
      dictionaries: { categoryTags },
      incidents: { incidentsFilters }
    }
  } = state;

  return {
    priorityFilters: incidentsFilters.priority,
    statusFilters: incidentsFilters.status,
    assigneeFilters: incidentsFilters['assignee.id'],
    priorityTypes: priorityOptions(state),
    statusTypes: statusOptions(state),
    categoryFilters: incidentsFilters['categories.name'],
    categoryTags,
    users: state.respond.users.users,
    timeframeFilter: incidentsFilters.created
  };
};

const dispatchToActions = (dispatch) => {
  return {
    updateFilter(change) {
      dispatch(DataActions.updateIncidentFilters(change));
    },
    reset() {
      dispatch(DataActions.resetIncidentFilters());
    }
  };
};

/**
 * Toolbar that provides search filtering and sorting functionality for searching / exploring incidents.
 * @class IncidentsToolbar
 * @public
 */
const IncidentsFilters = Component.extend({
  classNames: 'incidents-filters',

  timeframes: SINCE_WHEN_TYPES,

  @computed('timeframeFilter', 'timeframes')
  selectedTimeframe(timeframe, timeframes) {
    return timeframes.findBy('name', timeframe.name);
  },

  @computed('users', 'assigneeFilters')
  selectedAssignees(users, assigneeFilters = []) {
    return users.map((user) => {
      return assigneeFilters.includes(user.id) ? user : null;
    }).compact();
  },

  @computed('categoryTags', 'categoryFilters')
  selectedCategories(categories, categoryFilters = []) {
    return categories.mapBy('options')      // pull out the options array from each group
      .reduce((previousValue, item) => {    // reduce to one big array of the category options
        return previousValue.concat(item);
      }, [])
      .map((category) => {                  // find all the matching categories that are applied as a filter
        return categoryFilters.includes(category) ? category : null;
      }).compact();
  },

  actions: {
    toggleStatusFilter(status) {
      const statusFilters = this.get('statusFilters');
      this.send('updateFilter', {
        status: statusFilters.includes(status) ? statusFilters.without(status) : [...statusFilters, status]
      });
    },
    togglePriorityFilter(priority) {
      const priorityFilters = this.get('priorityFilters');
      this.send('updateFilter', {
        priority: priorityFilters.includes(priority) ? priorityFilters.without(priority) : [...priorityFilters, priority]
      });
    },
    assigneeChanged(selections) {
      this.send('updateFilter', {
        'assignee.id': selections.map((selection) => {
          return selection.id;
        })
      });
    },
    onChangeTimeframe(created) {
      this.send('updateFilter', { created });
    },
    categoryChanged(selections) {
      this.send('updateFilter', {
        'categories.name': selections
      });
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(IncidentsFilters);