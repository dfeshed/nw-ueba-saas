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
    field: 'fileName',
    title: 'NAME',
    width: '15%'
  },
  {
    field: 'checksumSha256',
    title: 'HASH',
    width: '10%'
  },
  {
    field: 'fileProperties.fileStatus',
    title: 'File Status'
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'triggerString',
      title: 'TRIGGER',
      width: '10%'
    }
  ],
  windows: [
    {
      field: 'lastRunTime',
      title: 'Last Run Time',
      format: 'DATE',
      width: '15%'
    },
    {
      field: 'nextRunTime',
      title: 'Next Run Time',
      format: 'DATE',
      width: '15%'
    },
    {
      field: 'triggerString',
      title: 'TRIGGER',
      width: '10%'
    }
  ],
  linux: [
    {
      field: 'triggerString',
      title: 'TRIGGER'
    }
  ]
};

const _generateColumns = function(columns) {
  for (const key in columns) {
    if (columns.hasOwnProperty(key)) {
      columns[key] = [ ...defaultColumns, ...columns[key]];
    }
  }
  return columns;
};

columnsConfig = _generateColumns(columnsConfig);

export default columnsConfig;