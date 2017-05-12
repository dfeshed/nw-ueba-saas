export default {
  class: 'rsa-context-panel__grid__archer-details',
  dataSourceGroup: 'Archer',
  timeWindowRequired: true,
  header: '',
  footer: '',
  title: 'context.archer.title',
  columns: [
    {
      class: 'col-xs-3',
      cell: [{
        field: 'Criticality Rating',
        title: 'context.archer.criticalityRating'
      },
      {
        field: 'IP Address',
        title: 'context.archer.ipAddress'
      },
      {
        field: 'Facilities',
        title: 'context.archer.facility',
        count: 'facilitiesCount'
      }]
    },
    {
      class: 'col-xs-3',
      cell: [{
        field: 'Risk Rating',
        title: 'context.archer.riskRating'
      },
      {
        field: 'Device ID',
        title: 'context.archer.deviceId'
      },
      {
        field: 'Device Owner',
        count: 'deviceCount',
        title: 'context.archer.deviceOwner'
      }]
    },
    {
      class: 'col-xs-3',
      cell: [{
        field: 'Device Name',
        title: 'context.archer.deviceName'
      },
      {
        field: 'Type',
        title: 'context.archer.deviceType'
      },
      {
        field: '',
        displayField: true
      }]
    },
    {
      class: 'col-xs-3',
      cell: [{
        field: 'Host Name',
        title: 'context.archer.hostName'
      },
      {
        field: 'Business Unit',
        count: 'businessUnitCount',
        title: 'context.archer.businessUnit'
      },
      {
        field: '',
        displayField: true
      }]
    }
  ]
};