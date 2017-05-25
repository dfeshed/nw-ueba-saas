export default {
  dataSourceGroup: 'IOC',
  header: 'context.iiocs.header',
  headerRequired: true,
  footerRequired: false,
  footer: '',
  title: 'context.iiocs.title',
  sortColumn: 'IOCLevel',
  columns: [
    {
      field: 'IOCLevel',
      title: 'context.iiocs.iOCLevel',
      width: '10vh',
      class: 'rsa-iioc-iiocLevel1',
      dataType: 'text',
      icon: 'arrow-up-8',
      className: 'sort'
    },
    {
      field: 'Description',
      title: 'context.iiocs.description',
      width: '30vh',
      class: 'rsa-iioc-iiocLevel0',
      dataType: 'text',
      icon: 'arrow-down-8',
      className: 'sort'
    },
    {
      field: 'LastExecuted',
      title: 'context.iiocs.lastExecuted',
      width: '20vh',
      class: 'rsa-iioc-iiocLevel2',
      dataType: 'text',
      icon: 'arrow-down-8',
      className: 'sort'
    }
  ]
};
