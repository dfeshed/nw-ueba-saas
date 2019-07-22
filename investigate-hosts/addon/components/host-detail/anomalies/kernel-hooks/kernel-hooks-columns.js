// Anomalies > kernel Hooks table header configuration

export default {
  windows: [{
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
    field: 'type',
    title: 'investigateHosts.detailsColumns.type',
    width: 200
  },
  {
    field: 'driverFileName',
    title: 'investigateHosts.detailsColumns.fileName',
    format: 'FILENAME',
    width: 300
  },
  {
    field: 'machineFileScore',
    title: 'investigateHosts.detailsColumns.machineFileScore',
    width: '8vw'
  },
  {
    field: 'fileProperties.score',
    title: 'investigateHosts.detailsColumns.globalScore',
    width: '8vw'
  },
  {
    field: 'machineCount',
    title: 'investigateHosts.detailsColumns.machineCount',
    width: '6vw',
    disableSort: true,
    visible: false
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'investigateHosts.detailsColumns.reputationStatus'
  },
  {
    field: 'signature',
    title: 'investigateHosts.detailsColumns.signature',
    format: 'SIGNATURE',
    width: '8vw'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'investigateHosts.detailsColumns.downloadInfo',
    format: 'DOWNLOADSTATUS',
    width: 100
  },
  {
    field: 'objectFunction',
    title: 'investigateHosts.detailsColumns.objectFunction',
    width: '25vw'
  },
  {
    field: 'hookedFileName',
    title: 'investigateHosts.detailsColumns.hookedFileName',
    width: 300
  }]
};
