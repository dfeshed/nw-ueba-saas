import { module, test } from 'qunit';
import { indicatorId } from 'entity-details/reducers/indicators/selectors';

module('Unit | Selector | Indicators Selector');

const state = {
  indicators: {
    indicatorId: 'inc-1'
  }
};

test('test indicator state', function(assert) {
  assert.equal(indicatorId(state), 'inc-1');
});