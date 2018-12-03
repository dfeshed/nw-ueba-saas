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
    field: 'pid',
    width: '4vw',
    title: 'investigateHosts.process.pid',
    isDescending: false
  },
  {
    field: 'score',
    width: '6vw',
    title: 'investigateHosts.process.riskScore',
    isDescending: false
  },
  {
    field: 'fileStatus',
    width: '5vw',
    title: 'investigateHosts.process.fileStatus',
    isDescending: false
  },
  {
    field: 'reputationStatus',
    width: '8vw',
    title: 'investigateHosts.process.reputationStatus',
    isDescending: false
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
    title: 'investigateHosts.process.downloaded',
    isDescending: false
  },
  {
    field: 'signature.features',
    width: '10vw',
    title: 'investigateHosts.process.signature',
    isDescending: false
  },
  {
    field: 'path',
    width: '20vw',
    title: 'investigateHosts.process.filePath',
    isDescending: false
  }
];
