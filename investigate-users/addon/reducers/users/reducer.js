import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

export const initialFilterState = Immutable.from({
  addAlertsAndDevices: true,
  addAllWatched: true,
  alertTypes: null,
  departments: null,
  indicatorTypes: null,
  isWatched: false,
  locations: null,
  minScore: null,
  severity: null,
  sortDirection: 'DESC',
  sortField: 'score',
  fromPage: 1,
  size: 25,
  userTags: null
});

const initialUsersSeverity = Immutable.from([{
  High: {
    userCount: null
  },
  Low: {
    userCount: null
  },
  Medium: {
    userCount: null
  },
  Critical: {
    userCount: null
  }
}]);

const initialState = Immutable.from({
  topUsers: [],
  riskyUserCount: 0,
  watchedUserCount: 0,
  adminUserCount: 0,
  usersSeverity: initialUsersSeverity,
  existAnomalyTypes: null,
  existAlertTypes: null,
  favorites: null,
  totalUsers: null,
  filter: initialFilterState
});

const tabs = handleActions({
  [ACTION_TYPES.RESTORE_DEFAULT]: () => Immutable.from(initialState),
  [ACTION_TYPES.GET_TOP_RISKY_USER]: (state, { payload }) => state.set('topUsers', [].concat(payload)),
  [ACTION_TYPES.GET_RISKY_USER_COUNT]: (state, { payload }) => state.set('riskyUserCount', payload),
  [ACTION_TYPES.GET_WATCHED_USER_COUNT]: (state, { payload }) => state.set('watchedUserCount', payload),
  [ACTION_TYPES.GET_ADMIN_USER_COUNT]: (state, { payload }) => state.set('adminUserCount', payload),
  [ACTION_TYPES.GET_SEVERITY_FOR_USERS]: (state, { payload }) => state.set('usersSeverity', [].concat(payload)),
  [ACTION_TYPES.GET_EXIST_ANOMALY_TYPES]: (state, { payload }) => state.set('existAnomalyTypes', payload),
  [ACTION_TYPES.GET_EXIST_ALERT_TYPES]: (state, { payload }) => state.set('existAlertTypes', payload),
  [ACTION_TYPES.GET_FAVORITES]: (state, { payload }) => state.set('favorites', payload),
  [ACTION_TYPES.GET_USERS]: (state, { payload: { data, total } }) => {
    // Concat user list data to current users list.
    let newState = state.set('users', state.getIn(['users']).concat(data));
    // Total user count for display and stop scrolling event.
    newState = newState.set('totalUsers', total);
    // Increment current page by one to fetch next page on scroll again.
    newState = newState.setIn(['filter', 'fromPage'], newState.getIn(['filter', 'fromPage']) + 1);
    return newState;
  },
  [ACTION_TYPES.UPDATE_FILTER_FOR_USERS]: (state, { payload }) => state.set('filter', payload ? state.getIn(['filter']).merge(payload) : initialFilterState),
  [ACTION_TYPES.RESET_USERS]: (state) => {
    const newState = state.merge({ topUsers: [], users: [], currentPage: 0, usersSeverity: initialUsersSeverity });
    return newState.setIn(['filter', 'fromPage'], 1);
  }
}, initialState);

export default tabs;