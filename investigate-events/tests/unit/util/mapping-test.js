import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import EventColumnGroups from '../../data/subscriptions/column-group/findAll/data';
import { mapColumnGroupsForEventTable } from 'investigate-events/util/mapping';

module('Unit | Util | Mapping', function(hooks) {
  setupTest(hooks);

  test('returns correct columnGroups after mapping', function(assert) {

    assert.ok(EventColumnGroups[4].hasOwnProperty('contentType'));
    assert.ok(EventColumnGroups[4].columns[0].hasOwnProperty('metaName'));
    assert.ok(EventColumnGroups[4].columns[0].hasOwnProperty('displayName'));

    const result = mapColumnGroupsForEventTable(EventColumnGroups);

    assert.notOk(result[4].hasOwnProperty('contentType'));
    assert.notOk(result[4].columns[0].hasOwnProperty('metaName'));
    assert.notOk(result[4].columns[0].hasOwnProperty('displayName'));

    assert.ok(result[4].hasOwnProperty('isEditable'), 'contentType mapped to isEditable');
    assert.ok(result[4].columns[0].hasOwnProperty('field'), 'metaName mapped to field');
    assert.ok(result[4].columns[0].hasOwnProperty('title'), 'displayName mapped to field');
  });
});
