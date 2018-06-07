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
  }]
};

const config = (os) => [...defaultConfig, ...filePropertiesConfig[os]];
export default config;
