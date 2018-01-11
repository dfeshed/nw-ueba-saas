import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import moment from 'moment';

import {
  isFetchingSchedule,
  weekOptions,
  runIntervalConfig,
  startDate
} from 'hosts-scan-configure/reducers/hosts-scan/selectors';

module('Unit | selectors | schedule');

test('isFetchingSchedule', function(assert) {
  assert.expect(2);
  const state = Immutable.from({ hostsScan: {
    fetchScheduleStatus: 'wait'
  } });
  const tests = {
    wait: isFetchingSchedule(state),
    completed: isFetchingSchedule(Immutable.from({ hostsScan: { fetchScheduleStatus: 'completed' } }))
  };
  assert.equal(tests.wait, true, 'isFetchingSchedule should return true when status is wait');
  assert.equal(tests.completed, false, 'isFetchingSchedule should return false when status is completed');
});


test('weekOptions', function(assert) {
  assert.expect(1);
  const schedule = Immutable.from(
    {
      hostsScan: {
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
    'label': 'hostsScanConfigure.recurrenceInterval.week.sunday'
  };
  assert.deepEqual(result[0], expected, 'should add label and isActive');
});


test('runIntervalConfig', function(assert) {
  assert.expect(1);
  const schedule = Immutable.from(
    {
      hostsScan: {
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
test('startDate', function(assert) {
  assert.expect(2);
  const schedule = Immutable.from(
    {
      hostsScan: {
        config: {
          scheduleConfig: {
            scheduleOptions: {
            }
          }
        }
      }
    });
  const result = startDate(schedule);

  assert.deepEqual(result, 'today', 'should return today if start date is empty');

  const schedule2 = Immutable.from(
    {
      hostsScan: {
        config: {
          scheduleConfig: {
            scheduleOptions: {
              startDate: '01/10/2018'
            }
          }
        }
      }
    });
  const result2 = startDate(schedule2);
  assert.deepEqual(result2, moment('01/10/2018').toISOString(), 'should return iso format date');
});
