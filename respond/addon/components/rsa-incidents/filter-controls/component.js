import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { debounce } from '@ember/runloop';
import {
  getTopLevelCategoryNames,
  getPriorityTypes,
  getStatusTypes
} from 'respond/selectors/dictionaries';

import {
  getPriorityFilters,
  getStatusFilters,
  getIdFilters,
  getIsUnassignedFilters,
  getAssigneeFilters,
  getCategoryFilters,
  hasAssigneeFilter,
  getSentToArcherFilters,
  isSendToArcherAvailable
} from 'respond/selectors/incidents';

import { getEnabledUsers } from 'respond/selectors/users';

const stateToComputed = (state) => {
  const enabledUsers = [].concat(getEnabledUsers(state));
  return {
    priorityFilters: getPriorityFilters(state),
    statusFilters: getStatusFilters(state),
    idFilter: getIdFilters(state),
    isUnassignedFilter: getIsUnassignedFilters(state),
    assigneeFilters: getAssigneeFilters(state),
    hasAssigneeFilter: hasAssigneeFilter(state),
    sentToArcherFilters: getSentToArcherFilters(state),
    priorityTypes: getPriorityTypes(state),
    statusTypes: getStatusTypes(state),
    categoryFilters: getCategoryFilters(state),
    categoryTags: getTopLevelCategoryNames(state),
    users: enabledUsers,
    isSendToArcherAvailable: isSendToArcherAvailable(state)
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
  sentToArcherTypes: [true, false],
  isIdFilterValid: true,

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

  assigneeMatcher({ name, id }, searchTerm) {
    const userName = name || id;
    return userName.toLowerCase().indexOf(searchTerm.toLowerCase());
  },

  actions: {
    idFilterChanged(value = '') {
      const isValid = !value ? true : (/^INC-\d+$/i).test(value);
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

    toggleSentToArcherStatusFilter(sentToArcher) {
      const sentToArcherFilters = this.get('sentToArcherFilters');
      this.get('updateFilter')({
        sentToArcher: sentToArcherFilters.includes(sentToArcher) ? sentToArcherFilters.without(sentToArcher) : [...sentToArcherFilters, sentToArcher]
      });
    },

    toggleIsUnassignedFilter() {
      this.get('updateFilter')({
        assignee: {
          field: 'assignee',
          isNull: !this.get('isUnassignedFilter')
        }
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
