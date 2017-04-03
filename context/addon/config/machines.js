export default [{
  class: 'rsa-context-panel__grid__host-details',
  dataSourceGroup: 'Machines',
  header: 'context.incident.header',
  footer: '',
  width: '2',
  title: 'context.hostSummary.title',
  columns: [
    {
      class: 'rsa-context-panel__endpoint__host-details__badge-score col-xs-2',
      displayField: true,
      cell: [{
        field: 'IIOCScore',
        title: 'context.hostSummary.riskScore',
        dataType: 'riskScore'
      }]
    },
    {
      class: 'col-xs-2',
      displayField: true,
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
      }]
    },
    {
      class: 'col-xs-3 ',
      displayField: true,
      cell: [{
        field: 'LastScan',
        title: 'context.hostSummary.lastUpdated'
      },
      {
        field: 'AdminStatus',
        title: 'context.hostSummary.adminStatus'
      },
      {
        field: 'LastSeen',
        title: 'context.hostSummary.lastLogin'
      }]
    },
    {
      class: 'col-xs-3',
      displayField: true,
      cell: [{
        field: 'MAC',
        title: 'context.hostSummary.macAddress'
      },
      {
        field: 'OperatingSystem',
        title: 'context.hostSummary.operatingSystem'
      },
      {
        field: '',
        displayField: true
      }]
    },
    {
      class: 'col-xs-2',
      displayField: true,
      cell: [{
        field: 'MachineStatus',
        title: 'context.hostSummary.machineStatus'
      },
      {
        field: 'IIOCRisk',
        title: 'context.hostSummary.riskScore'
      },
      {
        field: 'LocalIPAddress',
        title: 'context.hostSummary.ipAddress',
        nested: 'LocalIPAddress',
        dataType: 'link'
      },
      {
        field: '',
        displayField: true
      }]
    }
  ]
}];