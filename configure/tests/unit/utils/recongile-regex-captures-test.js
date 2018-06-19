import { module, test } from 'qunit';
import { hasInvalidCaptures } from 'configure/utils/reconcile-regex-captures';

module('Unit | Utility - Has Invalid Regex Captures', function() {
  test('it is okay that the regex and the capture configuration reflect the same number of groups', function(assert) {
    const regex = '^test$';
    const captures = [{ index: 0 }];
    assert.equal(hasInvalidCaptures(regex, captures), false);
  });

  test('it is okay that the regex has more capture groups than the capture configuration', function(assert) {
    const regex = '^test([ing])$'; // there are two capture groups (full capture and first capture)
    const captures = [{ index: 0 }]; // only full capture (zero) is defined
    assert.equal(hasInvalidCaptures(regex, captures), false);
  });

  test('it is not okay that the regex has fewer capture groups than the capture config', function(assert) {
    const regex = '^test([ing])$'; // only full and first capture group defined
    const captures = [{ index: '2' }]; // but configuration matches second capture group
    assert.equal(hasInvalidCaptures(regex, captures), true);
  });
});
