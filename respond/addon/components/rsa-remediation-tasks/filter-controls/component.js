import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { debounce } from '@ember/runloop';

const stateToComputed = (state) => {
  const {
    respond: {
      users,
      dictionaries: { priorityTypes, remediationStatusTypes },
      remediationTasks: { itemsFilters }
    }
  } = state;

  return {
    idFilter: itemsFilters.id,
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

  isIdFilterValid: true,

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

  // Custom search for the createdBy filter to ensure that if the user.name is not found, we also search id
  createdByMatcher({ name, id }, searchTerm) {
    const userName = name || id;
    return userName.toLowerCase().indexOf(searchTerm.toLowerCase());
  },

  actions: {
    idFilterChanged(value = '') {
      const isValid = !value ? true : (/^REM-\d+$/i).test(value);
      this.set('isIdFilterValid', isValid);
      if (isValid) {
        const id = value.toUpperCase();
        debounce(this, this.get('updateFilter'), { id }, 1000);
      }
    },

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
