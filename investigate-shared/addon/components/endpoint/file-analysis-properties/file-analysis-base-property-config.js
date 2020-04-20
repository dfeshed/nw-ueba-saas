export default [
  {
    sectionName: 'File Details',
    labelPrefix: 'investigateHosts.files.fields',
    fields: [
      {
        field: 'format'
      },
      {
        field: 'checksumMd5'
      },
      {
        field: 'checksumSha1'
      },
      {
        field: 'checksumSha256'
      },
      {
        field: 'size',
        format: 'SIZE'
      },
      {
        field: 'downloadedFileName'
      },
      {
        field: 'downloadedPath'
      }
    ]
  }
];
