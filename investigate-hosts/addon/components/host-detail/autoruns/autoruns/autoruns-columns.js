import { generateColumns } from 'investigate-hosts/util/util';

const defaultColumns = [
  {
    field: 'fileName',
    title: 'File Name'
  }
];
let columnsConfig = {
  mac: [
    {
      field: 'path',
      title: 'Path',
      width: '20%',
      disableSort: true
    }
  ],
  windows: [
    {
      field: 'registryPath',
      title: 'REGISTRY PATH',
      width: '20%',
      disableSort: true
    }
  ],
  linux: [
    {
      field: 'path',
      title: 'Path',
      width: '20%',
      disableSort: true
    }
  ]
};

columnsConfig = generateColumns(columnsConfig, defaultColumns);

export default columnsConfig;