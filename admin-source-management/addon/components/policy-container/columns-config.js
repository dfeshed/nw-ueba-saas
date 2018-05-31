export default [
  {
    field: 'name',
    title: 'Name',
    width: '10%'
  },
  {
    field: 'description',
    title: 'Description',
    width: '10%'
  },
  {
    field: 'scheduleConfig.enabledScheduledScan',
    title: 'Enabled',
    width: '5%'
  },
  {
    field: 'scheduleConfig.scheduleOptions.scanStartDate',
    title: 'Start Date',
    width: '10%',
    dataType: 'DATE'
  },
  {
    field: 'scheduleConfig.scheduleOptions.recurrenceInterval',
    title: 'Interval',
    width: '5%'
  },
  {
    field: 'scheduleConfig.scheduleOptions.recurrenceIntervalUnit',
    title: 'Interval Unit',
    width: '10%'
  },
  {
    field: 'scheduleConfig.scheduleOptions.runOnDaysOfWeek',
    title: 'On Days',
    width: '10%'
  },
  {
    field: 'scheduleConfig.scheduleOptions.scanStartTime',
    title: 'Start Time',
    width: '5%',
    dataType: 'TIME'
  },
  {
    field: 'scheduleConfig.scanOptions.cpuMaximum',
    title: 'CPU Maximum ( % )',
    width: '10%'
  },
  {
    field: 'scheduleConfig.scanOptions.cpuMaximumOnVirtualMachine',
    title: 'Virtual Machine Maximum ( % )',
    width: '10%'
  }
];
