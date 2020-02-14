import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, click, settled } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';

const SELECTORS = {
  leadsTable: '.table-widget .rsa-data-table',
  leadsTableRow: '.table-widget .rsa-data-table-body-row',
  leadsTableHeaderCell: '.table-widget .rsa-data-table-header-cell',
  sortButtons: '.sort-buttons',
  defaultSort: '.is-sorted.desc',
  sortingUp: '.rsa-data-table-header-cell:nth-child(2) .sort-icons .ascending',
  sortingDown: '.rsa-data-table-header-cell:nth-child(2) .sort-icons .descending',
  sortDescending: '.is-sorted.desc',
  sortAscending: '.is-sorted.asc'
};


module('Integration | Component | table-widget', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders table', async function(assert) {
    this.set('config', {
      columns: ['hostName', 'score', 'hostOsType'],
      size: 25,
      sort: {
        keys: ['score'],
        descending: true
      }
    });
    this.set('data', {
      items: [
        {
          hostName: 'Test',
          score: '100',
          osType: 'windows'
        }
      ]
    });
    await render(hbs`<TableWidget @config={{this.config}} @data={{this.data}}/>`);
    assert.dom(SELECTORS.leadsTable).exists('It renders table');
    assert.dom(SELECTORS.leadsTableRow).exists({ count: 1 }, 'It renders one row');
    assert.dom(SELECTORS.leadsTableHeaderCell).exists({ count: 3 }, 'It renders 3 columns');
  });

  test('it should sort the data', async function(assert) {
    this.set('config', {
      columns: ['hostName', 'score', 'hostOsType'],
      size: 25,
      sort: {
        keys: ['score'],
        descending: true
      }
    });
    this.set('data', {
      items: [
        {
          hostName: 'Test1',
          score: '10',
          osType: 'windows'
        },
        {
          hostName: 'Test2',
          score: '100',
          osType: 'windows'
        },
        {
          hostName: 'Test3',
          score: '20',
          osType: 'windows'
        },
        {
          hostName: 'Test4',
          score: '50',
          osType: 'windows'
        }
      ]
    });
    await render(hbs`<TableWidget @config={{this.config}} @data={{this.data}}/>`);
    assert.dom(SELECTORS.leadsTable).exists('It renders sort buttons');
    assert.dom(SELECTORS.defaultSort).containsText('Risk Score');
    await click(SELECTORS.sortingUp);
    await settled();
    assert.dom(SELECTORS.sortAscending).containsText('Risk Score');
    await click(SELECTORS.sortingDown);
    assert.dom(SELECTORS.sortDescending).containsText('Risk Score');
  });
});
