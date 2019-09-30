const FILTER_TYPES = [
  {
    'name': 'name',
    'label': 'investigateHosts.process.processName',
    'type': 'text',
    useI18N: true,
    'validations': {
      length: {
        validator: (value) => {
          return value.length > 256;
        },
        message: 'investigateFiles.filter.invalidFilterInputLength'
      },
      format: {
        validator: (value) => {
          return /[<>:"'/|\\?]+/.test(value);
        },
        message: 'investigateFiles.filter.invalidCharsAlphaNumericOnly'
      }
    },
    'placeholder': 'investigateFiles.filter.fileNamePlaceholder'
  },
  {
    name: 'fileProperties.fileStatus',
    label: 'investigateFiles.fields.fileStatus',
    type: 'list',
    listOptions: [
      { name: 'Neutral', label: 'configure.endpoint.certificates.status.statusOptions.neutral' },
      { name: 'Blacklist', label: 'investigateFiles.editFileStatus.fileStatusOptions.blacklist' },
      { name: 'Graylist', label: 'investigateFiles.editFileStatus.fileStatusOptions.graylist' },
      { name: 'Whitelist', label: 'investigateFiles.editFileStatus.fileStatusOptions.whitelist' }
    ]
  },
  {
    name: 'fileProperties.reputationStatus',
    label: 'investigateFiles.fields.reputationStatus',
    type: 'list',
    listOptions: [
      { name: 'Malicious', label: 'investigateFiles.filter.reputationStatus.Malicious' },
      { name: 'Suspicious', label: 'investigateFiles.filter.reputationStatus.Suspicious' },
      { name: 'Unknown', label: 'investigateFiles.filter.reputationStatus.Unknown' },
      { name: 'Known', label: 'investigateFiles.filter.reputationStatus.Known' },
      { name: 'Known Good', label: 'investigateFiles.filter.reputationStatus.Known_Good' },
      { name: 'Invalid', label: 'investigateFiles.filter.reputationStatus.Invalid' }
    ]
  },
  {
    'name': 'fileProperties.signature.features',
    'label': 'investigateFiles.fields.signature.features',
    'listOptions': [
      { name: 'unsigned', label: 'investigateFiles.filter.signature.unsigned' },
      { name: 'valid', label: 'investigateFiles.filter.signature.valid' },
      { name: 'invalid', label: 'investigateFiles.filter.signature.invalid' },
      { name: 'catalog', label: 'investigateFiles.filter.signature.catalog' },
      { name: 'microsoft', label: 'investigateFiles.filter.signature.signer.microsoft' },
      { name: 'apple', label: 'investigateFiles.filter.signature.signer.apple' }
    ],
    type: 'list'
  },
  {
    'name': 'machineFileScore',
    'label': 'investigateFiles.fields.score',
    'min': 0,
    'max': 100,
    'step': 1,
    'type': 'range'
  }
];

export {
  FILTER_TYPES
};
