import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { patchFlash } from '../../helpers/patch-flash';
import { flashErrorMessage } from 'investigate-users/utils/flash-message';

module('Unit | Utils | flash-message', (hooks) => {
  setupTest(hooks);

  test('it should give flash message', (assert) => {
    const done = assert.async();

    patchFlash((flash) => {
      assert.equal(flash.type, 'error');
      done();
    });
    flashErrorMessage('Test');
  });
});