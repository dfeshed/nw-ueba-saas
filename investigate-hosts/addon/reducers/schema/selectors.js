import reselect from 'reselect';
import { RESTRICTION_TYPE } from './restriction-type';
import HOST_LIST_COLUMNS from './host-columns';
import Immutable from 'seamless-immutable';
import DEFAULT_HOSTS_PREFERENCE from './host-default-preference';

const COLUMN_WIDTH = {
  'agentStatus.scanStatus': '8vw',
  'machine.scanStartTime': '9vw',
  'agentStatus.lastSeenTime': '8vw',
  'analysisData.machineRiskScore': '6vw',
  'machineIdentity.machineOsType': '7vw',
  'machineIdentity.networkInterfaces.ipv4': '5vw',
  'machine.users.name': '15vw',
  'groupPolicy.policyStatus': '6vw',
  'machineIdentity.agentMode': '5vw',
  'machineIdentity.agentVersion': '6vw',
  'machineIdentity.networkInterfaces.macAddress': '7vw'
};

const SORTABLE_COLUMNS = [
  'score',
  'machineIdentity.machineName',
  'groupPolicy.groups.name',
  'groupPolicy.policyStatus',
  'machineIdentity.agentVersion',
  'machine.users.name',
  'machineIdentity.machineOsType',
  'machineIdentity.agentMode',
  'machineIdentity.operatingSystem.description'
];

const DEFAULT_COLUMN = Immutable.from([
  {
    field: 'checkbox',
    dataType: 'checkbox',
    width: '1vw',
    class: 'rsa-form-row-checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true,
    headerComponentClass: 'rsa-form-checkbox',
    preferredDisplayIndex: 0
  },
  {
    dataType: 'string',
    width: '15vw',
    visible: true,
    field: 'machineIdentity.machineName',
    searchable: true,
    title: 'investigateHosts.hosts.column.machineIdentity.machineName',
    preferredDisplayIndex: 1
  },
  {
    dataType: 'string',
    visible: true,
    field: 'score',
    searchable: false,
    title: 'investigateHosts.hosts.column.score',
    preferredDisplayIndex: 2
  }
]);

const { createSelector } = reselect;
const _schema = () => HOST_LIST_COLUMNS || [];
const _preferences = (state) => state.preferences.preferences;

const _visibleColumns = createSelector(
  _preferences,
  (preferences) => {
    if (preferences.machinePreference) {
      return preferences.machinePreference.columnConfig || [];
    }
    return [];
  }
);

export const extractHostColumns = createSelector(
  _visibleColumns,
  (visibleColumns) => {
    const savedColumns = visibleColumns.filter((item) => {
      return item.tableId === 'hosts';
    });

    if (savedColumns && savedColumns.length) {
      const [{ columns }] = savedColumns;
      return columns;
    } else {
      const [{ columns }] = DEFAULT_HOSTS_PREFERENCE.machinePreference.columnConfig;
      return columns;
    }
  }
);

export const getHostTableColumns = createSelector(
  [_schema, extractHostColumns],
  (schema, columns) => {
    if (columns && columns.length) {
      let counter = columns.length + 3;
      if (schema && schema.length) {
        const updatedSchema = schema.map((item) => {
          const { dataType, name: field, searchable, values } = item;
          const currentColumn = columns.filter((column) => {
            return column.field === field;
          });
          let visible = false;
          let displayIndex, width;

          if (currentColumn && currentColumn.length) {
            visible = true;
            const [{ displayIndex: index, width: columnWidth }] = currentColumn;
            displayIndex = parseInt(index, 10);
            width = columnWidth;
          } else {
            displayIndex = counter;
            counter++;
          }

          return {
            visible,
            dataType,
            field,
            searchable,
            values,
            preferredDisplayIndex: displayIndex,
            title: `investigateHosts.hosts.column.${field}`,
            width: width || COLUMN_WIDTH[field] || '10vw',
            disableSort: !SORTABLE_COLUMNS.includes(field)
          };
        });
        // Set the default columns, if not present in stored configuration
        DEFAULT_COLUMN.forEach((column) => {
          if (column.dataType === 'checkbox') {
            updatedSchema.unshift(column);
          } else {
            const [item] = columns.filter((col) => {
              return column.field === col.field;
            });
            if (!item) {
              updatedSchema.unshift(column);
            }
          }

        });
        const visibleList = updatedSchema.filter((column) => column.visible);
        if (visibleList) {
          return updatedSchema;
        }
      }
      return [];
    }

  }
);

export const prepareSchema = createSelector(
  _schema,
  (schema) => {
    return schema.map((item) => {
      const newItem = { ...item }; // As we are modifying the object creating the new
      newItem.title = `investigateHosts.hosts.column.${item.name}`;
      newItem.selectedValues = [];
      newItem.selectedValue = (item.dataType === 'BOOLEAN') ? false : null;
      newItem.startValue = null;
      newItem.endValue = null;
      if (newItem.dataType === 'DATE') {
        newItem.hasCustomDate = false;
      }
      if (item.name === 'agentStatus.lastSeenTime') {
        newItem.operator = { ...RESTRICTION_TYPE.DATE_TIME_AGO };
      } else {
        newItem.operator = { ...RESTRICTION_TYPE[item.dataType] };
      }
      return newItem;
    });
  }
);
