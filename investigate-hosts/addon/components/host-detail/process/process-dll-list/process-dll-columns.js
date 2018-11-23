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
    format: 'SIGNATURE',
    width: 50
  },
  {
    field: 'path',
    title: 'investigateHosts.process.dll.filePath',
    width: 50
  }
];

const machineOsBasedColumnsConfig = {
  mac: [{
    field: 'timeCreated',
    title: 'investigateHosts.process.creationTime',
    format: 'DATE',
    width: 50
  }],
  windows: [{
    field: 'timeCreated',
    title: 'investigateHosts.process.creationTime',
    format: 'DATE',
    width: 50
  }],
  linux: []
};

const columnsConfig = generateColumns(machineOsBasedColumnsConfig, defaultColumnsConfig);

export default columnsConfig;