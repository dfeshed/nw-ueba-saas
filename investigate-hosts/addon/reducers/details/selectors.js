import reselect from 'reselect';
const { createSelector } = reselect;
const _snapShots = (state) => state.endpoint.detailsInput.snapShots;
const _activeHostDetailPropertyTab = (state) => state.endpoint.detailsInput.activeHostDetailPropertyTab || 'FILE_DETAILS';
const _downloadLink = (state) => state.endpoint.detailsInput.downloadLink;
const _preferences = (state) => state.preferences.preferences;
const _tableId = (state, tableId) => tableId;

const HOST_DETAIL_PROPERTY_TABS = [
  {
    label: 'investigateHosts.tabs.fileDetails',
    name: 'FILE_DETAILS'
  },
  {
    label: 'investigateHosts.tabs.localRiskDetails',
    name: 'RISK'
  }
];

export const hasScanTime = createSelector(
  _snapShots,
  (snapShots) => snapShots && !!snapShots.length
);

const _machineOsType = (state) => {
  if (state.endpoint.overview.hostDetails) {
    return state.endpoint.overview.hostDetails.machineIdentity.machineOsType;
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

export const savedColumnsConfig = createSelector(
  [_preferences, _tableId],
  (preferences, tableId) => {
    if (preferences.machinePreference) {
      const config = preferences.machinePreference.columnConfig || [];
      const columnConfig = config.filter((item) => {
        return item.tableId === tableId;
      });
      return columnConfig;
    }
    return [];
  }
);

export const updateConfig = (schema, savedConfig) => {
  if (schema && schema.length) {
    if (savedConfig.length) {
      const [ { columns } ] = savedConfig;
      const updatedSchema = schema.map((item, index) => {
        const currentColumn = columns.filter((column) => {
          return column.field === item.field;
        });
        let visible = false;
        let displayIndex, width;

        if (currentColumn && currentColumn.length) {
          visible = true;
          const [{ displayIndex: index, width: columnWidth }] = currentColumn;
          displayIndex = parseInt(index, 10);
          width = columnWidth;
        } else {
          width = item.width;
          displayIndex = index;
        }
        return { ...item, visible, preferredDisplayIndex: displayIndex, width };
      });

      const visibleList = updatedSchema.filter((column) => column.visible);
      if (visibleList) {
        return updatedSchema;
      }
      return [];
    }
  }
  return schema;
};

