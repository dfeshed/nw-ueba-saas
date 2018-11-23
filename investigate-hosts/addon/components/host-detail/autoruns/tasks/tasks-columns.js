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
    format: 'FILENAME'
  },
  {
    field: 'fileProperties.score',
    title: 'Risk Score'
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'Reputation Status'
  },
  {
    field: 'fileProperties.fileStatus',
    title: 'File Status',
    width: '15%'
  },
  {
    field: 'checksumSha256',
    title: 'HASH'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'downloaded',
    label: 'investigateHosts.files.fields.downloaded',
    disableSort: true,
    format: 'DOWNLOADSTATUS',
    width: 100
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'triggerString',
      title: 'TRIGGER'
    }
  ],
  windows: [
    {
      field: 'lastRunTime',
      title: 'Last Run Time',
      format: 'DATE'
    },
    {
      field: 'nextRunTime',
      title: 'Next Run Time',
      format: 'DATE',
      width: '15%'
    },
    {
      field: 'triggerString',
      title: 'TRIGGER'
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
