// Process view header configuration
import { generateColumns } from 'investigate-hosts/util/util';

const defaultSummaryConfig = [
  {
    label: 'investigateHosts.process.processName',
    field: 'fileName'
  },
  {
    label: 'investigateHosts.process.parentId',
    field: 'process.parentPid'
  },
  {
    label: 'investigateHosts.process.path',
    field: 'path'
  },
  {
    label: 'investigateHosts.process.launchArguments',
    field: 'process.launchArguments',
    cssClass: 'col-xs-6 col-md-6'
  }
];

const machineOsBasedSummaryConfig = {
  mac: [],
  windows: [],
  linux: []
};

const summaryItems = generateColumns(machineOsBasedSummaryConfig, defaultSummaryConfig);

export default summaryItems;