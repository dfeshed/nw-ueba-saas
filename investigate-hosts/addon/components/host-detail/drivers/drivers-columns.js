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
    title: 'Filename',
    format: 'FILENAME'
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
    'disableSort': true
  },
  {
    field: 'signature',
    title: 'Signature',
    format: 'SIGNATURE'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'downloaded',
    label: 'investigateHosts.files.fields.downloaded',
    disableSort: true,
    format: 'DOWNLOADSTATUS'
  },
  {
    field: 'path',
    title: 'Path'
  }
];

const machineOsBasedConfig = {
  mac: [
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '20%'
    }
  ],
  windows: [
    {
      field: 'timeCreated',
      title: 'FILE CREATION TIME',
      format: 'DATE',
      width: '20%'
    }
  ],
  linux: []
};

const columnsConfig = generateColumns(machineOsBasedConfig, defaultColumns);

export default columnsConfig;