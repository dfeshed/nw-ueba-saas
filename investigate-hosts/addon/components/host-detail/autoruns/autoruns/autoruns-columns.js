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
    format: 'FILENAME'
  },
  {
    field: 'machineFileScore',
    title: 'Risk Score'
  },
  {
    field: 'fileProperties.score',
    title: 'Global Score'
  },
  {
    field: 'machineCount',
    title: 'Active On',
    width: '6vw',
    disableSort: true
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'Reputation'
  },
  {
    field: 'fileProperties.fileStatus',
    title: 'File Status',
    width: '15vw'
  },
  {
    field: 'signature',
    title: 'SIGNATURE',
    format: 'SIGNATURE'
  },
  {
    field: 'fileProperties.downloadInfo',
    title: 'downloaded',
    label: 'investigateHosts.files.fields.downloaded',
    format: 'DOWNLOADSTATUS',
    width: 100
  },
  {
    field: 'timeCreated',
    title: 'FILE CREATION TIME',
    format: 'DATE',
    width: '10vw'
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'path',
      title: 'Path'
    }
  ],
  windows: [
    {
      field: 'registryPath',
      title: 'REGISTRY PATH',
      width: '45vw'
    }
  ],
  linux: [
    {
      field: 'path',
      title: 'Path'
    }
  ]
};

columnsConfig = generateColumns(columnsConfig, defaultColumns);

export default columnsConfig;
