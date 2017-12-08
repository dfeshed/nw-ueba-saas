import reselect from 'reselect';
import { RESTRICTION_TYPE } from './restriction-type';
import Immutable from 'seamless-immutable';
import CONFIG from './config';

const COLUMN_WIDTH = {
  'machine.machineName': 180,
  'agentStatus.scanStatus': 130,
  'machine.scanStartTime': 150,
  'agentStatus.lastSeenTime': 150,
  'analysisData.machineRiskScore': 100,
  'machine.machineOsType': 120,
  'machine.networkInterfaces.ipv4': 130,
  'machine.users.name': 130
};

const CHECKBOX_COLUMN = Immutable.from([{
  dataType: 'checkbox',
  width: '22',
  class: 'rsa-form-row-checkbox',
  componentClass: 'rsa-form-checkbox',
  visible: true,
  disableSort: true,
  headerComponentClass: 'rsa-form-checkbox'
}]);

const { createSelector } = reselect;
const _schema = (state) => state.endpoint.schema.schema || [];
const _visibleColumns = (state) => state.endpoint.schema.visibleColumns;
const _userProjectionChanged = (state) => state.endpoint.schema.userProjectionChanged;

export const getHostTableColumns = createSelector(
  [_schema, _visibleColumns, _userProjectionChanged],
  (schema, visibleColumns, userProjectionChanged) => {
    let finalSchema = [];
    if (schema && schema.length) {
      const updatedSchema = schema.map((item) => {
        const { dataType, name: field, searchable, values, userProjection } = item;
        let visible = item.defaultProjection;
        if (visibleColumns.length) {
          if (userProjectionChanged) {
            visible = userProjection || item.defaultProjection;
          } else {
            // If user preferences is saved, it should override default projections
            visible = visibleColumns.includes(field);
          }
        }
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
      // Making it as mutable as schema is passed down to data-table component and data-table component expecting simple array/ember array
      finalSchema = CHECKBOX_COLUMN.concat(updatedSchema).asMutable();
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

export const isSchemaLoaded = createSelector(
  getHostTableColumns,
  (columns) => {
    return !!columns.length;
  }
);

export const preferencesConfig = createSelector(
  [isSchemaLoaded, _schema],
  (isSchemaLoaded, columns) => {
    const fileConfig = { ...CONFIG };
    if (isSchemaLoaded) {
      // Set options of the dropdown from column schema
      const visibleColumns = fileConfig.items.find((item) => item.field === 'machinePreference.visibleColumns');
      const sortColumns = fileConfig.items.find((item) => item.field === 'machinePreference.sortField');
      const options = columns.map((column) => column.name);
      visibleColumns.options = options;
      sortColumns.options = options;
      return fileConfig;
    }
    return fileConfig;
  });

