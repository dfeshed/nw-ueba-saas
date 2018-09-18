const defaultColumns = [
  {
    'dataType': 'checkbox',
    'width': 20,
    'class': 'rsa-form-row-checkbox',
    'componentClass': 'rsa-form-checkbox',
    'visible': true,
    'disableSort': true,
    'headerComponentClass': 'rsa-form-checkbox'
  },
  {
    field: 'signature',
    title: 'SIGNATURE',
    format: 'SIGNATURE',
    width: '10%'
  },
  {
    field: 'fileProperties.fileStatus',
    title: 'File Status'
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
      format: 'DATE'
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
      format: 'DATE'
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
      width: '15%'
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
      columns[key] = [...defaultColumns, ...columns[key]];
    }
  }
  return columns;
};

columnsConfig = _generateColumns(columnsConfig);

export default columnsConfig;