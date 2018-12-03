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
    field: 'pid',
    width: '4vw',
    title: 'investigateHosts.process.pid',
    disableSort: true
  },
  {
    field: 'score',
    width: '5vw',
    title: 'investigateHosts.process.riskScore',
    disableSort: true
  },
  {
    field: 'fileStatus',
    width: '5vw',
    title: 'investigateHosts.process.fileStatus',
    disableSort: true
  },
  {
    field: 'reputationStatus',
    width: '8vw',
    title: 'investigateHosts.process.reputationStatus',
    disableSort: true
  },
  {
    field: 'launchArguments',
    width: '10vw',
    title: 'investigateHosts.process.launchArguments',
    isDescending: false,
    disableSort: true
  },
  {
    field: 'downloadInfo.status',
    width: '6vw',
    format: 'DOWNLOADSTATUS',
    title: 'investigateHosts.process.downloaded',
    disableSort: true
  },
  {
    field: 'signature.features',
    width: '10vw',
    title: 'investigateHosts.process.signature',
    disableSort: true
  },
  {
    field: 'path',
    width: '20vw',
    title: 'investigateHosts.process.filePath',
    disableSort: true
  }
];
