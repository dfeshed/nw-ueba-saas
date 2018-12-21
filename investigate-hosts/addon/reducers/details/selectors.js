import reselect from 'reselect';
const { createSelector } = reselect;
const _snapShots = (state) => state.endpoint.detailsInput.snapShots;
const _activeHostDetailPropertyTab = (state) => state.endpoint.detailsInput.activeHostDetailPropertyTab || 'FILE_DETAILS';
const _downloadLink = (state) => state.endpoint.detailsInput.downloadLink;

const HOST_DETAIL_PROPERTY_TABS = [
  {
    label: 'investigateHosts.tabs.fileDetails',
    name: 'FILE_DETAILS'
  },
  {
    label: 'investigateHosts.tabs.riskDetails',
    name: 'RISK'
  }
];

export const hasScanTime = createSelector(
  _snapShots,
  (snapShots) => snapShots && !!snapShots.length
);

const _machineOsType = (state) => {
  if (state.endpoint.overview.hostDetails) {
    return state.endpoint.overview.hostDetails.machine.machineOsType;
  }
  return 'windows';
};

export const getColumnsConfig = (state, config) => config[_machineOsType(state)];

export const hostDetailPropertyTabs = createSelector(
  [_activeHostDetailPropertyTab],
  (activeHostDetailPropertyTab) => {
    return HOST_DETAIL_PROPERTY_TABS.map((tab) => ({ ...tab, selected: tab.name === activeHostDetailPropertyTab }));
  }
);

export const downloadLink = createSelector(
  _downloadLink,
  (downloadLink) => {
    return downloadLink ? `${downloadLink}&${Number(new Date())}` : null;
  }
);
