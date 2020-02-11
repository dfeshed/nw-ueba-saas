import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, setupOnerror, resetOnerror } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';
const SELECTORS = {
  chartWidget: '.chart-widget',
  chartType: '.donut-chart'
};

module('Integration | Component | chart-widget', function(hooks) {
  setupRenderingTest(hooks);

  test('it throws the error if no chart type passed', async function(assert) {
    setupOnerror(function(err) {
      assert.ok(err);
    });
    await render(hbs`<ChartWidget />`);
    resetOnerror();
  });

  test('it renders the appropriate chart based on the chart type', async function(assert) {
    const data = {
      aggregate: {
        data: [
          { name: 'cats', value: 3 },
          { name: 'dogs', value: 10 },
          { name: 'horses', value: 17 }
        ]
      }
    };
    this.set('data', data);
    this.set('config', {
      chartType: 'donut-chart',
      aggregate: {
        columns: ['osType'],
        type: 'COUNT'
      }
    });

    await render(hbs`<ChartWidget @data={{this.data}} @config={{this.config}}/>`);
    assert.dom(SELECTORS.chartType).exists();
  });

});
