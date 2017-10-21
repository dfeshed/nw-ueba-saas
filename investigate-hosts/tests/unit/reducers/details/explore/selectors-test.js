import { module, test } from 'qunit';
import { exploreData } from '../../../state/state';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | explore');

import {
  enahancedSearchResult
} from 'investigate-hosts/reducers/details/explore/selectors';

test('enahancedSearchResult', function(assert) {
  const result = enahancedSearchResult(Immutable.from({ endpoint: { explore: { fileSearchResults: exploreData } } }));
  assert.equal(result.length, 5);
  assert.equal(result[0].title, '2017-10-05 23:24:34 (2)');
});
