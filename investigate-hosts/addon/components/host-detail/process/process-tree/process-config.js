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
    componentClass: 'host-detail/process/process-tree/tree-name'
  },
  {
    field: 'pid',
    width: 100,
    title: 'investigateHosts.process.pid'
  },
  {
    field: 'score',
    width: 200,
    title: 'investigateHosts.process.riskScore'
  },
  {
    field: 'fileStatus',
    width: 200,
    title: 'investigateHosts.process.fileStatus'
  },
  {
    field: 'reputationStatus',
    width: 200,
    title: 'investigateHosts.process.reputationStatus'
  },
  {
    field: 'downloadInfo.status',
    width: 200,
    format: 'DOWNLOADSTATUS',
    title: 'investigateHosts.process.downloaded'
  },
  {
    field: 'signature.features',
    width: 200,
    title: 'investigateHosts.process.signature'
  },
  {
    field: 'path',
    width: 200,
    title: 'investigateHosts.process.filePath'
  }
];