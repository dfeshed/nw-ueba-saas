import { buildContextOptions } from 'rsa-context-menu/utils/build-context-options';
import data from '../../helpers/actions-data';
import { module, test } from 'qunit';

module('Unit | Utility | build-context-options', function() {

  test('test parse result based on data', function(assert) {
    const result = buildContextOptions(data.data, {
      exists: () => true,
      t: (str) => {
        return str;
      }
    });
    assert.ok(result);
    assert.equal(result.EventGrid['ip.src'].length, 2, 'Should retrun only 2 actions');
    assert.notOk(result.EventGrid.test, 'Should not be having any actions');
  });
});