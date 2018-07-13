export default [
  {
    sectionName: 'File.Status',
    fields: [
      {
        field: 'fileProperties.reputationStatus'
      }
    ]
  },
  {
    sectionName: 'File.General',
    fields: [
      {
        field: 'fileName'
      },
      {
        field: 'fileProperties.entropy'
      },
      {
        field: 'fileProperties.size',
        format: 'SIZE'
      },
      {
        field: 'fileProperties.format'
      }
    ]
  },
  {
    sectionName: 'File.Signature',
    fieldPrefix: 'fileProperties',
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
  },
  {
    sectionName: 'File.Hash',
    labelPrefix: 'investigateHosts.files.fields',
    fieldPrefix: 'fileProperties',
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
    sectionName: 'File.Time',
    labelPrefix: 'investigateHosts.files.fields',
    fields: [
      {
        field: 'timeCreated',
        format: 'DATE'
      },
      {
        field: 'timeModified',
        format: 'DATE'
      },
      {
        field: 'timeAccessed',
        format: 'DATE'
      }
    ]
  },
  {
    sectionName: 'File.Location',
    labelPrefix: 'investigateHosts.files.fields',
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
  }
];
