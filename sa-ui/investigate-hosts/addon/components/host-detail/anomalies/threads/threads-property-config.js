export default {
  windows: [
    {
      sectionName: 'Suspicious Threads',
      fields: [
        {
          field: 'processName'
        },
        {
          field: 'processTime',
          format: 'DATE'
        },
        {
          field: 'eprocess'
        },
        {
          field: 'pid'
        },
        {
          field: 'ethread'
        },
        {
          field: 'tid'
        },
        {
          field: 'teb'
        },
        {
          field: 'startAddress'
        },
        {
          field: 'state'
        }
      ]
    }
  ]
};