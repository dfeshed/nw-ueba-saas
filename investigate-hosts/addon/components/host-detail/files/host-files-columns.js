let columnsConfig = {
  linux: [
    {
      field: 'rpm.packagename',
      label: 'investigateHosts.files.fields.rpm.packageName',
      width: 45,
      disableSort: true
    },
    {
      field: 'derivedOwner.username',
      label: 'investigateHosts.files.fields.owner.userName',
      width: 45,
      disableSort: true
    },
    {
      field: 'derivedOwner.groupname',
      label: 'investigateHosts.files.fields.owner.groupName',
      width: 85,
      disableSort: true
    }
  ],
  windows: [
    {
      'field': 'fileProperties.signature.features',
      'signer': 'fileProperties.signature.signer',
      'label': 'investigateHosts.files.fields.signature.features',
      'dataType': 'STRING',
      'format': 'SIGNATURE',
      'disableSort': true
    }
  ],
  mac: [
    {
      'field': 'fileProperties.signature.features',
      'signer': 'fileProperties.signature.signer',
      'label': 'investigateHosts.files.fields.signature.features',
      'dataType': 'STRING',
      'format': 'SIGNATURE',
      'disableSort': true
    }
  ]
};
const defaultColumns = [
  {
    'field': 'fileName',
    'label': 'investigateHosts.files.fields.fileName',
    'dataType': 'STRING',
    'width': 150
  },
  {
    'field': 'fileProperties.entropy',
    'label': 'investigateHosts.files.fields.entropy',
    'dataType': 'DOUBLE',
    'disableSort': true,
    'format': 'DECIMAL',
    'width': 53
  },
  {
    'field': 'fileProperties.size',
    'label': 'investigateHosts.files.fields.size',
    'dataType': 'LONG',
    'disableSort': true,
    'format': 'SIZE',
    'width': 65
  },
  {
    'field': 'timeCreated',
    'label': 'investigateHosts.files.fields.timeCreated',
    'format': 'DATE',
    'width': 115
  },
  {
    'field': 'path',
    'label': 'investigateHosts.files.fields.path',
    'width': 315
  }
];

const _generateColumns = function(columns) {
  for (const key in columns) {
    if (columns.hasOwnProperty(key)) {
      columns[key] = [...defaultColumns, ...columns[key]];
    }
  }
  return columns;
};

columnsConfig = _generateColumns(columnsConfig);

export default columnsConfig;
