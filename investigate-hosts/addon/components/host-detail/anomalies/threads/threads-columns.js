// Anomalies > Thread table header configuration

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
    field: 'startAddress',
    title: 'startAddress',
    width: '10vw'
  },
  {
    field: 'fileName',
    title: 'dllFileName',
    format: 'FILENAME',
    width: 500
  },
  {
    field: 'machineFileScore',
    title: 'score',
    width: '12vw'
  },
  {
    field: 'fileProperties.score',
    title: 'globalScore',
    width: '12vw'
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
    field: 'process',
    title: 'process',
    width: '10vw'
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
    title: 'tid'
  },
  {
    field: 'teb',
    title: 'teb',
    width: '20vw'
  }]
};
