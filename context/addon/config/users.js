export default {
  class: 'rsa-context-panel__grid__ad-details',
  dataSourceGroup: 'Users',
  header: '',
  headerRequired: false,
  footerRequired: true,
  footer: '',
  title: 'context.ADdata.title',
  rows: [{
    cell: [{
      field: 'displayName',
      title: 'context.ADdata.displayName'
    },
    {
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
    }]
  },
  {
    cell: [{
      field: 'sAMAccountName',
      title: 'context.ADdata.adUserID'
    },
    {
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
    }]
  },
  {
    cell: [ {
      field: 'company',
      title: 'context.ADdata.company'
    },
    {
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
    }]
  },
  {
    cell: [{
      field: 'lastLogonTimestamp',
      title: 'context.ADdata.lastLogonTimeStamp',
      dataType: 'datetime'
    },
    {
      field: 'distinguishedName',
      title: 'context.ADdata.distinguishedName'
    }
    ]
  }]
};