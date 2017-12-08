import { generateColumns } from 'investigate-hosts/util/util';

const defaultColumns = [
  {
    field: 'fileName',
    title: 'File Name'
  },
  {
    field: 'signature',
    title: 'Signature',
    format: 'SIGNATURE',
    width: '10%',
    disableSort: true
  },
  {
    field: 'machineCount',
    title: 'Machine Count',
    disableSort: true
  },
  {
    field: 'path',
    title: 'Path',
    width: '20%',
    disableSort: true
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