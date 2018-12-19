// Anomalies > Thread table header configuration

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
    field: 'startAddress',
    title: 'startAddress',
    width: 100
  },
  {
    field: 'fileName',
    title: 'dllFileName',
    format: 'FILENAME'
  },
  {
    field: 'fileProperties.score',
    title: 'score'
  },
  {
    field: 'machineCount',
    title: 'machineCount',
    width: '15%'
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'reputationStatus'
  },
  {
    field: 'process',
    title: 'process'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'downloaded',
    disableSort: true,
    format: 'DOWNLOADSTATUS',
    width: 100
  },
  {
    field: 'signature',
    title: 'signature',
    format: 'SIGNATURE'
  },
  {
    field: 'tid',
    title: 'tid',
    width: '15%'
  },
  {
    field: 'teb',
    title: 'teb'
  }]
};