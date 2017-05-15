export default {
  class: 'rsa-context-panel__grid__archer-details',
  dataSourceGroup: 'Archer',
  timeWindowRequired: true,
  header: '',
  footer: '',
  title: 'context.archer.title',
  rows: [{
    cell: [{
      field: 'Criticality Rating',
      title: 'context.archer.criticalityRating'
    },
    {
      field: 'Device ID',
      title: 'context.archer.deviceId'
    },
    {
      field: 'Device Owner',
      count: 'deviceCount',
      title: 'context.archer.deviceOwner'
    },
    {
      field: 'Risk Rating',
      title: 'context.archer.riskRating'
    }]
  },
  {
    cell: [{
      field: 'IP Address',
      title: 'context.archer.ipAddress'
    },
    {
      field: 'Type',
      title: 'context.archer.deviceType'
    },
    {
      field: 'Device Name',
      title: 'context.archer.deviceName'
    },
    {
      field: 'Business Unit',
      count: 'businessUnitCount',
      title: 'context.archer.businessUnit'
    }]
  },
  {
    cell: [{
      field: 'Host Name',
      title: 'context.archer.hostName'
    },
    {
      field: 'Facilities',
      title: 'context.archer.facility',
      count: 'facilitiesCount'
    }]
  }]
};
