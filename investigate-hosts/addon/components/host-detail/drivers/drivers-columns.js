import { generateColumns } from 'investigate-hosts/util/util';

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
    field: 'fileName',
    title: 'Filename'
  },
  {
    field: 'fileProperties.score',
    title: 'Risk Score'
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'Reputation Status'
  },
  {
    'field': 'fileProperties.fileStatus',
    'title': 'File Status',
    'label': 'investigateHosts.files.fields.fileStatus',
    'disableSort': true,
    'width': 53
  },
  {
    field: 'signature',
    title: 'Signature',
    format: 'SIGNATURE',
    width: '10%'
  },
  {
    field: 'path',
    title: 'Path',
    width: '20%'
  }
];

const machineOsBasedConfig = {
  mac: [
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      width: '20%',
      format: 'DATE'
    }
  ],
  windows: [
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      width: '20%',
      format: 'DATE'
    }
  ],
  linux: []
};

const columnsConfig = generateColumns(machineOsBasedConfig, defaultColumns);

export default columnsConfig;