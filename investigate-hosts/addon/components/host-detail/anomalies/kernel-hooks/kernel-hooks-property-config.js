export default {
  windows: [
    {
      sectionName: 'Kernel Hooks',
      fields: [
        {
          field: 'type'
        },
        {
          field: 'hookLocation.fileName'
        },
        {
          field: 'hookLocation.address'
        },
        {
          field: 'hookLocation.checksumSha256'
        },
        {
          field: 'hookLocation.path'
        },
        {
          field: 'hookLocation.objectName'
        },
        {
          field: 'hookLocation.objectFunction'
        },
        {
          field: 'jumpCount'
        },
        {
          field: 'jumpTo'
        },
        {
          field: 'hookLocation.imageSize'
        },
        {
          field: 'hookLocation.imageBase'
        }
      ]
    }
  ]
};