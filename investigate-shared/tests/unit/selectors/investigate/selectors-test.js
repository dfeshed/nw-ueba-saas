import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';

import {
  timeRange
} from 'investigate-shared/selectors/investigate/selectors';

module('Unit | Selectors | investigate', function(hooks) {

  setupTest(hooks);

  test('Returns timeRange value', function(assert) {
    const state = Immutable.from({
      investigate: {
        timeRange: {
          value: 1,
          unit: 'days'
        }
      }
    });
    const data = timeRange(state);
    assert.equal(data.unit, 'days');
    assert.equal(data.value, 1);
  });

});
