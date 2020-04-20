import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

import { isColumnGroupInProfile } from 'investigate-events/helpers/is-column-group-in-profile';

module('Unit | Helpers | is-column-group-in-profile', function(hooks) {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('it works', function(assert) {
    const columnGroup = { id: 1 };

    let profiles = [{
      name: 'foo',
      columnGroup: {
        id: 1
      }
    }];

    let result = isColumnGroupInProfile([ columnGroup, profiles ]);
    assert.ok(result.disableDelete === true, 'delete should be disabled');
    assert.ok(result.reason.indexOf('foo') > 0, 'name of pofile in reason text');

    profiles = [{
      name: 'foo',
      columnGroup: {
        id: 1
      }
    }, {
      name: 'bar',
      columnGroup: {
        id: 1
      }
    }];

    result = isColumnGroupInProfile([ columnGroup, profiles ]);

    assert.ok(result.disableDelete === true, 'delete should be disabled');
    assert.ok(result.reason.indexOf('foo, bar') > 0, 'name of pofile in reason text');

    profiles = [{
      name: 'foo',
      columnGroup: {
        id: 2
      }
    }, {
      name: 'bar',
      columnGroup: {
        id: 3
      }
    }];

    result = isColumnGroupInProfile([ columnGroup, profiles ]);

    assert.ok(result.disableDelete === false, 'delete should not be disabled');
    assert.ok(result.reason === '', 'reason text empty');

  });

});