/* eslint-env node */
export default {
  'type': 'files',
  'fields': [
    {
      'name': 'checksumMd5',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'checksumSha1',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'checksumSha256',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'elf.classType',
      'dataType': 'INT',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'elf.data',
      'dataType': 'INT',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'elf.entryPoint',
      'dataType': 'INT',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'elf.features',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'elf.type',
      'dataType': 'INT',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'entropy',
      'dataType': 'DOUBLE',
      'searchable': true,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'firstFileName',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'signature.timeStamp',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'signature.thumbprint',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'signature.features',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'format',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': false,
      'wrapperType': 'STRING',
      'values': [
        'macho',
        'linux'
      ]
    },
    {
      'name': 'sectionNames',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': true,
      'wrapperType': 'STRING'
    },
    {
      'name': 'importedLibraries',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'pe.timeStamp',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'pe.imageSize',
      'dataType': 'INT',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'pe.numberOfExportedFunctions',
      'dataType': 'INT',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'pe.numberOfNamesExported',
      'dataType': 'INT',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'pe.numberOfExecuteWriteSections',
      'dataType': 'INT',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'pe.features',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'pe.resources.originalFileName',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'pe.resources.company',
      'dataType': 'STRING',
      'searchable': true,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'pe.resources.description',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'pe.resources.version',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'macho.uuid',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'macho.identifier',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'macho.minOsxVersion',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'macho.features',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'macho.flags',
      'dataType': 'LONG',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'macho.numberOfLoadCommands',
      'dataType': 'LONG',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    },
    {
      'name': 'macho.version',
      'dataType': 'STRING',
      'searchable': false,
      'defaultProjection': false,
      'wrapperType': 'STRING'
    },
    {
      'name': 'size',
      'dataType': 'LONG',
      'searchable': true,
      'defaultProjection': false,
      'wrapperType': 'NUMBER'
    }
  ]
};