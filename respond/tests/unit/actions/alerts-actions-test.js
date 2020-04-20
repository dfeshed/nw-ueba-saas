import { module, test } from 'qunit';
import * as alertCreators from 'respond/actions/creators/alert-creators';
import { setupTest } from 'ember-qunit';

module('Unit | Utility | Alert  Actions - Reducers', function(hooks) {
  setupTest(hooks);

  test('The filterAlertNames function verifies if an applied filter is a valid filter anymore after deleting alerts', async function(assert) {
    assert.expect(3);
    const itemFilters = {
      'alert.name': [
        undefined,
        null,
        'ModuleIOC 1'
      ]
    };

    const validNames = [
      null,
      undefined,
      'foo',
      'ModuleIOC 1',
      'bar'
    ];
    const result = alertCreators.filterAlertNames(validNames, itemFilters);
    assert.deepEqual(result, ['ModuleIOC 1']);
    const nullFilterResult = alertCreators.filterAlertNames(null, itemFilters);
    assert.deepEqual(nullFilterResult, []);
    const emptyResult = alertCreators.filterAlertNames([], itemFilters);
    assert.deepEqual(emptyResult, []);

  });

});
