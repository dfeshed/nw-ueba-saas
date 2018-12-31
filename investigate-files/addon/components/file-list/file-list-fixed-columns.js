export default [
  {
    dataType: 'checkbox',
    width: 20,
    class: 'rsa-form-row-checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true,
    headerComponentClass: 'rsa-form-checkbox'
  },
  {
    dataType: 'string',
    width: 200,
    visible: true,
    field: 'firstFileName',
    searchable: true,
    title: 'investigateFiles.fields.firstFileName'
  },
  {
    dataType: 'string',
    width: 100,
    visible: true,
    field: 'score',
    searchable: false,
    title: 'investigateFiles.fields.score'
  },
  {
    dataType: 'string',
    width: 100,
    visible: true,
    field: 'machineCount',
    searchable: false,
    disableSort: true,
    title: 'investigateFiles.fields.machineCount'
  },
  {
    field: 'fileStatus',
    description: '',
    dataType: 'STRING',
    visible: true,
    title: 'investigateFiles.fields.fileStatus'
  },
  {
    field: 'remediationAction',
    description: '',
    dataType: 'STRING',
    visible: true,
    title: 'investigateFiles.fields.remediationAction'
  },
  {
    field: 'reputationStatus',
    description: '',
    dataType: 'STRING',
    width: 150,
    visible: true,
    title: 'investigateFiles.fields.reputationStatus'
  },
  {
    field: 'downloadInfo.status',
    dataType: 'STRING',
    visible: true,
    title: 'investigateFiles.fields.downloadInfo.status'
  },
  {
    field: 'size',
    description: 'File Size',
    dataType: 'LONG',
    visible: true,
    title: 'investigateFiles.fields.size'
  },
  {
    field: 'firstSeenTime',
    dataType: 'DATE',
    visible: true,
    width: 200,
    title: 'investigateFiles.fields.firstSeenTime'
  },
  {
    field: 'signature.features',
    description: 'Signature',
    dataType: 'STRING',
    title: 'investigateFiles.fields.signature.features',
    visible: true
  },
  {
    field: 'machineOsType',
    description: 'Operating system',
    dataType: 'STRING',
    title: 'investigateFiles.fields.machineOsType',
    width: 125,
    visible: true
  }
];