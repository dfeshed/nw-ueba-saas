import { module, test } from 'qunit';
import { mashUpData } from 'springboard-widget-lib/selectors/data-selector';

module('Unit | Selectors | data selector', function() {

  test('mashUpData will add aggregate data', function(assert) {
    const state = {
      data: {
        items: [
          {
            name: 'test'
          }
        ]
      },
      widget: {
        content: []
      }

    };
    const result = mashUpData(state);
    assert.equal(result.aggregate, undefined, 'No aggregate data');

    const state2 = {
      data: {
        items: [
          {
            name: 'test'
          },
          {
            name: 'test'
          },
          {
            name: 'test1'
          }
        ]
      },
      widget: {
        content: [{
          type: 'chart',
          aggregate: {
            columns: ['name']
          }
        }]
      }

    };
    const result2 = mashUpData(state2);
    assert.equal(result2.aggregate.data.length, 2, 'Has Aggregate data');
  });
});