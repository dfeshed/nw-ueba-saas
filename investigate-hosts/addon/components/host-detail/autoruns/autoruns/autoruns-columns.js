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
    field: 'fileProperties.fileStatus',
    title: 'File Status'
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'path',
      title: 'Path',
      width: '80%'
    }
  ],
  windows: [
    {
      field: 'registryPath',
      title: 'REGISTRY PATH',
      width: '80%'
    }
  ],
  linux: [
    {
      field: 'path',
      title: 'Path',
      width: '80%'
    }
  ]
};

columnsConfig = generateColumns(columnsConfig, defaultColumns);

export default columnsConfig;