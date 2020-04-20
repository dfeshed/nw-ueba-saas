export default {
  class: 'rsa-context-panel__grid__host-details',
  dataSourceGroup: 'Machines',
  header: 'context.incident.header',
  headerRequired: false,
  footerRequired: true,
  footer: '',
  title: 'context.hostSummary.title',
  rows: [{
    cell: [{
      field: 'total_modules_count',
      title: 'context.hostSummary.modulesCount'
    },
    {
      field: 'IIOCLevel0',
      title: 'context.hostSummary.iioc0'
    },
    {
      field: 'IIOCLevel1',
      title: 'context.hostSummary.iioc1'
    },
    {
      field: 'LastScan',
      title: 'context.hostSummary.lastUpdated'
    }]
  },
  {
    cell: [{
      field: 'AdminStatus',
      title: 'context.hostSummary.adminStatus'
    },
    {
      field: 'LastSeen',
      title: 'context.hostSummary.lastLogin'
    },
    {
      field: 'MAC',
      title: 'context.hostSummary.macAddress'
    },
    {
      field: 'OperatingSystem',
      title: 'context.hostSummary.operatingSystem'
    }]
  },
  {
    cell: [{
      field: 'MachineStatus',
      title: 'context.hostSummary.machineStatus'
    },
    {
      field: 'LocalIPAddress',
      title: 'context.hostSummary.ipAddress',
      nested: 'LocalIPAddress',
      dataType: 'link'
    }]
  }],
  riskScore: {
    field: 'IIOCScore',
    title: 'context.hostSummary.riskScore',
    dataType: 'riskScore'
  }
};