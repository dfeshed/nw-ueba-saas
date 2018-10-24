import { module, test } from 'qunit';
import { fileSummary } from 'investigate-files/reducers/file-detail/selectors';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';

module('Unit | selectors | file-detail', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('fileSummary', function(assert) {
    const state = Immutable.from({
      files: {
        fileList: {
          selectedDetailFile: {
            score: 100,
            size: 14000,
            fileStatus: 'Neutral',
            machineOsType: 'windows',
            machineCount: 10,
            signature: {
              features: ['valid', 'microsoft', 'signed']
            },
            firstFileName: 'dtf.exe'
          }
        }
      }
    });
    const result = fileSummary(state);
    assert.equal(result.filename, 'dtf.exe', 'Filename is in summary');
    assert.equal(result.signature, 'valid,microsoft,signed', 'Signature is correct');
  });

  test('summary without signature (linux)', function(assert) {
    const state = Immutable.from({
      files: {
        fileList: {
          selectedDetailFile: {
            firstFileName: 'dtf.exe'
          }
        }
      }
    });
    const result = fileSummary(state);
    assert.equal(result.signature, '', 'Signature is empty');
  });
});