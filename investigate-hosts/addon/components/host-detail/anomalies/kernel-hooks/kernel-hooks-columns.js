// Anomalies > kernel Hooks table header configuration

export default {
  windows: [{
    'dataType': 'checkbox',
    'width': 20,
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
    field: 'driverFileName',
    title: 'driverFileName',
    format: 'FILENAME'
  },
  {
    field: 'fileProperties.score',
    title: 'score'
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'reputationStatus'
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'downloaded',
    disableSort: true,
    format: 'DOWNLOADSTATUS',
    width: 100
  },
  {
    field: 'objectFunction',
    title: 'objectFunction',
    width: '15%'
  },
  {
    field: 'hookedFileName',
    title: 'hookedFileName'
  }]
};