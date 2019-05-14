import { module, test } from 'qunit';

import {
  truncateText
} from 'investigate-process-analysis/components/process-tree/util/data';

module('Unit | Util | process-tree data', function() {

  test('long text is truncated', function(assert) {
    const result = truncateText('a123456789012345678902123456');
    assert.equal(result, 'a12345678901234567...');
  });

  test('small text are not truncated', function(assert) {
    const result = truncateText('a12345678901234');
    assert.equal(result, 'a12345678901234');
  });
});
