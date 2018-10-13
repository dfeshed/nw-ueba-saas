import { module, test } from 'qunit';
import { events } from 'investigate-files/reducers/file-detail/selectors';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';

module('Unit | selectors | file-detail', function(hooks) {
  setupTest(hooks);
  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('events', function(assert) {
    const state = Immutable.from({
      files: {
        fileDetail: { eventsData: [{
          sessionId: 1,
          metas: [['action', 'createProcess'],
            ['filename.dst', 'malware.exe'],
            ['sessionid', 1]
          ] }]
        }
      }
    });
    const result = events(state);
    const keys = Object.keys(result[0]);
    assert.equal(result.length, 1, '1 event is present');
    assert.equal(keys[0], 'action', 'action => action');
    assert.equal(keys[1], 'target_filename', 'filename.dst => target_filename');
    assert.equal(keys[2], 'id', 'sessionid => id');
  });
});