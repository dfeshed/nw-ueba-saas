import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hasElement from 'investigate-users/helpers/has-element';

module('Integration | Helper | has-element', function(hooks) {
  setupRenderingTest(hooks);

  test('it should return true if element is present in array', async function(assert) {
    assert.equal(hasElement.compute([['10/2/2019', '10/2/2019'], '10/2/2019']), true);
  });

  test('it should return false if element is present in array', async function(assert) {
    assert.equal(hasElement.compute([['10/2/2019', '10/2/2019'], '10/2/2018']), false);
  });
});
