const defaultConfig = [
  {
    sectionName: 'File.General',
    fields: [
      {
        field: 'firstFileName'
      },
      {
        field: 'entropy'
      },
      {
        field: 'size',
        format: 'SIZE'
      },
      {
        field: 'format'
      }
    ]
  },
  {
    sectionName: 'Process',
    fields: [
      {
        field: 'process.createUtcTime',
        format: 'DATE'
      },
      {
        field: 'process.eprocess',
        format: 'HEX'
      },
      {
        field: 'process.integrityLevel'
      },
      {
        field: 'process.parentPath'
      },
      {
        field: 'process.threadCount'
      },
      {
        field: 'process.sessionId'
      }
    ]
  }
];
const filePropertiesConfig = {
  windows: [{
    sectionName: 'File.PE',
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
  {
    sectionName: 'File.Location',
    fields: [
      {
        field: 'path'
      },
      {
        field: 'sameDirectoryFileCounts.nonExe'
      },
      {
        field: 'sameDirectoryFileCounts.exe'
      },
      {
        field: 'sameDirectoryFileCounts.subFolder'
      },
      {
        field: 'sameDirectoryFileCounts.exeSameCompany'
      }
    ]
  },
  {
    sectionName: 'File.Hash',
    fields: [
      {
        field: 'checksumMd5'
      },
      {
        field: 'checksumSha1'
      },
      {
        field: 'checksumSha256'
      }
    ]
  },
  {
    sectionName: 'File.Signature',
    fields: [
      {
        field: 'signature.features',
        format: 'SIGNATURE'
      },
      {
        field: 'signature.timeStamp',
        format: 'DATE'
      },
      {
        field: 'signature.thumbprint'
      },
      {
        field: 'signature.signer'
      }
    ]
  }],
  mac: [{
    sectionName: 'File.MachO',
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
  {
    sectionName: 'File.Location',
    fields: [
      {
        field: 'path'
      },
      {
        field: 'sameDirectoryFileCounts.nonExe'
      },
      {
        field: 'sameDirectoryFileCounts.exe'
      },
      {
        field: 'sameDirectoryFileCounts.subFolder'
      },
      {
        field: 'sameDirectoryFileCounts.exeSameCompany'
      }
    ]
  },
  {
    sectionName: 'File.Hash',
    fields: [
      {
        field: 'checksumMd5'
      },
      {
        field: 'checksumSha1'
      },
      {
        field: 'checksumSha256'
      }
    ]
  },
  {
    sectionName: 'File.Signature',
    fields: [
      {
        field: 'signature.features',
        format: 'SIGNATURE'
      },
      {
        field: 'signature.timeStamp',
        format: 'DATE'
      },
      {
        field: 'signature.thumbprint'
      },
      {
        field: 'signature.signer'
      }
    ]
  }],
  linux: [{
    sectionName: 'File.ELF',
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
  }]
};

const config = (os) => [...defaultConfig, ...filePropertiesConfig[os]];
export default config;