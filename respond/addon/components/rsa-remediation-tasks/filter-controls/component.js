import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => {
  const {
    respond: {
      users,
      dictionaries: { priorityTypes, remediationStatusTypes },
      remediationTasks: { itemsFilters }
    }
  } = state;

  return {
    priorityFilters: itemsFilters.priority || [],
    statusFilters: itemsFilters.status || [],
    createdByFilters: itemsFilters.createdBy || [],
    users: users.allUsers,
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

  /**
   * The user objects that have been selected via the assignee picker
   * @property selectedCreatedBys
   * @public
   * @param users
   * @param createdByFilters
   * @returns {Array}
   */
  @computed('users', 'createdByFilters')
  selectedCreatedBys(users, createdByFilters = []) {
    return users.filter((user) => (createdByFilters.includes(user.id)));
  },

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

    createdByChanged(selections) {
      this.get('updateFilter')({
        createdBy: selections.map((selection) => {
          return selection.id;
        })
      });
    }
  }
});

export default connect(stateToComputed, undefined)(RemediationTaskFilters);