import Component from 'ember-component';
import connect from 'ember-redux/components/connect';

const stateToComputed = (state) => {
  const {
    respond: {
      dictionaries: { priorityTypes, remediationStatusTypes },
      remediationTasks: { itemsFilters }
    }
  } = state;

  return {
    priorityFilters: itemsFilters.priority || [],
    statusFilters: itemsFilters.status || [],
    escalatedFilters: itemsFilters.escalated || [],
    priorityTypes,
    remediationStatusTypes
  };
};

/**
 * @class RemediationTaskFilters
 * Represents the incident filters for populating the explorer filters panel
 *
 * @public
 */
const RemediationTaskFilters = Component.extend({
  tagName: '',

  escalationTypes: [true, false],

  actions: {
    toggleStatusFilter(status) {
      const statusFilters = this.get('statusFilters');
      this.get('updateFilter')({
        status: statusFilters.includes(status) ? statusFilters.without(status) : [...statusFilters, status]
      });
    },

    togglePriorityFilter(priority) {
      const priorityFilters = this.get('priorityFilters');
      this.get('updateFilter')({
        priority: priorityFilters.includes(priority) ? priorityFilters.without(priority) : [...priorityFilters, priority]
      });
    },

    toggleIsEscalatedFilter(escalated) {
      const escalatedFilters = this.get('escalatedFilters');
      this.get('updateFilter')({
        escalated: escalatedFilters.includes(escalated) ? escalatedFilters.without(escalated) : [...escalatedFilters, escalated]
      });
    }
  }
});

export default connect(stateToComputed, undefined)(RemediationTaskFilters);