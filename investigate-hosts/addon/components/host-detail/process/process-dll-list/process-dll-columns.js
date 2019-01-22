// Process details table header configuration
import { generateColumns } from 'investigate-hosts/util/util';

const defaultColumnsConfig = [
  {
    field: 'fileName',
    title: 'investigateHosts.process.dll.dllName',
    width: '10vw',
    format: 'FILENAME'
  },
  {
    field: 'signature',
    title: 'investigateHosts.process.signature',
    format: 'SIGNATURE',
    width: '28vw'
  },
  {
    field: 'path',
    title: 'investigateHosts.process.dll.filePath',
    width: '20vw'
  }
];

const machineOsBasedColumnsConfig = {
  mac: [{
    field: 'timeCreated',
    title: 'investigateHosts.process.creationTime',
    format: 'DATE',
    width: '35vw'
  }],
  windows: [{
    field: 'timeCreated',
    title: 'investigateHosts.process.creationTime',
    format: 'DATE',
    width: '35vw'
  }],
  linux: []
};

const columnsConfig = generateColumns(machineOsBasedColumnsConfig, defaultColumnsConfig);

export default columnsConfig;