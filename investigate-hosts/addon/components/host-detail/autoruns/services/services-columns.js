const defaultColumns = [
  {
    'dataType': 'checkbox',
    'width': '2vw',
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
    field: 'machineFileScore',
    title: 'Local Risk Score'
  },
  {
    field: 'fileProperties.score',
    title: 'Global Risk Score'
  },
  {
    field: 'machineCount',
    title: 'Active On',
    width: '6vw',
    disableSort: true
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'Reputation'
  },
  {
    field: 'fileProperties.fileStatus',
    title: 'File Status'
  },
  {
    field: 'signature',
    title: 'SIGNATURE',
    format: 'SIGNATURE'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'downloaded',
    label: 'investigateHosts.files.fields.downloaded',
    format: 'DOWNLOADSTATUS',
    width: 100
  },
  {
    field: 'path',
    title: 'FILE PATH',
    width: '20vw'
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'name',
      title: 'SERVICE NAME',
      width: '10vw'
    },
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '10vw'
    }
  ],
  windows: [
    {
      field: 'serviceName',
      title: 'SERVICE NAME',
      width: '10vw'
    },
    {
      field: 'state',
      title: 'RUNNING STATUS',
      width: '15vw'
    },
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '10vw'
    }
  ],
  linux: [
    {
      field: 'fileName',
      title: 'SERVICE NAME',
      width: '10vw'
    },
    {
      field: 'status',
      title: 'RUNNING STATUS',
      width: '10vw'
    },
    {
      field: 'timeModified',
      title: 'LAST MODIFIED TIME',
      width: '15vw'
    },
    {
      field: 'type',
      title: 'Type',
      width: '10vw'
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
