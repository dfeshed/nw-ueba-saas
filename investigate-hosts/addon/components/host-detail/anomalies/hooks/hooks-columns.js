// Anomalies > Hooks table header configuration

export default {
  windows: [{
    'dataType': 'checkbox',
    'width': '2vw',
    'class': 'rsa-form-row-checkbox',
    'componentClass': 'rsa-form-checkbox',
    'visible': true,
    'disableSort': true,
    'headerComponentClass': 'rsa-form-checkbox'
  },
  {
    field: 'type',
    title: 'type',
    width: 100
  },
  {
    field: 'dllFileName',
    title: 'dllFileName',
    format: 'FILENAME'
  },
  {
    field: 'machineFileScore',
    title: 'score'
  },
  {
    field: 'fileProperties.score',
    title: 'globalScore'
  },
  {
    field: 'machineCount',
    title: 'machineCount',
    width: '6vw',
    disableSort: true
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'reputationStatus',
    width: '15vw'
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'downloaded',
    format: 'DOWNLOADSTATUS',
    width: 100
  },
  {
    field: 'hookedProcess',
    title: 'hookedProcess',
    width: '15vw'
  },
  {
    field: 'hookedFileName',
    title: 'hookFileName',
    width: '20vw'
  },
  {
    field: 'symbol',
    title: 'hookedSymbol',
    width: '15vw'
  }]
};
