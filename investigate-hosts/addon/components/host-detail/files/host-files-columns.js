import { generateColumns } from 'investigate-hosts/util/util';

let columnsConfig = {
  linux: [
    {
      field: 'derivedOwner.username',
      title: 'User Name',
      label: 'investigateHosts.files.fields.owner.userName',
      disableSort: true
    },
    {
      field: 'derivedOwner.groupname',
      title: 'Group Name',
      label: 'investigateHosts.files.fields.owner.groupName',
      disableSort: true,
      width: '370px'
    }
  ],
  windows: [
    {
      'field': 'fileProperties.signature.features',
      'title': 'Signature',
      'label': 'investigateHosts.files.fields.signature.features',
      'format': 'SIGNATURE',
      'disableSort': true
    },
    {
      'field': 'timeCreated',
      'title': 'Created',
      'label': 'investigateHosts.files.fields.timeCreated',
      'format': 'DATE',
      width: '370px'
    }
  ],
  mac: [
    {
      'field': 'fileProperties.signature.features',
      'title': 'Signature',
      'label': 'investigateHosts.files.fields.signature.features',
      'format': 'SIGNATURE',
      'disableSort': true
    },
    {
      'field': 'timeCreated',
      'title': 'Created',
      'label': 'investigateHosts.files.fields.timeCreated',
      'format': 'DATE',
      width: '370px'
    }
  ]
};
const defaultColumns = [
  {
    'dataType': 'checkbox',
    'width': 20,
    'class': 'rsa-form-row-checkbox',
    'componentClass': 'rsa-form-checkbox',
    'visible': true,
    'disableSort': true,
    'headerComponentClass': 'rsa-form-checkbox'
  },
  {
    'field': 'fileName',
    'label': 'investigateHosts.files.fields.fileName',
    'title': 'Filename',
    'format': 'FILENAME',
    'width': 150
  },
  {
    'field': 'fileProperties.score',
    'title': 'Risk Score',
    'label': 'investigateHosts.files.fields.score',
    'width': 100
  },
  {
    'field': 'machineCount',
    'title': 'Active On',
    'width': '6vw'
  },
  {
    'field': 'fileProperties.reputationStatus',
    'title': 'Reputation Status',
    'label': 'investigateHosts.files.fields.reputationStatus',
    'width': 100
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
    'field': 'fileProperties.fileStatus',
    'title': 'File Status',
    'label': 'investigateHosts.files.fields.fileStatus',
    'disableSort': true,
    'width': 53
  },
  {
    'field': 'fileProperties.size',
    'title': 'Size',
    'label': 'investigateHosts.files.fields.size',
    'disableSort': false,
    'format': 'SIZE',
    'width': 65
  },
  {
    'field': 'fileProperties.downloadInfo',
    'title': 'downloaded',
    'label': 'investigateHosts.files.fields.downloaded',
    'disableSort': true,
    'format': 'DOWNLOADSTATUS',
    'width': 100
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
