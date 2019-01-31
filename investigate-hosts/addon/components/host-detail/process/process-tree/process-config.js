export default [
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
    field: 'name',
    dataType: 'tree-column',
    width: '18vw',
    title: 'investigateHosts.process.processName',
    componentClass: 'host-detail/process/process-tree/tree-name',
    disableSort: true
  },
  {
    field: 'machineFileScore',
    width: '4vw',
    title: 'investigateHosts.process.riskScore',
    isDescending: false,
    disableSort: true
  },
  {
    field: 'fileProperties.score',
    width: '4vw',
    title: 'investigateHosts.process.globalScore',
    disableSort: true
  },
  {
    field: 'machineCount',
    title: 'investigateHosts.process.machineCount',
    width: '6vw',
    disableSort: true
  },
  {
    field: 'fileProperties.fileStatus',
    width: '4vw',
    title: 'investigateHosts.process.fileStatus',
    disableSort: true
  },
  {
    field: 'fileProperties.reputationStatus',
    width: '6vw',
    title: 'investigateHosts.process.reputationStatus',
    disableSort: true
  },
  {
    field: 'launchArguments',
    width: '20vw',
    title: 'investigateHosts.process.launchArguments',
    isDescending: false,
    disableSort: true
  },
  {
    field: 'path',
    width: '16vw',
    title: 'investigateHosts.process.filePath',
    disableSort: true
  },
  {
    field: 'fileProperties.signature.features',
    width: '8vw',
    title: 'investigateHosts.process.signature',
    disableSort: true
  },
  {
    field: 'fileProperties.downloadInfo',
    width: '4vw',
    format: 'DOWNLOADSTATUS',
    title: 'investigateHosts.process.downloaded',
    disableSort: true
  },
  {
    field: 'pid',
    width: '3vw',
    title: 'investigateHosts.process.pid',
    disableSort: true
  }
];
