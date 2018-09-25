import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import * as dictionaryCreators from 'respond/actions/creators/dictionary-creators';

module('Unit | Actions | Dictionary | Creators', function(hooks) {
  setupTest(hooks);

  test('getAllAlertSources returns the complete list of alert sources', async function(assert) {
    assert.expect(1);

    const result = await dictionaryCreators.getAllAlertSources();

    return result.promise.then((sources) => {
      assert.deepEqual(sources, ['ECAT', 'Event Stream Analysis', 'Malware Analysis', 'NetWitness Investigate', 'Reporting Engine', 'User Entity Behavior Analytics', 'Web Threat Detection']);
    });
  });

});
