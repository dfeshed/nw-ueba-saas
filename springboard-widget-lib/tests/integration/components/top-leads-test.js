import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';

const SELECTORS = {
  topLeads: '.top-leads',
  visualTypeDonut: '.top-leads .donut-chart',
  leadsTable: '.top-leads .rsa-data-table',
  leadsTableRow: '.top-leads .rsa-data-table-body-row',
  leadsTableHeaderCell: '.top-leads .rsa-data-table-header-cell'
};

module('Integration | Component | top-leads', function(hooks) {
  setupRenderingTest(hooks);

  test('it should render the component based on config', async function(assert) {
    this.set('widget', {
      name: 'Top Risky Hosts',
      leadType: 'Hosts',
      leadCount: 25,
      visualConfig: {
        type: 'donut-chart',
        aggregate: {
          column: ['osType'],
          type: 'COUNT'
        }
      },
      tableConfig: {
        columns: ['hostName', 'score', 'hostOsType'],
        sort: {
          keys: ['score'],
          descending: true
        }
      }
    });
    this.set('widgetData', {
      aggregate: {
        data: [
          { name: 'cats', count: 3 },
          { name: 'dogs', count: 10 },
          { name: 'horses', count: 17 }
        ]
      },
      items: [
        {
          hostName: 'Test',
          score: '100',
          osType: 'windows'
        }
      ]
    });

    await render(hbs`<TopLeads @widget={{this.widget}} @widgetData={{this.widgetData}}/>`);
    assert.dom(SELECTORS.visualTypeDonut).exists('It renders donut chart component');
    assert.dom(SELECTORS.leadsTable).exists('It renders table');
    assert.dom(SELECTORS.leadsTableRow).exists({ count: 1 }, 'It renders one row');
    assert.dom(SELECTORS.leadsTableHeaderCell).exists({ count: 3 }, 'It renders 3 columns');
  });
});
