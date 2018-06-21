import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

const DEFAULT_RANGE = [0, 100];

const FILTER_FIELD = {
  ALERT: 'risk_score',
  INCIDENT: 'averageAlertRiskScore'
};

export default Component.extend({
  layout,
  tagName: 'box',
  classNames: ['risk-properties-panel'],
  data: [],
  filterRange: DEFAULT_RANGE,

  @computed('data', 'activeDataSourceTab', 'filterRange')
  filteredData(data, type, filterRange) {
    const [low, high] = filterRange;
    const filterField = FILTER_FIELD[type];
    let filtered = [];
    const contextData = data.resultList;
    if (contextData) {
      filtered = contextData.filter((entry) => {
        const scoreField = entry[filterField];
        if (scoreField >= low && scoreField <= high) {
          return entry;
        }
      });
    }
    return { data: filtered, completed: true };
  },

  @computed('filteredData')
  hasfilteredData(filteredData) {
    return (filteredData && filteredData.data.length);
  },

  actions: {
    updateFilter(range) {
      this.set('filterRange', range);
    }
  }
});
