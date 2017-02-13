export default [{
  dataSourceGroup: 'Machines',
  header: 'context.incident.header',
  footer: '',
  width: '2',
  title: 'context.ADdata.title',
  columns: [{
    class: 'col-xs-2',
    displayField: false,
    cell: [{
      field: 'fullName'
    },
    {
      field: 'title'
    },
    {
      field: 'telephoneNumber'
    },
    {
      field: 'mail'
    }]
  },
  {
    class: 'col-xs-3 ',
    displayField: true,
    cell: [{
      field: 'employeeID',
      title: 'context.ADdata.employeeID'
    },
    {
      field: 'department',
      title: 'context.ADdata.department'
    },
    {
      field: 'postalAddress',
      title: 'context.ADdata.postalAddress'
    }]
  },
  {
    class: 'col-xs-3',
    displayField: true,
    cell: [{
      field: 'managerName',
      title: 'context.ADdata.manager'
    },
    {
      field: 'groupName',
      title: 'context.ADdata.groups'
    },
    {
      field: 'jobCodes',
      title: 'context.ADdata.jobCodes'
    }]
  }]
}];