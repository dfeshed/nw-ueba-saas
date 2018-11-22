export default [
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
    field: 'name',
    dataType: 'tree-column',
    width: 265,
    title: 'investigateHosts.process.processName',
    isDescending: false
  },
  {
    field: 'pid',
    width: 100,
    title: 'investigateHosts.process.pid',
    isDescending: false
  },
  {
    field: 'score',
    width: 200,
    title: 'investigateHosts.process.riskScore',
    isDescending: false
  },
  {
    field: 'fileStatus',
    width: 200,
    title: 'investigateHosts.process.fileStatus',
    isDescending: false
  },
  {
    field: 'reputationStatus',
    width: 200,
    title: 'investigateHosts.process.reputationStatus',
    isDescending: false
  },
  {
    field: 'downloadInfo.status',
    width: 200,
    format: 'DOWNLOADSTATUS',
    title: 'investigateHosts.process.downloaded',
    isDescending: false
  },
  {
    field: 'signature.features',
    width: 200,
    title: 'investigateHosts.process.signature',
    isDescending: false
  },
  {
    field: 'path',
    width: 200,
    title: 'investigateHosts.process.filePath',
    isDescending: false
  }
];