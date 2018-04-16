export default {
  windows: [
    {
      sectionName: 'Services',
      fields: [
        {
          field: 'displayName'
        },
        {
          field: 'fileId'
        },
        {
          field: 'state'
        },
        {
          field: 'win32ErrorCode'
        }
      ]
    }
  ],
  linux: [
    {
      sectionName: 'Services',
      fields: [
        {
          field: 'description'
        },
        {
          field: 'type'
        },
        {
          field: 'fileId'
        },
        {
          field: 'pid'
        }
      ]
    }
  ],
  mac: [
    {
      sectionName: 'Services',
      fields: [
        {
          field: 'description'
        },
        {
          field: 'type'
        },
        {
          field: 'fileId'
        },
        {
          field: 'pid'
        }
      ]
    }
  ]
};