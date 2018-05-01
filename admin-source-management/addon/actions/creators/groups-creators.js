import * as ACTION_TYPES from 'admin-source-management/actions/types/groups-types';
import groupsAPI from 'admin-source-management/actions/api/groups-api';

const fetchGroups = () => ({
  type: ACTION_TYPES.FETCH_GROUPS,
  promise: groupsAPI.fetchGroups()
});

export {
  fetchGroups
};
