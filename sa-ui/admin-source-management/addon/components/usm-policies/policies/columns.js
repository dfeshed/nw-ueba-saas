export default [
  {
    title: 'adminUsm.policies.list.select',
    class: 'rsa-form-row-checkbox',
    width: '40px',
    dataType: 'checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true
  },
  {
    field: 'name',
    title: 'adminUsm.policies.list.name',
    width: '15%',
    dataType: 'text',
    visible: true
  },
  {
    field: 'associatedGroups',
    title: 'adminUsm.policies.list.associatedGroups',
    width: '20%',
    dataType: 'text',
    visible: true
  },
  {
    field: 'description',
    title: 'adminUsm.policies.list.description',
    width: '20%',
    dataType: 'text',
    visible: true
  },
  {
    field: 'sourceType',
    title: 'adminUsm.policies.list.sourceType',
    width: '15%',
    dataType: 'text',
    visible: true
  },
  {
    field: 'publishStatus',
    title: 'adminUsm.policies.list.publishStatus',
    width: '15%',
    dataType: 'icon',
    visible: true
  }
];