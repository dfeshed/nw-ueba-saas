export default {
  windows: [
    {
      sectionName: 'Tasks',
      fields: [
        {
          field: 'creatorUser'
        },
        {
          field: 'executeUser'
        },
        {
          field: 'fileId'
        },
        {
          field: 'name'
        },
        {
          field: 'launchArguments'
        },
        {
          field: 'triggerString'
        },
        {
          field: 'status'
        }
      ]
    }
  ],
  linux: [
    {
      sectionName: 'Tasks',
      fields: [
        {
          field: 'fileId'
        },
        {
          field: 'launchArguments'
        },
        {
          field: 'triggerString'
        },
        {
          field: 'user'
        }
      ]
    }
  ],
  mac: [
    {
      sectionName: 'Tasks',
      fields: [
        {
          field: 'fileId'
        },
        {
          field: 'launchArguments'
        },
        {
          field: 'triggerString'
        },
        {
          field: 'user'
        }
      ]
    }
  ]
};