import * as ACTION_TYPES from '../../actions/types';
import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import moment from 'moment';

export const initialFilterState = Immutable.from({
  alert_start_range: `${moment().subtract('months', 3).unix() * 1000},${moment().unix() * 1000}`,
  sort_direction: 'DESC',
  sort_field: 'startDate',
  total_severity_count: true,
  severity: null,
  feedback: null,
  indicator_types: null,
  fromPage: 1,
  size: 25
});

const initialState = Immutable.from({
  topAlerts: [],
  topAlertsError: null,
  alertList: [],
  alertListError: null,
  existAnomalyTypes: null,
  alertsForTimeline: null,
  alertsForTimelineError: null,
  alertsSeverity: {
    total_severity_count: {
      Critical: null,
      High: null,
      Medium: null,
      Low: null
    }
  },
  filter: initialFilterState,
  totalAlerts: null
});

const tabs = handleActions({
  [ACTION_TYPES.RESTORE_DEFAULT]: () => (Immutable.from(initialState)),
  [ACTION_TYPES.GET_TOP_ALERTS]: (state, { payload }) => state.set('topAlerts', [].concat(payload)),
  [ACTION_TYPES.GET_ALERTS_FOR_TIMELINE]: (state, { payload }) => state.set('alertsForTimeline', payload),
  [ACTION_TYPES.ALERT_LIST_ERROR]: (state, { payload }) => state.set('alertListError', payload),
  [ACTION_TYPES.TOP_ALERTS_ERROR]: (state, { payload }) => state.set('topAlertsError', payload),
  [ACTION_TYPES.ALERTS_FOR_TIMELINE_ERROR]: (state, { payload }) => state.set('alertsForTimelineError', payload),
  [ACTION_TYPES.GET_ALERTS]: (state, { payload: { data, info, total } }) => {
    // Concat data for requested page.
    let newState = state.set('alertList', state.getIn(['alertList']).concat(data));
    // Add total alerts.
    newState = newState.set('totalAlerts', total);
    // Add alertsSeverity for given filter.
    newState = info ? newState.set('alertsSeverity', info) : newState;
    // increment current page by one so for pulling next page data on next scroll.
    newState = newState.setIn(['filter', 'fromPage'], newState.getIn(['filter', 'fromPage']) + 1);
    return newState;
  },
  [ACTION_TYPES.GET_EXIST_ANOMALY_TYPES_ALERT]: (state, { payload }) => state.set('existAnomalyTypes', payload),
  [ACTION_TYPES.UPDATE_FILTER_FOR_ALERTS]: (state, { payload }) => {
    let filter = initialFilterState;
    if (payload) {
      filter = state.getIn(['filter']).merge(payload);
    }
    return state.set('filter', filter);
  },
  [ACTION_TYPES.RESET_ALERTS]: (state) => {
    const newState = state.merge({ topAlerts: [], alertList: [], currentPage: 0, filter: { fromPage: 1 } });
    return newState.setIn(['filter', 'fromPage'], 1);
  }
}, initialState);

export default tabs;
