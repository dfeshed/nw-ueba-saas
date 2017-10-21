import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';

import {
  hasScanTime
} from 'investigate-hosts/reducers/details/selectors';

module('Unit | selectors | details');

test('areSomeScanning', function(assert) {
  const result = hasScanTime(Immutable.from({ endpoint: { detailsInput: { snapShots: [11231231, 12312311] } } }));
  assert.equal(result, true, 'should return true as some snapshots are available');
});
