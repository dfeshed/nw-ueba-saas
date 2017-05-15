export default {
  class: 'rsa-context-panel__grid__ad-details',
  dataSourceGroup: 'Users',
  header: '',
  timeWindowRequired: true,
  footer: '',
  title: 'context.ADdata.title',
  rows: [{
    cell: [{
      field: 'employeeID',
      title: 'context.ADdata.employeeID'
    },
    {
      field: 'telephoneNumber',
      title: 'context.ADdata.phone'
    },
    {
      field: 'mail',
      title: 'context.ADdata.email'
    },
    {
      field: 'sAMAccountName',
      title: 'context.ADdata.adUserID'
    }]
  },
  {
    cell: [{
      field: 'title',
      title: 'context.ADdata.jobTitle'
    },
    {
      field: 'manager',
      title: 'context.ADdata.manager'
    },
    {
      field: 'memberOf',
      title: 'context.ADdata.groups',
      dataType: 'group',
      count: 'groupCount'
    },
    {
      field: 'company',
      title: 'context.ADdata.company'
    }]
  },
  {
    cell: [{
      field: 'department',
      title: 'context.ADdata.department'
    },
    {
      field: 'location',
      title: 'context.ADdata.location'
    },
    {
      field: 'lastLogon',
      title: 'context.ADdata.lastLogon',
      dataType: 'datetime'
    },
    {
      field: 'lastLogonTimestamp',
      title: 'context.ADdata.lastLogonTimeStamp',
      dataType: 'datetime'
    }]
  },
  {
    cell: [{
      field: 'distinguishedName',
      title: 'context.ADdata.distinguishedName'
    }]
  }],
  firstRow: {
    class: 'rsa-context-panel__endpoint__host-details col-xs-2',
    field: 'displayName',
    title: 'context.hostSummary.riskScore'
  }
};