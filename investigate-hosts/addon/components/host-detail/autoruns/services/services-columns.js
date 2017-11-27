const defaultColumns = [
  {
    field: 'signature',
    title: 'SIGNATURE',
    format: 'SIGNATURE',
    width: '10%',
    disableSort: true
  },
  {
    field: 'path',
    title: 'FILE PATH',
    width: '20%'
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'name',
      title: 'SERVICE NAME',
      width: '10%'
    },
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '10%'
    }
  ],
  windows: [
    {
      field: 'serviceName',
      title: 'SERVICE NAME',
      width: '10%'
    },
    {
      field: 'state',
      title: 'RUNNING STATUS',
      width: '15%'
    },
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '15%'
    }
  ],
  linux: [
    {
      field: 'fileName',
      title: 'SERVICE NAME',
      width: '10%'
    },
    {
      field: 'status',
      title: 'RUNNING STATUS',
      width: '15%'
    },
    {
      field: 'timeModified',
      title: 'LAST MODIFIED TIME',
      width: '15%',
      format: 'DATE'
    },
    {
      field: 'type',
      title: 'Type'
    }
  ]
};

const _generateColumns = function(columns) {
  for (const key in columns) {
    if (columns.hasOwnProperty(key)) {
      columns[key] = [...columns[key], ...defaultColumns];
    }
  }
  return columns;
};

columnsConfig = _generateColumns(columnsConfig);

export default columnsConfig;