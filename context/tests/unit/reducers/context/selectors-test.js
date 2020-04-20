import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import { getLookupData } from 'context/reducers/context/selectors';
import lookupData from '../../../data/subscriptions/context/stream/data/ip';

module('Unit | Selectors | Context');

const state = Immutable.from({
  meta: 'ip',
  lookupKey: '10.10.10.10',
  errorMessage: null,
  lookupData,
  isClicked: true
});

test('get Lookup schema', function(assert) {
  const lookupData = getLookupData(state);
  assert.deepEqual(lookupData, lookupData);
});
