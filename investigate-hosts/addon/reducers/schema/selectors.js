import reselect from 'reselect';
import { RESTRICTION_TYPE } from './restriction-type';
import HOST_LIST_COLUMNS from './host-columns';
import Immutable from 'seamless-immutable';

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
    dataType: 'checkbox',
    width: '1vw',
    class: 'rsa-form-row-checkbox',
    componentClass: 'rsa-form-checkbox',
    visible: true,
    disableSort: true,
    headerComponentClass: 'rsa-form-checkbox'
  },
  {
    dataType: 'string',
    width: '15vw',
    visible: true,
    field: 'machineIdentity.machineName',
    searchable: true,
    title: 'investigateHosts.hosts.column.machineIdentity.machineName'
  },
  {
    dataType: 'string',
    visible: true,
    field: 'score',
    searchable: false,
    title: 'investigateHosts.hosts.column.score'
  }
]);

const { createSelector } = reselect;
const _schema = () => HOST_LIST_COLUMNS || [];
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
          width: COLUMN_WIDTH[field] || '10vw',
          disableSort: !SORTABLE_COLUMNS.includes(field)
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
