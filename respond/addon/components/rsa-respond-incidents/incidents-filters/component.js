import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import * as DataActions from 'respond/actions/data-creators';
import { priorityOptions, statusOptions } from 'respond/selectors/dictionaries';
import computed from 'ember-computed-decorators';
import { SINCE_WHEN_TYPES } from 'respond/utils/since-when-types';

const stateToComputed = (state) => {
  return {
    priorityFilters: state.respond.incidents.incidentsFilters.priority,
    statusFilters: state.respond.incidents.incidentsFilters.status,
    assigneeFilters: state.respond.incidents.incidentsFilters['assignee.id'],
    priorityTypes: priorityOptions(state),
    statusTypes: statusOptions(state),
    users: state.respond.users.users,
    timeframeFilter: state.respond.incidents.incidentsFilters.created
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
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(IncidentsFilters);