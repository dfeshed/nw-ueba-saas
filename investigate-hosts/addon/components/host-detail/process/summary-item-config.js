// Process view header configuration
import { generateColumns } from 'investigate-hosts/util/util';

const defaultSummaryConfig = [
  {
    label: 'investigateHosts.process.processName',
    field: 'fileName',
    cssClass: 'col-xs-2 col-md-2'
  },
  {
    label: 'investigateHosts.process.owner',
    field: 'process.owner',
    cssClass: 'col-xs-1 col-md-1'
  },
  {
    label: 'investigateHosts.process.parentId',
    field: 'process.parentPid',
    cssClass: 'col-xs-1 col-md-1'
  },
  {
    label: 'investigateHosts.process.path',
    field: 'path',
    cssClass: 'col-xs-4 col-md-4'
  },
  {
    label: 'investigateHosts.process.launchArguments',
    field: 'process.launchArguments',
    cssClass: 'col-xs-5 col-md-5'
  }
];

const machineOsBasedSummaryConfig = {
  mac: [],
  windows: [],
  linux: []
};

const summaryItems = generateColumns(machineOsBasedSummaryConfig, defaultSummaryConfig);

export default summaryItems;
