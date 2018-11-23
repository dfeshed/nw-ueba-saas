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
    field: 'fileProperties.downloadInfo',
    title: 'downloaded',
    label: 'investigateHosts.files.fields.downloaded',
    disableSort: true,
    format: 'DOWNLOADSTATUS',
    width: 100
  },
  {
    field: 'fileProperties.fileStatus',
    title: 'File Status',
    width: '15%'
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
      title: 'REGISTRY PATH'
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