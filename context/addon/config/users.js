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
        field: 'managerName',
        title: 'context.ADdata.manager'
      },
      {
        field: 'postalAddress',
        title: 'context.ADdata.postalAddress'
      }]
    },
    {
      class: 'col-xs-3',
      cell: [{
        field: 'mail',
        title: 'context.ADdata.email'
      },
      {
        field: 'groupName',
        title: 'context.ADdata.groups',
        dataType: 'group',
        count: 'groupCount'
      },
      {
        field: 'lastLogon',
        title: 'context.ADdata.lastLogon'
      }]
    },
    {
      class: 'col-xs-3',
      cell: [{
        field: 'adUserID',
        title: 'context.ADdata.adUserID',
        dataType: 'horizontal',
        class: 'col-xs-3'
      },
      {
        field: 'jobCodes',
        title: 'context.ADdata.jobCodes'
      },
      {
        field: 'lastLogonTimeStamp',
        title: 'context.ADdata.lastLogonTimeStamp'
      }]
    }
  ],

  firstRow: {
    class: 'rsa-context-panel__endpoint__host-details col-xs-2',
    field: 'fullName',
    title: 'context.hostSummary.riskScore'
  }
};