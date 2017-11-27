/* eslint-env node */

export default {
  'name': 'default',
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
