import { module, test } from 'qunit';

import { base64ToUnicode } from 'investigate-shared/utils/file-analysis-base64decoder';

module('Unit | Utils | base64 decoder util', function() {

  test('renders base64 data set in unicode', async function(assert) {
    const encodedData = 'ZGFua29nYWk=5bCP6aO85by+4pyTIMOgIGxhIG1vZGUK';
    const result = base64ToUnicode(encodedData);

    assert.equal(result, 'dankogai小飼弾✓ à la mode', 'base64 string converted into unicode');
  });

});
