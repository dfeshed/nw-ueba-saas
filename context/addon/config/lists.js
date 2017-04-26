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
      width: '20vh'
    },
    {
      field: 'dataSourceDescription',
      title: 'context.list.dataSourceDescription',
      width: '20vh'
    },
    {
      field: 'resultMeta.dataSourceCreatedBy',
      title: 'context.list.createdByUser',
      width: '20vh'
    },
    {
      field: 'dataSourceCreatedOn',
      title: 'context.list.createdTimeStamp',
      width: '20vh',
      dataType: 'datetime'
    },
    {
      field: 'dataSourceLastModifiedOn',
      title: 'context.list.lastModifiedTimeStamp',
      width: '20vh',
      dataType: 'datetime'
    }
  ]
};