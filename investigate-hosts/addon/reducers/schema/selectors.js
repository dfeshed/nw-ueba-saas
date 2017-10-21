import reselect from 'reselect';
import { RESTRICTION_TYPE } from './restriction-type';
import Immutable from 'seamless-immutable';

const COLUMN_WIDTH = {
  'machine.machineName': 180,
  'agentStatus.scanStatus': 130,
  'machine.scanStartTime': 130,
  'agentStatus.lastSeenTime': 130,
  'analysisData.machineRiskScore': 100,
  'machine.machineOsType': 120,
  'machine.networkInterfaces.ipv4': 130,
  'machine.users.name': 130
};

const CHECKBOX_COLUMN = Immutable.from([{
  dataType: 'checkbox',
  width: '2%',
  class: 'rsa-form-row-checkbox',
  componentClass: 'rsa-form-checkbox',
  visible: true,
  disableSort: true,
  headerComponentClass: 'rsa-form-checkbox'
}]);

const { createSelector } = reselect;
const _schema = (state) => state.endpoint.schema.schema || [];

export const getHostTableColumns = createSelector(
  _schema,
  (schema) => {
    let finalSchema = [];
    if (schema && schema.length) {
      const updatedSchema = schema.map((item) => {
        const { dataType, name: field, searchable, values } = item;
        return {
          visible: item.defaultProjection,
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

