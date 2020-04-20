const HW_COLUMNS = [
  {
    title: '',
    class: 'rsa-form-row-checkbox',
    width: '1vw',
    dataType: 'checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true,
    headerComponentClass: 'rsa-form-checkbox'
  },
  {
    field: 'monitor',
    width: '20vw',
    title: 'Monitor Name'
  },
  {
    field: 'trigger',
    width: '15vw',
    title: 'Trigger Name',
    disableSort: true
  },
  {
    field: 'severity',
    width: '10vw',
    title: 'Severity',
    disableSort: true
  },
  {
    field: 'suppressionConfigured',
    width: '10vw',
    title: 'Suppression',
    status: 'suppressionStatus',
    disableSort: true
  }
];

export {
  HW_COLUMNS
};
