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
    'validations': {
      length: {
        validator: (value) => {
          return value.length > 256;
        },
        message: 'investigateFiles.filter.invalidFilterInputLength'
      },
      format: {
        validator: (value) => {
          return !(/^([!-~])*$/.test(value));
        },
        message: 'investigateFiles.filter.invalidCharacters'
      }
    }
  },
  {
    name: 'fileStatus',
    label: 'investigateFiles.fields.fileStatus',
    type: 'list',
    listOptions: [
      { name: 'Blacklist', label: 'investigateFiles.editFileStatus.fileStatusOptions.blacklist' },
      { name: 'Graylist', label: 'investigateFiles.editFileStatus.fileStatusOptions.graylist' },
      { name: 'Whitelist', label: 'investigateFiles.editFileStatus.fileStatusOptions.whitelist' },
      { name: 'KnownGood', label: 'investigateFiles.editFileStatus.fileStatusOptions.knowngood' }
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
    'name': 'size',
    'label': 'investigateFiles.fields.size',
    'type': 'number',
    'units': [
      { label: 'Bytes', type: 'bytes' },
      { label: 'KB', type: 'KB' },
      { label: 'MB', type: 'MB' },
      { label: 'GB', type: 'GB' }
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
    type: 'dropdown'
  },
  {
    'name': 'pe.resources.company',
    'label': 'investigateFiles.fields.companyName',
    'type': 'text'
  },
  {
    'name': 'checksumMd5',
    'label': 'investigateFiles.fields.checksumMd5',
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
    'name': 'checksumSha256',
    'label': 'investigateFiles.fields.checksumSha256',
    'type': 'text',
    'validations': {
      format: {
        validator: (value) => {
          return !/^[A-Za-z0-9]*$/.test(value);
        },
        message: 'investigateFiles.filter.invalidCharsAlphaNumericOnly'
      }
    }
  }
];

export {
  FILTER_TYPES
};
