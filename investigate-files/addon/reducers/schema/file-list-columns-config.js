const FILE_LIST_COLUMNS_CONFIG = [
  {
    name: 'firstFileName',
    description: 'File Name',
    dataType: 'STRING'
  },
  {
    name: 'score',
    description: '',
    dataType: 'INT'
  },
  {
    name: 'fileStatus',
    description: '',
    dataType: 'STRING'
  },
  {
    name: 'remediationAction',
    description: 'Remediation Action',
    dataType: 'STRING'
  },
  {
    name: 'reputationStatus',
    description: '',
    dataType: 'STRING'
  },
  {
    name: 'downloadInfo.status',
    dataType: 'STRING'
  },
  {
    name: 'size',
    description: 'File Size',
    dataType: 'LONG'
  },
  {
    name: 'signature.features',
    description: 'Signature',
    dataType: 'STRING',
    values: [
      'signed',
      'unsigned',
      'valid',
      'invalid',
      'catalog',
      'microsoft',
      'apple'
    ]
  },
  {
    name: 'firstSeenTime',
    dataType: 'DATE'
  },
  {
    name: 'machineOsType',
    description: 'Operating system',
    dataType: 'STRING',
    values: [
      'windows',
      'linux',
      'mac'
    ]
  },
  {
    name: 'signature.timeStamp',
    dataType: 'DATE'
  },
  {
    name: 'signature.thumbprint',
    dataType: 'STRING'
  },
  {
    name: 'signature.signer',
    dataType: 'STRING'
  },
  {
    name: 'checksumMd5',
    description: '',
    dataType: 'STRING'
  },
  {
    name: 'checksumSha1',
    description: '',
    dataType: 'STRING'
  },
  {
    name: 'checksumSha256',
    description: 'File Hash',
    dataType: 'STRING'
  },
  {
    name: 'pe.timeStamp',
    dataType: 'DATE'
  },
  {
    name: 'pe.imageSize',
    dataType: 'INT'
  },
  {
    name: 'pe.numberOfExportedFunctions',
    dataType: 'LONG'
  },
  {
    name: 'pe.numberOfNamesExported',
    dataType: 'LONG'
  },
  {
    name: 'pe.numberOfExecuteWriteSections',
    dataType: 'INT'
  },
  {
    name: 'pe.features',
    dataType: 'STRING'
  },
  {
    name: 'pe.resources.originalFileName',
    dataType: 'STRING'
  },
  {
    name: 'pe.resources.company',
    dataType: 'STRING'
  },
  {
    name: 'pe.resources.description',
    dataType: 'STRING'
  },
  {
    name: 'pe.resources.version',
    dataType: 'STRING'
  },
  {
    name: 'pe.sectionNames',
    description: '',
    dataType: 'STRING'
  },
  {
    name: 'pe.importedLibraries',
    dataType: 'STRING'
  },
  {
    name: 'elf.classType',
    dataType: 'INT'
  },
  {
    name: 'elf.data',
    dataType: 'INT'
  },
  {
    name: 'elf.entryPoint',
    dataType: 'LONG'
  },
  {
    name: 'elf.features',
    dataType: 'STRING'
  },
  {
    name: 'elf.type',
    dataType: 'INT'
  },
  {
    name: 'elf.sectionNames',
    description: '',
    dataType: 'STRING'
  },
  {
    name: 'elf.importedLibraries',
    dataType: 'STRING'
  },
  {
    name: 'macho.uuid',
    dataType: 'STRING'
  },
  {
    name: 'macho.identifier',
    dataType: 'STRING'
  },
  {
    name: 'macho.minOsxVersion',
    dataType: 'STRING'
  },
  {
    name: 'macho.features',
    dataType: 'STRING'
  },
  {
    name: 'macho.flags',
    dataType: 'LONG'
  },
  {
    name: 'macho.numberOfLoadCommands',
    dataType: 'LONG'
  },
  {
    name: 'macho.version',
    dataType: 'STRING'
  },
  {
    name: 'macho.sectionNames',
    description: '',
    dataType: 'STRING'
  },
  {
    name: 'macho.importedLibraries',
    dataType: 'STRING'
  },
  {
    name: 'entropy',
    description: 'File Entropy',
    dataType: 'DOUBLE'
  },
  {
    name: 'format',
    description: 'File format',
    dataType: 'STRING',
    values: [
      'pe',
      'elf',
      'macho',
      'script',
      'unknown'
    ]
  },
  {
    name: 'downloadInfo.path',
    dataType: 'STRING'
  },
  {
    name: 'downloadInfo.fileName',
    dataType: 'STRING'
  }
];
export default FILE_LIST_COLUMNS_CONFIG;