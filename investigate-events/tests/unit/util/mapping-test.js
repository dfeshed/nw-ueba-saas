import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import EventColumnGroups from '../../data/subscriptions/column-group';
import { mapColumnGroupsForEventTable } from 'investigate-events/util/mapping';

module('Unit | Util | Mapping', function(hooks) {
  setupTest(hooks);

  test('returns correct columnGroups after mapping', function(assert) {

    assert.ok(EventColumnGroups[5].hasOwnProperty('contentType'));
    assert.ok(EventColumnGroups[5].columns[0].hasOwnProperty('metaName'));
    assert.ok(EventColumnGroups[5].columns[0].hasOwnProperty('displayName'));

    assert.equal(EventColumnGroups[5].columns[4].width, 100, 'custom.metasummary column has a standard width');

    const result = mapColumnGroupsForEventTable(EventColumnGroups);

    assert.notOk(result[5].hasOwnProperty('contentType'));
    assert.notOk(result[5].columns[0].hasOwnProperty('metaName'));
    assert.notOk(result[5].columns[0].hasOwnProperty('displayName'));

    assert.ok(result[5].hasOwnProperty('isEditable'), 'contentType mapped to isEditable');
    assert.ok(result[5].columns[0].hasOwnProperty('field'), 'metaName mapped to field');
    assert.ok(result[5].columns[0].hasOwnProperty('title'), 'displayName mapped to field');

    assert.equal(result[5].columns[4].width, 2000, 'width is assigned to custom.metasummary column');
  });
});
