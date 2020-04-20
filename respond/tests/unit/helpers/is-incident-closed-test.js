import { isIncidentClosed } from 'respond/helpers/is-incident-closed';
import { module, test } from 'qunit';

module('Unit | Helper | is-incident-closed', function() {
  test('it returns true for closed incidents, false for active incidents', function(assert) {
    assert.equal(isIncidentClosed('CLOSED'), true);
    assert.equal(isIncidentClosed('CLOSED_FALSE_POSITIVE'), true);
    assert.equal(isIncidentClosed('NEW'), false);
    assert.equal(isIncidentClosed('CLoSeD'), false);
  });
});

