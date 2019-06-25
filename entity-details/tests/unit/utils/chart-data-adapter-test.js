import { module, test } from 'qunit';
import _ from 'lodash';

import { setupTest } from 'ember-qunit';
import chartDataAdapter from 'entity-details/utils/chart-data-adapter';

const chartData = [
  {
    keys: ['C:\\Program Files\\DellTPad\\normal_process_4.exe'],
    additionalInformation: null,
    value: 50,
    anomaly: false
  },
  {
    keys: ['C:\\Windows\\System32\\wininit.exe'],
    additionalInformation: null,
    value: 50,
    anomaly: true
  },
  {
    keys: ['C:\\Windows\\System32\\svchost.exe'],
    additionalInformation: null,
    value: 52,
    anomaly: false
  },
  {
    keys: ['C:\\Windows\\System32\\winlogon.exe'],
    additionalInformation: null,
    value: 52,
    anomaly: false
  }
];
module('Unit | Utils | chart-data-adapter', (hooks) => {
  setupTest(hooks);

  test('it should not update data if no settings passed', (assert) => {
    const settings = {
      chartSettings: {}
    };
    const data = chartDataAdapter(settings, chartData);
    assert.deepEqual(data.chartSettings.dataProvider, chartData);
  });
  test('it sort data based on settings fun', (assert) => {
    const settings = {
      chartSettings: {},
      sortData: (data) => {
        return _.orderBy(data, ['anomaly'], ['desc']);
      }
    };
    const data = chartDataAdapter(settings, chartData);
    assert.deepEqual(data.chartSettings.dataProvider[0], {
      keys: ['C:\\Windows\\System32\\wininit.exe'],
      additionalInformation: null,
      value: 50,
      anomaly: true
    });
  });

  test('it update data based on settings fun', (assert) => {
    const settings = {
      chartSettings: {},
      dataAdapter: (dataItem) => {
        const chartItem = {
          category: dataItem.keys[0],
          originalCategory: dataItem.keys[0],
          value: dataItem.value
        };

        if (dataItem.anomaly) {
          chartItem.color = '#CC3300';
        }
        return chartItem;
      }
    };
    const data = chartDataAdapter(settings, chartData);
    assert.deepEqual(data.chartSettings.dataProvider[0], {
      category: 'C:\\Program Files\\DellTPad\\normal_process_4.exe',
      originalCategory: 'C:\\Program Files\\DellTPad\\normal_process_4.exe',
      value: 50
    });
  });

  test('it sort and update data based on settings', (assert) => {
    const settings = {
      chartSettings: {},
      sortData: (data) => {
        return _.orderBy(data, ['anomaly'], ['desc']);
      },
      dataAdapter: (dataItem) => {
        const chartItem = {
          category: dataItem.keys[0],
          originalCategory: dataItem.keys[0],
          value: dataItem.value
        };

        if (dataItem.anomaly) {
          chartItem.color = '#CC3300';
        }
        return chartItem;
      },
      // This function will enrich data.
      dataAggregator: (list) => {
        return _.map(list, (value) => {
          value.color = 'red';
          return value;
        });
      }
    };
    const data = chartDataAdapter(settings, chartData);
    assert.deepEqual(data.chartSettings.dataProvider[0], {
      category: 'C:\\Program Files\\DellTPad\\normal_process_4.exe',
      originalCategory: 'C:\\Program Files\\DellTPad\\normal_process_4.exe',
      // Enriched data from dataAggregator function.
      color: 'red',
      value: 50
    });
  });
});
