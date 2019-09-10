/**
 * A set of types representing the time range since a starting point time (e.g., since 7 days ago)
 * @public
 * @type {[*]}
 */
const FILTER_TYPES = [
  {
    'name': 'firstFileName',
    'label': 'investigateFiles.fields.firstFileName',
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
    name: 'fileStatus',
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
    name: 'remediationAction',
    label: 'investigateFiles.fields.remediationAction',
    type: 'list',
    listOptions: [
      { name: 'Block', label: 'investigateFiles.remediationAction.Block' }
    ]
  },
  {
    name: 'reputationStatus',
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
    'name': 'score',
    'label': 'investigateFiles.fields.score',
    'min': 0,
    'max': 100,
    'step': 1,
    'type': 'range'
  },
  {
    'name': 'machineOsType',
    'label': 'investigateFiles.filter.osType',
    'listOptions': [
      { name: 'windows', label: 'investigateFiles.filter.fileType.pe' },
      { name: 'linux', label: 'investigateFiles.filter.fileType.linux' },
      { name: 'mac', label: 'investigateFiles.filter.fileType.macho' }
    ],
    type: 'list'
  },
  {
    'name': 'size',
    'label': 'investigateFiles.fields.size',
    'type': 'number',
    'useI18N': true,
    'operators': [
      { label: 'investigateFiles.filter.label.greaterThan', type: 'GREATER_THAN' },
      { label: 'investigateFiles.filter.label.lessThan', type: 'LESS_THAN' },
      { label: 'investigateFiles.filter.label.between', type: 'BETWEEN' }
    ],
    'units': [
      { label: 'investigateFiles.filter.label.bytes', type: 'bytes' },
      { label: 'investigateFiles.filter.label.kb', type: 'KB' },
      { label: 'investigateFiles.filter.label.mb', type: 'MB' },
      { label: 'investigateFiles.filter.label.gb', type: 'GB' }
    ]
  },
  {
    'name': 'entropy',
    'label': 'investigateFiles.fields.entropy',
    'min': 0,
    'max': 10,
    'step': 0.1,
    'type': 'range'
  },
  {
    'name': 'format',
    'label': 'investigateFiles.fields.format',
    'listOptions': [
      { name: 'pe', label: 'investigateFiles.filter.fileType.pe' },
      { name: 'elf', label: 'investigateFiles.filter.fileType.linux' },
      { name: 'macho', label: 'investigateFiles.filter.fileType.macho' },
      { name: 'script', label: 'investigateFiles.filter.fileType.scripts' },
      { name: 'unknown', label: 'investigateFiles.filter.fileType.unknown' }
    ],
    type: 'list'
  },
  {
    'name': 'signature.features',
    'label': 'investigateFiles.fields.signature.features',
    'listOptions': [
      { name: 'file.unsigned', label: 'investigateFiles.filter.signature.unsigned' },
      { name: 'signature.valid', label: 'investigateFiles.filter.signature.valid' },
      { name: 'signature.invalid', label: 'investigateFiles.filter.signature.invalid' },
      { name: 'signature.catalog', label: 'investigateFiles.filter.signature.catalog' },
      { name: 'signature.microsoft', label: 'investigateFiles.filter.signature.signer.microsoft' },
      { name: 'signature.apple', label: 'investigateFiles.filter.signature.signer.apple' }
    ],
    type: 'list'
  },
  {
    'name': 'pe.resources.company',
    'label': 'investigateFiles.fields.companyName',
    'type': 'text',
    'placeholder': 'e.g., RSA Security Inc'
  },
  {
    'name': 'fileHash',
    'label': 'investigateFiles.filter.fileHash',
    'type': 'text',
    'validations': {
      format: {
        validator: (value) => {
          return !/^[A-Za-z0-9]*$/.test(value);
        },
        message: 'investigateFiles.filter.invalidCharsAlphaNumericOnly'
      }
    }
  },
  {
    name: 'downloadInfo.status',
    label: 'investigateFiles.fields.downloadInfo.status',
    type: 'list',
    listOptions: [
      { name: 'Downloaded', label: 'investigateFiles.filter.fileDownloadStatus.Downloaded' },
      { name: 'Error', label: 'investigateFiles.filter.fileDownloadStatus.Error' },
      { name: 'NotDownloaded', label: 'investigateFiles.filter.fileDownloadStatus.NotDownloaded' }
    ]
  }
];

export {
  FILTER_TYPES
};
