import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import Immutable from 'seamless-immutable';
import { operationResponseDataType } from '../../reducers/selectors';
import { isFlag, FLAGS } from 'ngcoreui/services/transport/flag-helper';

const stateToComputed = (state) => ({
  dataType: operationResponseDataType(state),
  responseAsJson: state.responseAsJson
});

const treeViewResponseItem = Component.extend({
  item: null,

  keyValuePairsConfig: [
    { field: 'key', title: 'Key' },
    { field: 'value', title: 'Value' }
  ],

  @computed('item')
  raw: (item) => {
    item = Immutable.without(item, ['path', 'route', 'flags']);
    return JSON.stringify(item, null, 2);
  },

  @computed('item')
  isNotStatusUpdate: (item) => {
    return !isFlag(item.flags, FLAGS.STATUS_UPDATE);
  },

  @computed('dataType')
  isString: (dataType) => {
    return dataType ? dataType.string : false;
  },

  @computed('dataType')
  isKeyValuePairs: (dataType) => {
    return dataType ? (dataType.params || dataType.nodeInfo) : false;
  },

  @computed('item', 'isKeyValuePairs')
  keyValuePairsItems: (item, isKeyValuePairs) => {
    if (!isKeyValuePairs) {
      return null;
    }
    return Object.entries(item.params).map((item) => {
      return { key: item[0], value: item[1] };
    });
  },

  @computed('dataType', 'item')
  isCustomTable: (dataType, item) => {
    const result = dataType ? (item.results || item.params) && (
      dataType.paramList ||
      dataType.queryResults ||
      dataType.nodeList
    ) : false;
    if (result) {
      const array = (item.results && item.results.fields) || item.params;
      const keys = Object.keys(array[0]);
      return array.every((el) => {
        return Object.keys(el).every((key, index) => {
          return key === keys[index];
        });
      });
    }
    return result;
  },

  @computed('item', 'isCustomTable')
  customTableColumnConfig: (item, isCustomTable) => {
    if (!isCustomTable) {
      return [];
    }
    if (item.results || item.params) {
      const array = (item.results && item.results.fields) || item.params;
      if (array.length === 0) {
        return [];
      } else {
        return Object.keys(array[0]).map((key) => ({
          field: key,
          title: key
        }));
      }
    }
  },

  @computed('item', 'isCustomTable')
  customTableItems: (item, isCustomTable) => {
    if (!isCustomTable) {
      return [];
    }
    // Replace time values with datetime strings
    const array = (item.results && item.results.fields) || item.params;
    return array.map((row) => {
      if (row.format && row.value && row.format === 32) {
        row = row.set('value', (new Date(row.value * 1000)).toLocaleString());
        return row;
      }
      return row;
    });
  }
});

export default connect(stateToComputed)(treeViewResponseItem);
