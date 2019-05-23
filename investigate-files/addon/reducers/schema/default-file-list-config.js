const DEFAULT_FILE_PREFERENCES = {
  filePreference: {
    columnConfig: [{
      tableId: 'files',
      columns: [
        {
          field: 'firstSeenTime',
          width: '7vw',
          displayIndex: 3
        },
        {
          field: 'reputationStatus',
          width: '10vw',
          displayIndex: 4
        },
        {
          field: 'size',
          width: '3vw',
          displayIndex: 5
        },
        {
          field: 'signature.features',
          width: '8vw',
          displayIndex: 6
        },
        {
          field: 'pe.resources.company',
          width: '6vw',
          displayIndex: 7
        },
        {
          field: 'fileStatus',
          width: '10vw',
          displayIndex: 8
        },
        {
          field: 'remediationAction',
          width: '10vw',
          displayIndex: 9
        },
        {
          field: 'downloadInfo.status',
          width: '10vw',
          displayIndex: 10
        },
        {
          field: 'machineOsType',
          width: '6vw',
          displayIndex: 11
        }
      ]
    }],
    sortField: '{ "sortField": "score", "isSortDescending": false }'
  }
};

export default DEFAULT_FILE_PREFERENCES;
