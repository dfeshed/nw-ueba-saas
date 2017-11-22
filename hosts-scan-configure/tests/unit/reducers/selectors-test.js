import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  isFetchingSchedule,
  weekOptions,
  runIntervalConfig
} from 'hosts-scan-configure/reducers/schedule/selectors';

module('Unit | selectors | schedule');

test('isFetchingSchedule', function(assert) {
  assert.expect(2);
  const state = Immutable.from({ schedule: {
    fetchScheduleStatus: 'wait'
  } });
  const tests = {
    wait: isFetchingSchedule(state),
    completed: isFetchingSchedule(Immutable.from({ schedule: { fetchScheduleStatus: 'completed' } }))
  };
  assert.equal(tests.wait, true, 'isFetchingSchedule should return true when status is wait');
  assert.equal(tests.completed, false, 'isFetchingSchedule should return false when status is completed');
});


test('weekOptions', function(assert) {
  assert.expect(1);
  const schedule = Immutable.from(
    {
      schedule: {
        config: {
          scheduleConfig: {
            scheduleOptions: {
              'recurrenceIntervalUnit': 'WEEKS',
              'recurrenceInterval': 1,
              'runOnDays': [0],
              'startTime': '2017-08-29T10:23:49.452Z',
              'timeZone': 'UTC'
            }
          }
        }
      }
    });
  const result = weekOptions(schedule);
  const expected = {
    'index': 0,
    'isActive': true,
    'label': 'hostsScanConfigure.recurrenceInterval.week.monday'
  };
  assert.deepEqual(result[0], expected, 'should add label and isActive');
});


test('runIntervalConfig', function(assert) {
  assert.expect(1);
  const schedule = Immutable.from(
    {
      schedule: {
        config: {
          scheduleConfig: {
            scheduleOptions: {
              'recurrenceIntervalUnit': 'WEEKS',
              'recurrenceInterval': 1,
              'runOnDays': [0],
              'startTime': '2017-08-29T10:23:49.452Z',
              'timeZone': 'UTC'
            }
          }
        }
      }
    });
  const result = runIntervalConfig(schedule);
  const expected = {
    'options': [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24],
    'runLabel': 'hostsScanConfigure.recurrenceInterval.intervalText.WEEKS'
  };
  assert.deepEqual(result, expected, 'should return the processed run interval configuration');
});
