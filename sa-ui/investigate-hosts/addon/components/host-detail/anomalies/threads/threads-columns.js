// Anomalies > Thread table header configuration

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
    field: 'startAddress',
    title: 'investigateHosts.detailsColumns.startAddress',
    width: '10vw'
  },
  {
    field: 'fileName',
    title: 'investigateHosts.detailsColumns.fileName',
    format: 'FILENAME',
    width: 500
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
    field: 'fileProperties.hostCount',
    title: 'investigateHosts.detailsColumns.hostCount',
    width: '6vw'
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'investigateHosts.detailsColumns.reputationStatus'
  },
  {
    field: 'process',
    title: 'investigateHosts.detailsColumns.process',
    width: '10vw'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'investigateHosts.detailsColumns.downloadInfo',
    format: 'DOWNLOADSTATUS',
    width: 100
  },
  {
    field: 'signature',
    title: 'investigateHosts.detailsColumns.signature',
    format: 'SIGNATURE'
  },
  {
    field: 'tid',
    title: 'investigateHosts.detailsColumns.tid'
  },
  {
    field: 'teb',
    title: 'investigateHosts.detailsColumns.teb',
    width: '20vw'
  }]
};
