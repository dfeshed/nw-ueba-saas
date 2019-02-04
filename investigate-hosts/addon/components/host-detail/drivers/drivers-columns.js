import { generateColumns } from 'investigate-hosts/util/util';

const defaultColumns = [
  {
    'dataType': 'checkbox',
    'width': '2vw',
    'class': 'rsa-form-row-checkbox',
    'componentClass': 'rsa-form-checkbox',
    'visible': true,
    'disableSort': true,
    'headerComponentClass': 'rsa-form-checkbox'
  },
  {
    field: 'fileName',
    title: 'Filename',
    format: 'FILENAME',
    width: '150px'
  },
  {
    field: 'machineFileScore',
    title: 'Local Risk Score'
  },
  {
    field: 'fileProperties.score',
    title: 'Global Risk Score'
  },
  {
    field: 'machineCount',
    title: 'Active On',
    width: '6vw',
    disableSort: true
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'Reputation',
    width: '130px'
  },
  {
    'field': 'fileProperties.fileStatus',
    'title': 'File Status',
    'label': 'investigateHosts.files.fields.fileStatus',
    'disableSort': false,
    width: '150px'
  },
  {
    field: 'signature',
    title: 'Signature',
    format: 'SIGNATURE',
    width: '200px'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'downloaded',
    label: 'investigateHosts.files.fields.downloaded',
    format: 'DOWNLOADSTATUS',
    width: '150px'
  },
  {
    field: 'path',
    title: 'Path',
    width: '200px'
  }
];

const machineOsBasedConfig = {
  mac: [
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '150px'
    }
  ],
  windows: [
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '150px'
    }
  ],
  linux: []
};

const columnsConfig = generateColumns(machineOsBasedConfig, defaultColumns);

export default columnsConfig;
