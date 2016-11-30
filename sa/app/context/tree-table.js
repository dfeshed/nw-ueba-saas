export default{
  header: ' ',
  footer: '',
  title: 'context.list.title',
  columns: [
    {
      field: 'dataSourceName',
      title: 'context.list.dataSourceName',
      groupedColumn: true
    },
    {
      field: 'dataSourceDescription',
      title: 'context.list.dataSourceDescription',
      groupedColumn: true
    },
    {
      field: 'data',
      title: 'context.list.data',
      groupedColumn: false,
      groupdata: 'resultList',
      content: 'data'
    }
  ]
};

