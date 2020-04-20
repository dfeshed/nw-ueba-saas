export default {
  windows: [
    {
      sectionName: 'Image Hooks',
      fields: [
        {
          field: 'type'
        },
        {
          field: 'hookedProcess'
        },
        {
          field: 'hookLocation.fileName'
        },
        {
          field: 'hookLocation.section'
        },
        {
          field: 'hookLocation.address'
        },
        {
          field: 'hookLocation.symbol'
        },
        {
          field: 'hookLocation.symbolOffset'
        },
        {
          field: 'jumpCount'
        },
        {
          field: 'jumpTo'
        },
        {
          field: 'inlinePatch.originalAsm',
          format: 'LIST'
        },
        {
          field: 'inlinePatch.currentAsm',
          format: 'LIST'
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