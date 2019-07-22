const defaultColumns = [
  {
    'dataType': 'checkbox',
    'width': '2vw',
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
    width: '12vw'
  },
  {
    field: 'machineFileScore',
    title: 'investigateHosts.detailsColumns.machineFileScore',
    order: 5,
    width: '8vw'
  },
  {
    field: 'fileProperties.score',
    title: 'investigateHosts.detailsColumns.globalScore',
    order: 6,
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
    order: 8
  },
  {
    field: 'fileProperties.fileStatus',
    title: 'investigateHosts.detailsColumns.fileStatus',
    order: 9
  },
  {
    field: 'signature',
    title: 'investigateHosts.detailsColumns.signature',
    format: 'SIGNATURE',
    order: 4,
    width: '10vw'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'investigateHosts.detailsColumns.downloadInfo',
    label: 'investigateHosts.detailsColumns.downloadInfo',
    format: 'DOWNLOADSTATUS',
    width: 100,
    order: 10
  },
  {
    field: 'path',
    title: 'investigateHosts.detailsColumns.filePath',
    width: '20vw',
    order: 9
  },
  {
    field: 'displayName',
    title: 'investigateHosts.detailsColumns.displayName',
    width: '20vw',
    order: 2
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'name',
      title: 'investigateHosts.detailsColumns.serviceName',
      width: '10vw',
      order: 3
    },
    {
      field: 'timeCreated',
      title: 'investigateHosts.detailsColumns.timeCreated',
      format: 'DATE',
      width: '10vw',
      order: 12
    }
  ],
  windows: [
    {
      field: 'serviceName',
      title: 'investigateHosts.detailsColumns.serviceName',
      width: '10vw',
      order: 3
    },
    {
      field: 'state',
      title: 'investigateHosts.detailsColumns.state',
      width: '15vw',
      order: 11
    },
    {
      field: 'timeCreated',
      title: 'investigateHosts.detailsColumns.timeCreated',
      format: 'DATE',
      width: '10vw',
      order: 12
    }
  ],
  linux: [
    {
      field: 'fileName',
      title: 'investigateHosts.detailsColumns.serviceName',
      width: '10vw',
      order: 3
    },
    {
      field: 'status',
      title: 'investigateHosts.detailsColumns.state',
      width: '10vw',
      order: 11
    },
    {
      field: 'timeModified',
      title: 'investigateHosts.detailsColumns.timeModified',
      width: '15vw',
      order: 12
    },
    {
      field: 'type',
      title: 'investigateHosts.detailsColumns.type',
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
