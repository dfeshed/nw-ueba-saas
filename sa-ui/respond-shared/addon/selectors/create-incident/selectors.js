import reselect from 'reselect';
import _ from 'lodash';
import Immutable from 'seamless-immutable';

const { createSelector } = reselect;

const UNASSIGN_USER = { id: 'UNASSIGNED' };

const incidentsState = (state) => state.respondShared.createIncident;

export const getPriorityTypes = createSelector(
  incidentsState,
  (incidentsState) => incidentsState.priorityTypes
);

export const getEnabledUsers = createSelector(
  incidentsState,
  (incidentsState) => {
    return incidentsState.enabledUsers;
  }
);

export const getCategoryTags = createSelector(
  incidentsState,
  (incidentsState) => incidentsState.categoryTags
);

export const getGroupedCategories = createSelector(
  getCategoryTags,
  (categories) => {
    const groupedCategories = categories.reduce((groups, item) => {
      if (!groups[item.parent]) {
        groups[item.parent] = { groupName: item.parent, options: [item] };
      } else {
        groups[item.parent].options.push(item);
      }
      return groups;
    }, {});
    return _.values(groupedCategories);
  }
);

export const getAssigneeOptions = createSelector(
  getEnabledUsers,
  (enabledUsers) => {
    const users = enabledUsers.asMutable();
    users.unshift(UNASSIGN_USER);
    return Immutable.from(users);
  }
);
