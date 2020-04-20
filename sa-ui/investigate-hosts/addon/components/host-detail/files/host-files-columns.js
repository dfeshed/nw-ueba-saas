import { generateColumns } from 'investigate-hosts/util/util';

let columnsConfig = {
  linux: [
    {
      field: 'derivedOwner.username',
      title: 'investigateHosts.files.fields.owner.userName',
      label: 'investigateHosts.files.fields.owner.userName',
      disableSort: true
    },
    {
      field: 'derivedOwner.groupname',
      title: 'investigateHosts.files.fields.owner.groupName',
      label: 'investigateHosts.files.fields.owner.groupName',
      disableSort: true,
      width: '370px'
    }
  ],
  windows: [
    {
      'field': 'fileProperties.signature.features',
      'title': 'investigateHosts.files.fields.signature.features',
      'label': 'investigateHosts.files.fields.signature.features',
      'format': 'SIGNATURE',
      'disableSort': true
    },
    {
      'field': 'timeCreated',
      'title': 'investigateHosts.files.fields.timeCreated',
      'label': 'investigateHosts.files.fields.timeCreated',
      'format': 'DATE',
      width: '12vw'
    }
  ],
  mac: [
    {
      'field': 'fileProperties.signature.features',
      'title': 'investigateHosts.files.fields.signature.features',
      'label': 'investigateHosts.files.fields.signature.features',
      'format': 'SIGNATURE',
      'disableSort': true
    },
    {
      'field': 'timeCreated',
      'title': 'investigateHosts.files.fields.timeCreated',
      'label': 'investigateHosts.files.fields.timeCreated',
      'format': 'DATE',
      width: '12vw'
    }
  ]
};
const defaultColumns = [
  {
    'dataType': 'checkbox',
    'width': '2vw',
    'class': 'rsa-form-row-checkbox',
    'componentClass': 'rsa-form-checkbox',
    'visible': true,
    'disableSort': true,
    'headerComponentClass': 'rsa-form-checkbox',
    field: 'checkbox'
  },
  {
    'field': 'fileName',
    'label': 'investigateHosts.files.fields.fileName',
    'title': 'investigateHosts.files.fields.fileName',
    'format': 'FILENAME',
    'width': 150
  },
  {
    'field': 'machineFileScore',
    'label': 'investigateHosts.detailsColumns.machineFileScore',
    'title': 'investigateHosts.detailsColumns.machineFileScore',
    'width': '8vw'
  },
  {
    'field': 'fileProperties.score',
    'title': 'investigateHosts.detailsColumns.globalScore',
    'label': 'investigateHosts.detailsColumns.globalScore',
    'width': '8vw'
  },
  {
    'field': 'fileProperties.hostCount',
    'title': 'investigateHosts.detailsColumns.hostCount',
    'width': '6vw'
  },
  {
    'field': 'fileProperties.fileStatus',
    'title': 'investigateHosts.detailsColumns.fileStatus',
    'label': 'investigateHosts.detailsColumns.fileStatus',
    'disableSort': false,
    'width': 100
  },
  {
    'field': 'fileProperties.reputationStatus',
    'title': 'investigateHosts.detailsColumns.reputationStatus',
    'label': 'investigateHosts.detailsColumns.reputationStatus',
    'width': 100
  },
  {
    'field': 'fileProperties.downloadInfo',
    'title': 'investigateHosts.detailsColumns.downloadInfo',
    'label': 'investigateHosts.detailsColumns.downloadInfo',
    'format': 'DOWNLOADSTATUS',
    'width': 200
  },
  {
    'field': 'path',
    'title': 'investigateHosts.files.fields.path',
    'label': 'investigateHosts.files.fields.path',
    'width': '18vw'
  },
  {
    'field': 'fileProperties.size',
    'title': 'investigateHosts.files.fields.size',
    'label': 'investigateHosts.files.fields.size',
    'disableSort': false,
    'format': 'SIZE',
    'width': 165
  },
  {
    'field': 'fileProperties.entropy',
    'title': 'investigateHosts.files.fields.entropy',
    'label': 'investigateHosts.files.fields.entropy',
    'disableSort': true,
    'format': 'DECIMAL',
    'width': 53
  }
];

columnsConfig = generateColumns(columnsConfig, defaultColumns);

export default columnsConfig;
