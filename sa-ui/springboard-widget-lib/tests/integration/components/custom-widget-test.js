import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, waitUntil, findAll, click } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';

const SELECTORS = {
  topLeads: '.custom-widget',
  visualTypeDonut: '.custom-widget .donut-chart',
  leadsTable: '.custom-widget .rsa-data-table',
  leadsTableRow: '.custom-widget .rsa-data-table-body-row',
  leadsTableHeaderCell: '.custom-widget .rsa-data-table-header-cell',
  loadingIndicator: '.custom-widget .rsa-loader',
  viewAllHeader: '.springboard-widget__header-arrow'
};

module('Integration | Component | top-leads', function(hooks) {
  setupRenderingTest(hooks);

  test('it should render the component based on config', async function(assert) {
    this.set('widget', {
      name: 'Top Risky Hosts',
      leadType: 'Hosts', // Need this for master API
      content: [
        {
          type: 'chart',
          chartType: 'donut-chart',
          aggregate: {
            columns: ['osType'],
            type: 'COUNT'
          }
        },
        {
          type: 'table',
          columns: ['hostName', 'score', 'hostOsType'],
          size: 25,
          sort: {
            keys: ['score'],
            descending: true
          }
        }
      ]
    });
    this.set('data', {
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

    this.set('fetchData', () => {
      return new Promise((resolve) => setTimeout(() => resolve({
        data: {
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
        }
      }), 1));
    });

    await render(hbs`<CustomWidget @widget={{this.widget}} @fetchData={{this.fetchData}}/>`);
    await waitUntil(() => findAll(SELECTORS.loadingIndicator).length === 0);
    assert.dom(SELECTORS.visualTypeDonut).exists('It renders donut chart component');
    assert.dom(SELECTORS.leadsTable).exists('It renders table');
    assert.dom(SELECTORS.leadsTableRow).exists({ count: 1 }, 'It renders one row');
    assert.dom(SELECTORS.leadsTableHeaderCell).exists({ count: 3 }, 'It renders 3 columns');
  });

  test('navigate will call the service method', async function(assert) {
    assert.expect(1);
    const deepLink = this.owner.lookup('service:deepLink');
    const originFn = deepLink.transition;

    deepLink.transition = () => {
      assert.ok(true);
      deepLink.transition = originFn;
    };
    this.set('widget', {
      name: 'Top Risky Hosts',
      leadType: 'Hosts', // Need this for master API
      deepLink: { location: 'HOST_LIST' },
      content: [
        {
          type: 'chart',
          chartType: 'donut-chart',
          aggregate: {
            columns: ['osType'],
            type: 'COUNT'
          }
        },
        {
          type: 'table',
          columns: ['hostName', 'score', 'hostOsType'],
          size: 25,
          sort: {
            keys: ['score'],
            descending: true
          }
        }
      ]
    });
    this.set('data', {
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

    this.set('fetchData', () => {
      return new Promise((resolve) => setTimeout(() => resolve({
        data: {
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
        }
      }), 1));
    });


    await render(hbs`<CustomWidget @widget={{this.widget}} @fetchData={{this.fetchData}}/>`);
    await waitUntil(() => findAll(SELECTORS.loadingIndicator).length === 0);
    await click(SELECTORS.viewAllHeader);

  });
});
