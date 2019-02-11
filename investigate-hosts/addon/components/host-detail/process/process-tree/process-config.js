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
    width: '7vw',
    title: 'investigateHosts.process.localRiskScore',
    isDescending: false,
    disableSort: true
  },
  {
    field: 'fileProperties.score',
    width: '7vw',
    title: 'investigateHosts.process.globalRiskScore',
    disableSort: true
  },
  {
    field: 'machineCount',
    title: 'investigateHosts.process.machineCount',
    width: '6vw',
    disableSort: true,
    visible: false
  },
  {
    field: 'fileProperties.reputationStatus',
    width: '6vw',
    title: 'investigateHosts.process.reputationStatus',
    disableSort: true
  },
  {
    field: 'fileProperties.fileStatus',
    width: '4vw',
    title: 'investigateHosts.process.fileStatus',
    disableSort: true
  },
  {
    field: 'fileProperties.signature.features',
    width: '12vw',
    title: 'investigateHosts.process.signature',
    disableSort: true
  },
  {
    field: 'fileProperties.downloadInfo',
    width: '6vw',
    format: 'DOWNLOADSTATUS',
    title: 'investigateHosts.process.downloaded',
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
    field: 'pid',
    width: '3vw',
    title: 'investigateHosts.process.pid',
    disableSort: true
  }
];
