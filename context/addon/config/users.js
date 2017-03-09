export default [{
  dataSourceGroup: 'Users',
  header: '',
  footer: '',
  title: 'context.ADdata.title',
  columns: [
    {
      cell: [{
        field: 'employeeID',
        title: 'context.ADdata.employeeID',
        dataType: 'horizontal',
        displayTitle: true,
        class: 'col-xs-3'
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
      displayField: true,
      cell: [{
        field: 'telephoneNumber',
        title: 'context.ADdata.phone',
        dataType: 'horizontal',
        displayTitle: true,
        class: 'col-xs-3'
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
      cell: [{
        field: 'mail',
        title: 'context.ADdata.email',
        dataType: 'horizontal',
        class: 'col-xs-3'
      },
      {
        field: 'groupName',
        title: 'context.ADdata.groups',
        dataType: 'group'
      },
      {
        field: 'lastLogon',
        title: 'context.ADdata.lastLogon'
      }]
    },
    {
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
}];