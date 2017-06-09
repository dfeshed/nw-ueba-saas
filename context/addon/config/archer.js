export default {
  class: 'rsa-context-panel__grid__archer-details',
  dataSourceGroup: 'Archer',
  headerRequired: false,
  footerRequired: true,
  header: '',
  footer: '',
  title: 'context.archer.title',
  rows: [{
    cell: [{
      field: 'Criticality Rating',
      title: 'context.archer.criticalityRating'
    },
    {
      field: 'Risk Rating',
      title: 'context.archer.riskRating'
    },
    {
      field: 'Device Name',
      title: 'context.archer.deviceName'
    },
    {
      field: 'Host Name',
      title: 'context.archer.hostName'
    }]
  },
  {
    cell: [{
      field: 'IP Address',
      title: 'context.archer.ipAddress'
    },
    {
      field: 'Device ID',
      title: 'context.archer.deviceId'
    },
    {
      field: 'Type',
      title: 'context.archer.deviceType'
    },
    {
      field: 'Facilities',
      title: 'context.archer.facility',
      count: 'facilitiesCount',
      dataType: 'group',
      displayType: 'inline'
    }]
  },
  {
    cell: [{
      field: 'Business Unit',
      count: 'businessUnitCount',
      title: 'context.archer.businessUnit',
      dataType: 'group',
      displayType: 'inline'
    },
    {
      field: 'Device Owner',
      count: 'deviceCount',
      title: 'context.archer.deviceOwner',
      dataType: 'group',
      displayType: 'inline'
    }]
  }]
};
