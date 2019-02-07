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
    format: 'FILENAME',
    order: 1
  },
  {
    field: 'machineFileScore',
    title: 'Local Risk Score',
    order: 4
  },
  {
    field: 'fileProperties.score',
    title: 'Global Risk Score',
    order: 5
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
    order: 10
  },
  {
    field: 'fileProperties.fileStatus',
    title: 'File Status',
    width: '15vw',
    order: 11
  },
  {
    field: 'signature',
    title: 'SIGNATURE',
    format: 'SIGNATURE',
    order: 3
  },
  {
    field: 'checksumSha256',
    title: 'HASH',
    order: 12
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'downloaded',
    label: 'investigateHosts.files.fields.downloaded',
    format: 'DOWNLOADSTATUS',
    width: 100,
    order: 13
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'triggerString',
      title: 'TRIGGER',
      order: 15
    }
  ],
  windows: [
    {
      field: 'triggerString',
      title: 'TRIGGER',
      order: 15
    },
    {
      field: 'creatorUser',
      title: 'CREATOR USER',
      order: 8
    },
    {
      field: 'executeUser',
      title: 'EXECUTE USER',
      order: 9
    },
    {
      field: 'name',
      title: 'TASK NAME',
      order: 2
    },
    {
      field: 'launchArguments',
      title: 'LAUNCH ARGUMENTS',
      order: 6
    },
    {
      field: 'status',
      title: 'STATUS',
      order: 14
    }
  ],
  linux: [
    {
      field: 'triggerString',
      title: 'TRIGGER',
      order: 15
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
