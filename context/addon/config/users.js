export default {
  class: 'rsa-context-panel__grid__ad-details',
  dataSourceGroup: 'Users',
  header: '',
  footer: '',
  title: 'context.ADdata.title',
  columns: [
    {
      class: 'col-xs-3',
      cell: [{
        field: 'employeeID',
        title: 'context.ADdata.employeeID'
      },
      {
        field: 'title',
        title: 'context.ADdata.jobTitle'
      },
      {
        field: 'department',
        title: 'context.ADdata.department'
      },
      {
        field: 'distinguishedName',
        title: 'context.ADdata.distinguishedName'
      }]
    },
    {
      class: 'col-xs-3',
      displayField: true,
      cell: [{
        field: 'telephoneNumber',
        title: 'context.ADdata.phone'
      },
      {
        field: 'manager',
        title: 'context.ADdata.manager'
      },
      {
        field: 'location',
        title: 'context.ADdata.location'
      }]
    },
    {
      class: 'col-xs-3',
      cell: [{
        field: 'mail',
        title: 'context.ADdata.email'
      },
      {
        field: 'memberOf',
        title: 'context.ADdata.groups',
        dataType: 'group',
        count: 'groupCount'
      },
      {
        field: 'lastLogon',
        title: 'context.ADdata.lastLogon',
        dataType: 'datetime'
      }]
    },
    {
      class: 'col-xs-3',
      cell: [{
        field: 'sAMAccountName',
        title: 'context.ADdata.adUserID',
        dataType: 'horizontal',
        class: 'col-xs-3'
      },
      {
        field: 'company',
        title: 'context.ADdata.company'
      },
      {
        field: 'lastLogonTimestamp',
        title: 'context.ADdata.lastLogonTimeStamp',
        dataType: 'datetime'
      }]
    }
  ],

  firstRow: {
    class: 'rsa-context-panel__endpoint__host-details col-xs-2',
    field: 'displayName',
    title: 'context.hostSummary.riskScore'
  }
};