export default [
  {
    sectionName: 'Status',
    fields: [
      {
        field: 'reputationStatus'
      }
    ]
  },
  {
    sectionName: 'General',
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
    sectionName: 'Signature',
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
    sectionName: 'Hash',
    labelPrefix: 'investigateHosts.files.fields',
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
  }
];
