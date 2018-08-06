import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import {
  filters,
  listWithoutDefault
} from 'investigate-files/reducers/file-filter/selectors';

const STATE = Immutable.from({
  files: {
    filter: {
      expressionList: [
        {
          propertyName: 'firstFileName',
          propertyValues: [
            {
              value: 'windows'
            }
          ],
          restrictionType: 'IN'
        },
        {
          restrictionType: 'IN',
          propertyName: 'format',
          propertyValues: []
        }
      ]
    }
  }
});


module('Unit | Selectors | investigate-files | file-filter', function(hooks) {

  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('filters', function(assert) {
    const result = filters(STATE);
    assert.equal(result.length, 7, 'filters result should be 2');
  });

  test('listWithoutDefault', function(assert) {
    const result = listWithoutDefault(STATE);
    assert.equal(result.length, 7, ' should be 2 as there are no defaults');
  });

});
