export default {
  'scheduledScanConfig': {
    'enabled': true,
    'recurrentSchedule': {
      'recurrence': {
        'interval': 1,
        'unit': 'WEEKS'
      },
      'startTime': 'start-time',
      'runOnDays': [
        1,
        2
      ],
      'scheduleStartDate': 'start-date'
    },
    'scanOptions': {
      'cpuMax': 10,
      'cpuMaxVm': 20
    }
  }
};