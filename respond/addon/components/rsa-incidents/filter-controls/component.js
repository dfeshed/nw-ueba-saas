import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => {
  const {
    respond: {
      users,
      dictionaries: { categoryTags, priorityTypes, statusTypes },
      incidents: { itemsFilters }
    }
  } = state;

  return {
    priorityFilters: itemsFilters.priority || [],
    statusFilters: itemsFilters.status || [],
    assigneeFilters: itemsFilters['assignee.id'],
    priorityTypes,
    statusTypes,
    categoryFilters: itemsFilters['categories.parent'],
    categoryTags,
    users: users.enabledUsers
  };
};

/**
 * @class IncidentFilters
 * Represents the incident filters for populating the explorer filters panel
 *
 * @public
 */
const IncidentFilters = Component.extend({
  tagName: '',

  /**
   * The user objects that have been selected via the assignee picker
   * @property selectedAssignees
   * @public
   * @param users
   * @param assigneeFilters
   * @returns {Array}
   */
  @computed('users', 'assigneeFilters')
  selectedAssignees(users, assigneeFilters = []) {
    return users.filter((user) => (assigneeFilters.includes(user.id)));
  },

  /**
   * Returns the list of selected category objects that are currently being used in the filter
   * @public
   * @param categories
   * @param categoryFilters
   */
  @computed('categoryTags', 'categoryFilters')
  selectedCategories(categories, categoryFilters = []) {
    return categories.filter((category) => (categoryFilters.includes(category)));
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

    assigneeChanged(selections) {
      this.get('updateFilter')({
        'assignee.id': selections.map((selection) => {
          return selection.id;
        })
      });
    },

    categoryChanged(selections) {
      this.get('updateFilter')({
        'categories.parent': selections
      });
    }
  }
});

export default connect(stateToComputed, undefined)(IncidentFilters);