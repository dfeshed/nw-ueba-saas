export default [
  {
    title: 'live.search.fields.select',
    class: 'rsa-form-row-checkbox',
    width: 18,
    dataType: 'checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true
  },
  {
    field: 'title',
    title: 'live.search.fields.resourceName',
    width: 350
  },
  {
    field: 'created',
    title: 'live.search.fields.createdDate',
    dataType: 'date',
    width: 120
  },
  {
    field: 'updated',
    title: 'live.search.fields.updatedDate',
    dataType: 'date',
    width: 120
  },
  {
    field: 'resourceType.title',
    title: 'live.search.fields.resourceType',
    width: 225
  },
  {
    field: 'version',
    title: 'live.search.fields.version',
    width: 65
  }
];
