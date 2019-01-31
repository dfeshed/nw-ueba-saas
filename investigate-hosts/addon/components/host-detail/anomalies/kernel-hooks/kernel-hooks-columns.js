// Anomalies > kernel Hooks table header configuration

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
    width: 200
  },
  {
    field: 'driverFileName',
    title: 'driverFileName',
    format: 'FILENAME',
    width: 300
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
    title: 'reputationStatus'
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE',
    width: '15vw'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'downloaded',
    format: 'DOWNLOADSTATUS',
    width: 100
  },
  {
    field: 'objectFunction',
    title: 'objectFunction',
    width: '25vw'
  },
  {
    field: 'hookedFileName',
    title: 'hookedFileName',
    width: 300
  }]
};
