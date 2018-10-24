// Process details table header configuration
import { generateColumns } from 'investigate-hosts/util/util';

const defaultColumnsConfig = [
  {
    field: 'fileName',
    title: 'investigateHosts.process.dll.dllName',
    width: 50
  },
  {
    field: 'signature',
    title: 'investigateHosts.process.signature',
    format: 'SIGNATURE'
  },
  {
    field: 'path',
    title: 'investigateHosts.process.dll.filePath'
  }
];

const machineOsBasedColumnsConfig = {
  mac: [{
    field: 'timeCreated',
    title: 'investigateHosts.process.creationTime',
    format: 'DATE',
    width: '22%'
  }],
  windows: [{
    field: 'timeCreated',
    title: 'investigateHosts.process.creationTime',
    format: 'DATE',
    width: '22%'
  }],
  linux: []
};

const columnsConfig = generateColumns(machineOsBasedColumnsConfig, defaultColumnsConfig);

export default columnsConfig;