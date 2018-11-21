import { module, test } from 'qunit';
import { relevantOperators } from 'investigate-events/util/possible-operators';

module('Unit | Util | possible-operators');

// indexed by value and format is not text
test('Dispays expensive operators', function(assert) {
  const meta = { format: 'IPv4', metaName: 'alias.ip', count: 4, flags: -2147482621, displayName: 'IP Aliases', indexedBy: 'value', expensiveCount: 0 };
  const options = relevantOperators(meta);
  assert.equal(options.length, 4, 'Correct number of operator options');
  assert.notOk(options.findBy('displayName', '=').isExpensive, 'Expected = to not be expensive');
  assert.notOk(options.findBy('displayName', '!=').isExpensive, 'Expected != to not be expensive');
  assert.notOk(options.findBy('displayName', 'exists').isExpensive, 'Expected exists to not be expensive');
  assert.notOk(options.findBy('displayName', '!exists').isExpensive, 'Expected !exists to not be expensive');
});

// indexed by value and format is text
test('Dispays expensive operators- indexed by value and format is text', function(assert) {
  const meta = { format: 'Text', metaName: 'alert', count: 7, flags: -2147483133, displayName: 'Alerts', indexedBy: 'value', expensiveCount: 2 };
  const options = relevantOperators(meta);
  assert.equal(options.length, 7, 'Correct number of operator options');
  assert.ok(options.findBy('displayName', 'contains').isExpensive, 'Expected contains to be expensive');
  assert.ok(options.findBy('displayName', 'ends').isExpensive, 'Expected ends to be expensive');
  assert.notOk(options.findBy('displayName', '=').isExpensive, 'Expected = to not be expensive');
  assert.notOk(options.findBy('displayName', '!=').isExpensive, 'Expected != to not be expensive');
  assert.notOk(options.findBy('displayName', 'begins').isExpensive, 'Expected begins to not be expensive');
  assert.notOk(options.findBy('displayName', 'exists').isExpensive, 'Expected exists to not be expensive');
  assert.notOk(options.findBy('displayName', '!exists').isExpensive, 'Expected !exists to not be expensive');
});

// indexed by key and format is text
test('Dispays expensive operators - indexed by key and format is text', function(assert) {
  const meta = { format: 'Text', metaName: 'referer', count: 7, flags: -2147482878, displayName: 'Referer', indexedBy: 'key', expensiveCount: 5 };
  const options = relevantOperators(meta);
  assert.equal(options.length, 7, 'Correct number of operator options');
  assert.ok(options.findBy('displayName', 'contains').isExpensive, 'Expected contains to be expensive');
  assert.ok(options.findBy('displayName', 'ends').isExpensive, 'Expected ends to be expensive');
  assert.ok(options.findBy('displayName', '=').isExpensive, 'Expected = to be expensive');
  assert.ok(options.findBy('displayName', '!=').isExpensive, 'Expected != to be expensive');
  assert.ok(options.findBy('displayName', 'begins').isExpensive, 'Expected begins to be expensive');
  assert.notOk(options.findBy('displayName', 'exists').isExpensive, 'Expected exists to not be expensive');
  assert.notOk(options.findBy('displayName', '!exists').isExpensive, 'Expected !exists to not be expensive');
});

// indexed by key and format is not text
test('Dispays expensive operators - indexed by key and format is not text', function(assert) {
  const meta = { format: 'UInt64', metaName: 'filename.size', count: 4, flags: -2147482878, displayName: 'File Size', indexedBy: 'key', expensiveCount: 2 };
  const options = relevantOperators(meta);
  assert.equal(options.length, 4, 'Correct number of operator options');
  assert.ok(options.findBy('displayName', '=').isExpensive, 'Expected = to be expensive');
  assert.ok(options.findBy('displayName', '!=').isExpensive, 'Expected != to be expensive');
  assert.notOk(options.findBy('displayName', 'exists').isExpensive, 'Expected exists to not be expensive');
  assert.notOk(options.findBy('displayName', '!exists').isExpensive, 'Expected !exists to not be expensive');
});

// special case - sessionid
test('Dispays expensive operators - special case - sessionid', function(assert) {
  const meta = { format: 'UInt64', metaName: 'sessionid', count: 4, flags: -2147483631, displayName: 'Session ID', indexedBy: 'none', expensiveCount: 0 };
  const options = relevantOperators(meta);
  assert.equal(options.length, 4, 'Correct number of operator options');
  assert.notOk(options.findBy('displayName', '=').isExpensive, 'Expected = to not be expensive');
  assert.notOk(options.findBy('displayName', '!=').isExpensive, 'Expected != to not be expensive');
  assert.notOk(options.findBy('displayName', 'exists').isExpensive, 'Expected exists to not be expensive');
  assert.notOk(options.findBy('displayName', '!exists').isExpensive, 'Expected !exists to not be expensive');
});