export default [
  {
    title: 'adminUsm.groups.list.select',
    class: 'rsa-form-row-checkbox',
    width: '40px',
    dataType: 'checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true
  },
  {
    field: 'name',
    title: 'adminUsm.policies.list.name',
    width: '10%'
  },
  {
    field: 'description',
    title: 'adminUsm.policies.list.description',
    width: '10%'
  },
  {
    field: 'scheduleConfig.enabledScheduledScan',
    title: 'adminUsm.policies.list.enabled',
    width: '5%'
  },
  {
    field: 'scheduleConfig.scheduleOptions.scanStartDate',
    title: 'adminUsm.policies.list.startDate',
    width: '10%',
    dataType: 'DATE'
  },
  {
    field: 'scheduleConfig.scheduleOptions.recurrenceInterval',
    title: 'adminUsm.policies.list.interval',
    width: '5%'
  },
  {
    field: 'scheduleConfig.scheduleOptions.recurrenceIntervalUnit',
    title: 'adminUsm.policies.list.intervalUnit',
    width: '10%'
  },
  {
    field: 'scheduleConfig.scheduleOptions.runOnDaysOfWeek',
    title: 'adminUsm.policies.list.onDays',
    width: '10%'
  },
  {
    field: 'scheduleConfig.scheduleOptions.scanStartTime',
    title: 'adminUsm.policies.list.startTime',
    width: '5%',
    dataType: 'TIME'
  },
  {
    field: 'scheduleConfig.scanOptions.cpuMaximum',
    title: 'adminUsm.policies.list.cpuMax',
    width: '10%'
  },
  {
    field: 'scheduleConfig.scanOptions.cpuMaximumOnVirtualMachine',
    title: 'adminUsm.policies.list.vmMax',
    width: '10%'
  }
];
