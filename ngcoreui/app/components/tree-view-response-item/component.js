import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import Immutable from 'seamless-immutable';
import { operationResponseDataType } from '../../reducers/selectors';
import { isFlag, FLAGS } from 'ngcoreui/services/transport/flag-helper';

const stateToComputed = (state) => ({
  dataType: operationResponseDataType(state)
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

  @computed('item')
  keyValuePairsItems: (item) => {
    return Object.entries(item.params).map((item) => {
      return { key: item[0], value: item[1] };
    });
  },

  @computed('dataType')
  isCustomTable: (dataType) => {
    return dataType ? (dataType.paramsList || dataType.queryResults || dataType.nodeList) : false;
  },

  @computed('item')
  customTableColumnConfig: (item) => {
    if (item.results.fields.length === 0) {
      return [];
    } else {
      return Object.keys(item.results.fields[0]).map((key) => ({
        field: key,
        title: key
      }));
    }
  },

  @computed('item')
  customTableItems: (item) => {
    // Replace time values with datetime strings
    return item.results.fields.map((row) => {
      if (row.format && row.value && row.format === 32) {
        row = row.set('value', (new Date(row.value * 1000)).toLocaleString());
        return row;
      }
      return row;
    });
  }
});

export default connect(stateToComputed)(treeViewResponseItem);
