import { generateColumns } from 'investigate-hosts/util/util';

let columnsConfig = {
  linux: [
    {
      field: 'derivedOwner.username',
      label: 'investigateHosts.files.fields.owner.userName',
      width: 45,
      disableSort: true
    },
    {
      field: 'derivedOwner.groupname',
      label: 'investigateHosts.files.fields.owner.groupName',
      width: 90,
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
    },
    {
      'field': 'timeCreated',
      'label': 'investigateHosts.files.fields.timeCreated',
      'format': 'DATE',
      'width': 150
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
    },
    {
      'field': 'timeCreated',
      'label': 'investigateHosts.files.fields.timeCreated',
      'format': 'DATE',
      'width': 150
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
    'field': 'path',
    'label': 'investigateHosts.files.fields.path',
    'width': 400
  }
];

columnsConfig = generateColumns(columnsConfig, defaultColumns);

export default columnsConfig;
