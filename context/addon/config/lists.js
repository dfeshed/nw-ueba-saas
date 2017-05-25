export default {
  dataSourceGroup: 'LIST',
  header: 'context.list.title',
  footer: '',
  title: 'context.list.title',
  headerRequired: false,
  footerRequired: true,
  sortColumn: 'dataSourceLastModifiedOn',
  sortOrder: 'desc',
  columns: [
    {
      field: 'dataSourceName',
      title: 'context.list.dataSourceName',
      width: '20vh',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'dataSourceDescription',
      title: 'context.list.dataSourceDescription',
      width: '20vh',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'resultMeta.dataSourceCreatedBy',
      title: 'context.list.createdByUser',
      width: '20vh',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'dataSourceCreatedOn',
      title: 'context.list.createdTimeStamp',
      width: '20vh',
      dataType: 'datetime',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'dataSourceLastModifiedOn',
      title: 'context.list.lastModifiedTimeStamp',
      width: '20vh',
      dataType: 'datetime',
      icon: 'arrow-down-8',
      className: 'sort'
    }
  ]
};