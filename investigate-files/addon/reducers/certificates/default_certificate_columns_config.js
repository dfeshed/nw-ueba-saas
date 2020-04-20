const DEFAULT_CERTIFICATE_PREFERENCES = {
  machinePreference: {
    sortField: '{ "key": "score", "descending": true }'
  },
  certificatePreference: {
    columnConfig: [{
      tableId: 'files-certificates',
      columns: [
        {
          field: 'friendlyName',
          width: '10vw',
          displayIndex: 2
        },
        {
          field: 'certificateStatus',
          width: '7vw',
          displayIndex: 3
        },
        {
          field: 'issuer',
          width: '15vw',
          displayIndex: 4,
          disableSort: true
        },
        {
          field: 'thumbprint',
          width: '15vw',
          displayIndex: 5,
          disableSort: true
        },
        {
          field: 'notValidBeforeUtcDate',
          width: '15vw',
          displayIndex: 6
        },
        {
          field: 'notValidAfterUtcDate',
          width: '15vw',
          displayIndex: 7
        },
        {
          field: 'subject',
          width: '15vw',
          displayIndex: 8,
          disableSort: true
        },
        {
          field: 'subjectKey',
          width: '15vw',
          displayIndex: 9,
          disableSort: true
        },
        {
          field: 'serial',
          width: '15vw',
          displayIndex: 10,
          disableSort: true
        },
        {
          field: 'authorityKey',
          width: '15vw',
          displayIndex: 11,
          disableSort: true
        }
      ]
    }],
    sortField: '{ "sortField": "score", "isSortDescending": false }'
  }
};

export default DEFAULT_CERTIFICATE_PREFERENCES;
