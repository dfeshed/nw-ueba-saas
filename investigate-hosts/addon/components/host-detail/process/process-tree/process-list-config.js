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
    width: '15vw',
    title: 'investigateHosts.process.processName',
    isDescending: false
  },
  {
    field: 'machineFileScore',
    width: '4vw',
    title: 'investigateHosts.process.localRiskScore',
    isDescending: false
  },
  {
    field: 'fileProperties.score',
    width: '4vw',
    title: 'investigateHosts.process.globalRiskScore',
    isDescending: false
  },
  {
    field: 'machineCount',
    title: 'investigateHosts.process.machineCount',
    width: '6vw',
    disableSort: true
  },
  {
    field: 'fileProperties.reputationStatus',
    width: '8vw',
    title: 'investigateHosts.process.reputationStatus',
    isDescending: false
  },
  {
    field: 'fileProperties.fileStatus',
    width: '5vw',
    title: 'investigateHosts.process.fileStatus',
    isDescending: false
  },
  {
    field: 'fileProperties.signature.features',
    width: '10vw',
    title: 'investigateHosts.process.signature',
    isDescending: false
  },
  {
    field: 'fileProperties.downloadInfo',
    width: '6vw',
    format: 'DOWNLOADSTATUS',
    title: 'investigateHosts.process.downloaded',
    isDescending: false
  },
  {
    field: 'path',
    width: '20vw',
    title: 'investigateHosts.process.filePath',
    isDescending: false
  },
  {
    field: 'createTime',
    title: 'investigateHosts.process.creationTime',
    format: 'DATE',
    width: '10vw'
  },
  {
    field: 'launchArguments',
    width: '10vw',
    title: 'investigateHosts.process.launchArguments',
    isDescending: false
  },
  {
    field: 'pid',
    width: '4vw',
    title: 'investigateHosts.process.pid',
    isDescending: false
  }
];
