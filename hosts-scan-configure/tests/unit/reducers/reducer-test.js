import { test, module } from 'qunit';
import Immutable from 'seamless-immutable';
import { LIFECYCLE } from 'redux-pack';
import ACTION_TYPES from 'hosts-scan-configure/actions/types';
import reducer from 'hosts-scan-configure/reducers/schedule/reducer';
import makePackAction from '../../helpers/make-pack-action';

module('Unit | Reducers | Schedule');
const initialState = Immutable.from({
  config: {
    name: 'default'
  },
  fetchScheduleStatus: null
});

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, initialState);
});

test('When FETCH_SCHEDULE_CONFIG starts, the fetchScheduleStatus changes to wait', function(assert) {
  // const initState = copy(initialState);
  const fetchScheduleStatus = 'wait';
  const expectedEndState = {
    ...initialState,
    fetchScheduleStatus
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_SCHEDULE_CONFIG });
  const endState = reducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});


test('When FETCH_SCHEDULE_CONFIG success,the scheduleConfig and fetchScheduleStatus update appropriately ', function(assert) {
  const config = {
    'name': 'default',
    'id': 1,
    'scheduleConfig': {
      'enabled': true,
      'group': 'default',
      'scanOptions': {
        'cpuMax': '80',
        'cpuMaxVm': '90'
      },
      'scheduleOptions': {
        'recurrenceIntervalUnit': 'DAYS',
        'recurrenceInterval': 1,
        'runOnDays': [1],
        'startTime': '2017-08-29T10:23:49.452Z',
        'timeZone': 'UTC'
      }
    }
  };

  const expectedEndState = {
    ...initialState,
    fetchScheduleStatus: 'completed',
    config
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_SCHEDULE_CONFIG, payload: { data: config } });
  const endState = reducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('The UPDATE_CONFIG_PROPERTY action will update the schedule config', function(assert) {

  const previous = Immutable.from({
    config: { 'name': 'default',
      'id': 1,
      'scheduleConfig': {
        'enabled': true,
        'group': 'default',
        'scanOptions': {
          'cpuMax': '80',
          'cpuMaxVm': '90'
        },
        'scheduleOptions': {
          'recurrenceIntervalUnit': 'DAYS',
          'recurrenceInterval': 1,
          'runOnDays': [1],
          'startTime': '2017-08-29T10:23:49.452Z',
          'timeZone': 'UTC'
        }
      }
    }
  });

  const expectedEndState = {
    'config': {
      'id': 1,
      'name': 'default',
      'scheduleConfig': {
        'enabled': true,
        'group': 'default',
        'scanOptions': {
          'cpuMax': '80',
          'cpuMaxVm': '90'
        },
        'scheduleOptions': {
          'recurrenceInterval': 1,
          'recurrenceIntervalUnit': 'WEEKS',
          'runOnDays': [
            1
          ],
          'startTime': '2017-08-29T10:23:49.452Z',
          'timeZone': 'UTC'
        }
      }
    }
  };
  const payload = {
    scheduleConfig: {
      scheduleOptions: {
        recurrenceIntervalUnit: 'WEEKS'
      }
    }
  };
  const result = reducer(previous, { type: ACTION_TYPES.UPDATE_CONFIG_PROPERTY, payload });
  assert.deepEqual(result, expectedEndState);
});
