/**
 * A set of types representing the time range since a starting point time (e.g., since 7 days ago)
 * @public
 * @type {[*]}
 */
const FILTER_TYPES = [
  {
    'name': 'firstFileName',
    'label': 'investigateFiles.fields.firstFileName',
    'type': 'text'
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
    'name': 'pe.resources.company',
    'label': 'investigateFiles.fields.companyName',
    'type': 'text'
  },
  {
    'name': 'checksumMd5',
    'label': 'investigateFiles.fields.checksumMd5',
    'type': 'text'
  },
  {
    'name': 'checksumSha256',
    'label': 'investigateFiles.fields.checksumSha256',
    'type': 'text'
  }
];

export {
  FILTER_TYPES
};
