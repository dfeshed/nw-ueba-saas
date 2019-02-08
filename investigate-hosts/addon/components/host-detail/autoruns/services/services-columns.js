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
    format: 'FILENAME',
    order: 1,
    width: '12vw'
  },
  {
    field: 'machineFileScore',
    title: 'Local Risk Score',
    order: 5,
    width: '8vw'
  },
  {
    field: 'fileProperties.score',
    title: 'Global Risk Score',
    order: 6,
    width: '8vw'
  },
  {
    field: 'machineCount',
    title: 'Active On',
    width: '6vw',
    disableSort: true,
    order: 7
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'Reputation',
    order: 8
  },
  {
    field: 'fileProperties.fileStatus',
    title: 'File Status',
    order: 9
  },
  {
    field: 'signature',
    title: 'SIGNATURE',
    format: 'SIGNATURE',
    order: 4,
    width: '10vw'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'downloaded',
    label: 'investigateHosts.files.fields.downloaded',
    format: 'DOWNLOADSTATUS',
    width: 100,
    order: 10
  },
  {
    field: 'path',
    title: 'FILE PATH',
    width: '20vw',
    order: 9
  },
  {
    field: 'displayName',
    title: 'DISPLAY NAME',
    width: '20vw',
    order: 2
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'name',
      title: 'SERVICE NAME',
      width: '10vw',
      order: 3
    },
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '10vw',
      order: 12
    }
  ],
  windows: [
    {
      field: 'serviceName',
      title: 'SERVICE NAME',
      width: '10vw',
      order: 3
    },
    {
      field: 'state',
      title: 'RUNNING STATUS',
      width: '15vw',
      order: 11
    },
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '10vw',
      order: 12
    }
  ],
  linux: [
    {
      field: 'fileName',
      title: 'SERVICE NAME',
      width: '10vw',
      order: 3
    },
    {
      field: 'status',
      title: 'RUNNING STATUS',
      width: '10vw',
      order: 11
    },
    {
      field: 'timeModified',
      title: 'LAST MODIFIED TIME',
      width: '15vw',
      order: 12
    },
    {
      field: 'type',
      title: 'Type',
      width: '10vw',
      order: 13
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
