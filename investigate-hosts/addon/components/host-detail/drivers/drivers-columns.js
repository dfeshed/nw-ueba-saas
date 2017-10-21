const columnsConfig = [
  {
    field: 'fileName',
    title: 'File Name'
  },
  {
    field: 'timeCreated',
    title: 'FILE CREATION TIME',
    width: '10%',
    format: 'DATE'
  },
  {
    field: 'signature',
    title: 'Signature',
    format: 'SIGNATURE',
    width: '10%',
    disableSort: true
  },
  {
    field: 'machineCount',
    title: 'Machine Count',
    disableSort: true
  },
  {
    field: 'path',
    title: 'Path',
    width: '20%',
    disableSort: true
  }
];

export default columnsConfig;