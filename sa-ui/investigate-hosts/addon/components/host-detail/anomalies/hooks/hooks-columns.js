// Anomalies > Hooks table header configuration

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
    width: 100
  },
  {
    field: 'dllFileName',
    title: 'investigateHosts.detailsColumns.fileName',
    format: 'FILENAME'
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
    title: 'investigateHosts.detailsColumns.reputationStatus',
    width: '15vw'
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
    field: 'hookedProcess',
    title: 'investigateHosts.detailsColumns.hookedProcess',
    width: '15vw'
  },
  {
    field: 'hookedFileName',
    title: 'investigateHosts.detailsColumns.hookedFileName',
    width: '20vw'
  },
  {
    field: 'symbol',
    title: 'investigateHosts.detailsColumns.hookedSymbol',
    width: '15vw'
  }]
};
