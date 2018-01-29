import { generateColumns } from 'investigate-hosts/util/util';

let columnsConfig = {
  linux: [
    {
      field: 'derivedOwner.username',
      title: 'User Name',
      label: 'investigateHosts.files.fields.owner.userName',
      width: 45,
      disableSort: true
    },
    {
      field: 'derivedOwner.groupname',
      title: 'Group Name',
      label: 'investigateHosts.files.fields.owner.groupName',
      width: 90,
      disableSort: true
    }
  ],
  windows: [
    {
      'field': 'fileProperties.signature.features',
      title: 'Signature',
      'signer': 'fileProperties.signature.signer',
      'label': 'investigateHosts.files.fields.signature.features',
      'dataType': 'STRING',
      'format': 'SIGNATURE',
      'disableSort': true
    },
    {
      'field': 'timeCreated',
      title: 'Created',
      'label': 'investigateHosts.files.fields.timeCreated',
      'format': 'DATE',
      'width': 150
    }
  ],
  mac: [
    {
      'field': 'fileProperties.signature.features',
      'title': 'Signature',
      'signer': 'fileProperties.signature.signer',
      'label': 'investigateHosts.files.fields.signature.features',
      'format': 'SIGNATURE',
      'disableSort': true
    },
    {
      'field': 'timeCreated',
      'title': 'Created',
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
    'title': 'Filename',
    'width': 150
  },
  {
    'field': 'fileProperties.entropy',
    'title': 'Entropy',
    'label': 'investigateHosts.files.fields.entropy',
    'disableSort': true,
    'format': 'DECIMAL',
    'width': 53
  },
  {
    'field': 'fileProperties.size',
    'title': 'Size',
    'label': 'investigateHosts.files.fields.size',
    'disableSort': true,
    'format': 'SIZE',
    'width': 65
  },
  {
    'field': 'path',
    'title': 'Path',
    'label': 'investigateHosts.files.fields.path',
    'width': 400
  }
];

columnsConfig = generateColumns(columnsConfig, defaultColumns);

export default columnsConfig;
