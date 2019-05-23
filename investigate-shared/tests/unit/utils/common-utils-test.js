import { module, test } from 'qunit';

import { convertPixelToVW } from 'investigate-shared/utils/common-util';

module('Unit | Utils | common utils', function() {

  test('converts the number into VW', function(assert) {
    const result = convertPixelToVW(100);
    assert.equal(result, '6vw');
  });

});
