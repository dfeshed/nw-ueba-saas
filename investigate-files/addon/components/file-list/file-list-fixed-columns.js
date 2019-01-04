export default [
  {
    dataType: 'checkbox',
    width: 20,
    class: 'rsa-form-row-checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true,
    headerComponentClass: 'rsa-form-checkbox'
  },
  {
    dataType: 'string',
    width: 200,
    visible: true,
    field: 'firstFileName',
    searchable: true,
    title: 'investigateFiles.fields.firstFileName'
  },
  {
    dataType: 'string',
    width: 100,
    visible: true,
    field: 'score',
    searchable: false,
    title: 'investigateFiles.fields.score'
  },
  {
    dataType: 'string',
    width: 100,
    visible: true,
    field: 'machineCount',
    searchable: false,
    disableSort: true,
    title: 'investigateFiles.fields.machineCount'
  }
];