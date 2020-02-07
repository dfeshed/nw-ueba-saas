import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';

const SELECTORS = {
  leadsTable: '.table-widget .rsa-data-table',
  leadsTableRow: '.table-widget .rsa-data-table-body-row',
  leadsTableHeaderCell: '.table-widget .rsa-data-table-header-cell'
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
});
