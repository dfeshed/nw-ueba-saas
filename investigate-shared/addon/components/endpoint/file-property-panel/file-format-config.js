export default {
  pe: {
    sectionName: 'File.PE',
    fieldPrefix: 'fileProperties',
    fields: [
      {
        field: 'pe.timeStamp',
        format: 'DATE'
      },
      {
        field: 'pe.imageSize',
        format: 'SIZE'
      },
      {
        field: 'pe.numberOfExportedFunctions'
      },
      {
        field: 'pe.numberOfNamesExported'
      },
      {
        field: 'pe.numberOfExecuteWriteSections'
      },
      {
        field: 'pe.features'
      },
      {
        field: 'pe.resources.originalFileName'
      },
      {
        field: 'pe.resources.company'
      },
      {
        field: 'pe.resources.description'
      },
      {
        field: 'pe.resources.version'
      },
      {
        field: 'pe.importedLibraries'
      },
      {
        field: 'pe.sectionNames'
      }
    ]
  },
  macho: {
    sectionName: 'File.MachO',
    fieldPrefix: 'fileProperties',
    fields: [
      {
        field: 'macho.uuid'
      },
      {
        field: 'macho.identifier'
      },
      {
        field: 'macho.minOsxVersion'
      },
      {
        field: 'macho.flags'
      },
      {
        field: 'macho.numberOfLoadCommands'
      },
      {
        field: 'macho.version'
      },
      {
        field: 'macho.features'
      },
      {
        field: 'macho.importedLibraries'
      },
      {
        field: 'macho.sectionNames'
      }
    ]
  },
  elf: {
    sectionName: 'File.ELF',
    fieldPrefix: 'fileProperties',
    fields: [
      {
        field: 'elf.classType'
      },
      {
        field: 'elf.data'
      },
      {
        field: 'elf.entryPoint'
      },
      {
        field: 'elf.type'
      },
      {
        field: 'elf.features'
      },
      {
        field: 'elf.importedLibraries'
      },
      {
        field: 'elf.sectionNames'
      }
    ]
  }
};