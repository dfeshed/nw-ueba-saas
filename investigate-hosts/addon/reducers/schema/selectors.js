import reselect from 'reselect';
import { RESTRICTION_TYPE } from './restriction-type';
import Immutable from 'seamless-immutable';

const COLUMN_WIDTH = {
  'agentStatus.scanStatus': 130,
  'machine.scanStartTime': 150,
  'agentStatus.lastSeenTime': 150,
  'analysisData.machineRiskScore': 100,
  'machine.machineOsType': 120,
  'machine.networkInterfaces.ipv4': 130,
  'machine.users.name': 130
};

const DEFAULT_COLUMN = Immutable.from([
  {
    dataType: 'checkbox',
    width: 22,
    class: 'rsa-form-row-checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true,
    headerComponentClass: 'rsa-form-checkbox'
  },
  {
    dataType: 'string',
    width: 300,
    visible: true,
    field: 'machine.machineName',
    searchable: true,
    title: 'investigateHosts.hosts.column.machine.machineName'
  }

]);

const { createSelector } = reselect;
const _schema = (state) => state.endpoint.schema.schema || [];
const _preferences = (state) => state.preferences.preferences;

const _visibleColumns = createSelector(
  _preferences,
  (preferences) => {
    if (preferences.machinePreference) {
      return preferences.machinePreference.visibleColumns;
    }
    return [];
  }
);

export const getHostTableColumns = createSelector(
  [_schema, _visibleColumns],
  (schema, _visibleColumns) => {
    let finalSchema = [];
    if (schema && schema.length) {
      const updatedSchema = schema.map((item) => {
        const { dataType, name: field, searchable, values } = item;
        const visible = _visibleColumns.includes(field);
        return {
          visible,
          dataType,
          field,
          searchable,
          values,
          title: `investigateHosts.hosts.column.${field}`,
          width: COLUMN_WIDTH[field]
        };
      });

      const visibleList = updatedSchema.filter((column) => column.visible);

      if (visibleList) {
        // Making it as mutable as schema is passed down to data-table component and data-table component expecting simple array/ember array
        finalSchema = DEFAULT_COLUMN.concat(updatedSchema).asMutable();
      }
    }
    return finalSchema;
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


