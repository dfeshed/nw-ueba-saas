export default {
  dataSourceGroup: 'LIST',
  header: 'context.list.title',
  footer: '',
  title: 'context.list.title',
  columns: [
    {
      field: 'dataSourceName',
      title: 'context.list.dataSourceName',
      nested: '',
      width: '100'
    },
    {
      field: 'dataSourceDescription',
      title: 'context.list.dataSourceDescription',
      width: '100',
      nested: ''
    },
    {
      field: 'createdBy',
      title: 'context.list.createdByUser',
      width: '100',
      nested: ''
    },
    {
      field: 'dataSourceCreatedOn',
      title: 'context.list.createdTimeStamp',
      width: '100',
      dataType: 'datetime',
      nested: ''
    },
    {
      field: 'dataSourceLastModifiedOn',
      title: 'context.list.lastModifiedTimeStamp',
      width: '80',
      nested: '',
      dataType: 'datetime'
    }
  ]
};