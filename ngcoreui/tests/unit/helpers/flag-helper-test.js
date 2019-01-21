import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import { isFlag, FLAGS } from 'ngcoreui/services/transport/flag-helper';

module('Unit | Helpers | flagHelper', (hooks) => {

  setupTest(hooks);

  test('identifies a config node', (assert) => {
    assert.strictEqual(isFlag(914793674309632, FLAGS.CONFIG_NODE), true);
  });

  test('identifies a stat node', (assert) => {
    assert.strictEqual(isFlag(598134325510144, FLAGS.STAT_NODE), true);
  });

  test('identifies a folder node', (assert) => {
    assert.strictEqual(isFlag(1706442046308352, FLAGS.FOLDER_NODE), true);
  });

});
