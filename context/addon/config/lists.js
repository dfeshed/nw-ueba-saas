export default {
  dataSourceGroup: 'LIST',
  header: 'context.list.title',
  footer: '',
  title: 'context.list.title',
  sortColumn: 'dataSourceLastModifiedOn',
  sortOrder: 'descending',
  columns: [
    {
      field: 'dataSourceName',
      title: 'context.list.dataSourceName',
      width: '100'
    },
    {
      field: 'dataSourceDescription',
      title: 'context.list.dataSourceDescription',
      width: '100'
    },
    {
      field: 'resultMeta.dataSourceCreatedBy',
      title: 'context.list.createdByUser',
      width: '100'
    },
    {
      field: 'dataSourceCreatedOn',
      title: 'context.list.createdTimeStamp',
      width: '100',
      dataType: 'datetime'
    },
    {
      field: 'dataSourceLastModifiedOn',
      title: 'context.list.lastModifiedTimeStamp',
      width: '80',
      dataType: 'datetime'
    }
  ]
};