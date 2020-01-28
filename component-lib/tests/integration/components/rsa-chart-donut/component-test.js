import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';
let data;

module('Integration | Component | donut-chart', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(() => {
    data = [
      { name: 'cats', count: 3 },
      { name: 'dogs', count: 10 },
      { name: 'horses', count: 17 }
    ];
  });

  test('it renders the donut chart with passed down data', async function(assert) {
    this.set('data', data);
    await render(hbs`<RsaChartDonut @data={{this.data}}/>`);
    assert.equal(findAll('.donut-chart path').length, 3, 'It renders 3 pie');
  });

  test('it renders the legend for the donut chart', async function(assert) {
    this.set('data', data);
    await render(hbs`<RsaChartDonut @data={{this.data}}/>`);
    const legendStyle = findAll('.donut-chart .legend')[2].getAttribute('style');
    const pieStyle = findAll('.donut-chart path')[2].getAttribute('style');
    assert.equal(findAll('.donut-chart .legend').length, 3, 'It renders 3 pie');
    assert.equal(pieStyle, legendStyle, 'Legend color is same as pie color');
  });

  test('it does not renders the legend', async function(assert) {
    const data = [
      { name: 'cats', value: 3 },
      { name: 'dogs', value: 10 },
      { name: 'horses', value: 17 }
    ];
    this.set('data', data);
    this.set('options', { showLegend: false, valueProp: 'value' });
    await render(hbs`<RsaChartDonut @data={{this.data}} @options={{this.options}}/>`);
    assert.equal(findAll('.donut-chart path').length, 3, 'It renders 3 pie');
    assert.equal(findAll('.donut-chart .legend').length, 0, 'Legend is not rendered');
  });

  test('it renders the aggregate column text', async function(assert) {
    this.set('data', data);
    this.set('columnName', 'Operating System');
    await render(hbs`<RsaChartDonut @data={{this.data}} @columnName={{this.columnName}}/>`);
    assert.equal(findAll('.donut-chart .column-name').length, 1, 'Aggregate column label is rendered');
  });

  test('it does not renders the aggregate column text', async function(assert) {
    this.set('data', data);
    await render(hbs`<RsaChartDonut @data={{this.data}}/>`);
    assert.equal(findAll('.donut-chart .aggregate-column').length, 0, 'Aggregate column label is rendered');
  });

  test('it should display no data message', async function(assert) {
    await render(hbs`<RsaChartDonut/>`);
    assert.equal(findAll('.empty-message').length, 1, 'Aggregate column label is rendered');
  });

});
