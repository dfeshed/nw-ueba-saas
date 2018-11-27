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
    componentClass: 'host-detail/process/process-tree/tree-name'
  },
  {
    field: 'pid',
    width: '4vw',
    title: 'investigateHosts.process.pid'
  },
  {
    field: 'score',
    width: '5vw',
    title: 'investigateHosts.process.riskScore'
  },
  {
    field: 'fileStatus',
    width: '5vw',
    title: 'investigateHosts.process.fileStatus'
  },
  {
    field: 'reputationStatus',
    width: '8vw',
    title: 'investigateHosts.process.reputationStatus'
  },
  {
    field: 'launchArguments',
    width: '10vw',
    title: 'investigateHosts.process.launchArguments',
    isDescending: false
  },
  {
    field: 'downloadInfo.status',
    width: '6vw',
    format: 'DOWNLOADSTATUS',
    title: 'investigateHosts.process.downloaded'
  },
  {
    field: 'signature.features',
    width: '10vw',
    title: 'investigateHosts.process.signature'
  },
  {
    field: 'path',
    width: '20vw',
    title: 'investigateHosts.process.filePath'
  }
];
