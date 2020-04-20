export default {
  windows: [
    {
      sectionName: 'Autoruns',
      fields: [
        {
          field: 'fileId'
        },
        {
          field: 'registryPath'
        },
        {
          field: 'type'
        }
      ]
    }
  ],
  linux: [
    {
      sectionName: 'Autoruns',
      fields: [
        {
          field: 'fileId'
        },
        {
          field: 'type'
        }
      ]
    }
  ],
  mac: [
    {
      sectionName: 'Autoruns',
      fields: [
        {
          field: 'fileId'
        },
        {
          field: 'type'
        }
      ]
    }
  ]
};