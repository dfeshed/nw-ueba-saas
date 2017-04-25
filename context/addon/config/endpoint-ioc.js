export default {
  dataSourceGroup: 'IOC',
  header: 'context.iiocs.header',
  footer: '',
  title: 'context.iiocs.title',
  sortColumn: 'IOCLevel',
  columns: [
    {
      field: 'LastExecuted',
      title: 'context.iiocs.lastExecuted',
      width: '200',
      class: 'rsa-iioc-iiocLevel2',
      dataType: 'text'
    },
    {
      field: 'IOCLevel',
      title: 'context.iiocs.iOCLevel',
      width: '200',
      class: 'rsa-iioc-iiocLevel1',
      dataType: 'text'
    },
    {
      field: 'Description',
      title: 'context.iiocs.description',
      width: '200',
      class: 'rsa-iioc-iiocLevel0',
      dataType: 'text'
    }
  ]
};
