/* eslint-env node */

module.exports = {
  'name': 'default',
  'scheduleConfig': {
    'enabled': true,
    'group': 'default',
    'scanOptions': {
      'cpuMax': '80',
      'cpuMaxVm': '90'
    },
    'schedule': {
      'recurrenceIntervalUnit': 'DAYS',
      'recurrenceInterval': 1,
      'runOnDaysOfWeek': [1],
      'runAtTime': '2017-08-29T10:23:49.452Z',
      'timeZone': 'UTC'
    }
  }
};