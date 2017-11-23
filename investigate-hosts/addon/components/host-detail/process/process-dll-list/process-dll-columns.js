// Process details table header configuration
import { generateColumns } from 'investigate-hosts/util/util';

const defaultColumnsConfig = [
  {
    field: 'fileName',
    title: 'investigateHosts.process.dll.dllName',
    width: 85
  },
  {
    field: 'signature',
    title: 'investigateHosts.process.signature',
    format: 'SIGNATURE',
    width: 65
  },
  {
    field: 'hashLookup',
    title: 'investigateHosts.process.hashlookup',
    width: 43
  },
  {
    field: 'path',
    title: 'investigateHosts.process.dll.filePath',
    width: 200
  }
];

const machineOsBasedColumnsConfig = {
  mac: [{
    field: 'timeCreated',
    title: 'investigateHosts.process.creationTime',
    format: 'DATE',
    width: 100
  }],
  windows: [{
    field: 'timeCreated',
    title: 'investigateHosts.process.creationTime',
    format: 'DATE',
    width: 100
  }],
  linux: []
};

const columnsConfig = generateColumns(machineOsBasedColumnsConfig, defaultColumnsConfig);

export default columnsConfig;