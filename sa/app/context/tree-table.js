export default{
  header: ' ',
  footer: '',
  title: 'context.list.title',
  columns: [
    {
      field1: 'dataSourceName',
      field2: 'connectionName',
      title: 'context.list.dataSourceName',
      groupedColumn: true
    },
    {
      field1: 'dataSourceDescription',
      title: 'context.list.dataSourceDescription',
      groupedColumn: true
    },
    {
      field1: 'data',
      title: 'context.list.data',
      groupedColumn: false,
      groupdata: 'resultList',
      content: 'data'
    }
  ]
};

