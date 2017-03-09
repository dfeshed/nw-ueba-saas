import Ember from 'ember';
import layout from './template';
import computed from 'ember-computed-decorators';

const {
  Component
} = Ember;

export default Component.extend({
  layout,
  classNames: 'rsa-context-panel__context-data-table',

  @computed('data', 'sortColumn')
  sortedData(columnData, columnName) {
    if (columnName) {
      columnData.sort((a, b) => (a[columnName] - b[columnName]));
    }
    return columnData;
  },

  actions: {
    activate(option) {
      this.sendAction('activatePanel', option);
    }
  },

  constructPath(incId, path) {
    path = path.replace('{0}', incId);
    return window.location.origin.concat(path);
  }

});
