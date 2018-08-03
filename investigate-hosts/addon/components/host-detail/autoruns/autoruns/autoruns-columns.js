import { generateColumns } from 'investigate-hosts/util/util';

const defaultColumns = [
  {
    field: 'fileName',
    title: 'Filename'
  },
  {
    field: 'fileProperties.reputationStatus',
    title: 'Reputation Status'
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