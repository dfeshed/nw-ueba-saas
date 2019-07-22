const defaultColumns = [
  {
    'dataType': 'checkbox',
    'width': 20,
    'class': 'rsa-form-row-checkbox',
    'componentClass': 'rsa-form-checkbox',
    'visible': true,
    'disableSort': true,
    'headerComponentClass': 'rsa-form-checkbox',
    field: 'checkbox'
  },
  {
    field: 'fileName',
    title: 'investigateHosts.detailsColumns.fileName',
    format: 'FILENAME',
    order: 1,
    width: '10vw'
  },
  {
    field: 'machineFileScore',
    title: 'investigateHosts.detailsColumns.machineFileScore',
    order: 4,
    width: '8vw'
  },
  {
    field: 'fileProperties.score',
    title: 'investigateHosts.detailsColumns.globalScore',
    order: 5,
    width: '8vw'
  },
  {
    field: 'machineCount',
    title: 'investigateHosts.detailsColumns.machineCount',
    width: '6vw',
    disableSort: true,
    order: 7,
    visible: false
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'investigateHosts.detailsColumns.reputationStatus',
    order: 10
  },
  {
    field: 'fileProperties.fileStatus',
    title: 'investigateHosts.detailsColumns.fileStatus',
    width: '8vw',
    order: 11
  },
  {
    field: 'signature',
    title: 'investigateHosts.detailsColumns.signature',
    format: 'SIGNATURE',
    order: 3,
    width: '10vw'
  },
  {
    field: 'checksumSha256',
    title: 'investigateHosts.detailsColumns.hash',
    order: 12,
    width: '22vw'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'investigateHosts.detailsColumns.downloadInfo',
    label: 'investigateHosts.detailsColumns.downloadInfo',
    format: 'DOWNLOADSTATUS',
    width: 100,
    order: 13
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'triggerString',
      title: 'investigateHosts.detailsColumns.triggerString',
      order: 15,
      width: '18vw'
    }
  ],
  windows: [
    {
      field: 'triggerString',
      title: 'investigateHosts.detailsColumns.triggerString',
      order: 15,
      width: '18vw'
    },
    {
      field: 'creatorUser',
      title: 'investigateHosts.detailsColumns.creatorUser',
      order: 8,
      width: '15vw'
    },
    {
      field: 'executeUser',
      title: 'investigateHosts.detailsColumns.executeUser',
      order: 9
    },
    {
      field: 'name',
      title: 'investigateHosts.detailsColumns.taskName',
      order: 2,
      width: '20vw'
    },
    {
      field: 'launchArguments',
      title: 'investigateHosts.detailsColumns.launchArguments',
      order: 6,
      width: '18vw'
    },
    {
      field: 'status',
      title: 'investigateHosts.detailsColumns.status',
      order: 14,
      width: '14vw'
    }
  ],
  linux: [
    {
      field: 'triggerString',
      title: 'investigateHosts.detailsColumns.triggerString',
      order: 15,
      width: '18vw'
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
