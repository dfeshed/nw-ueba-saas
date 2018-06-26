import { module, test } from 'qunit';

import {
  enrichedPillsData,
  selectedPills
} from 'investigate-events/reducers/investigate/next-gen/selectors';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

module('Unit | Selectors | next-gen');

test('enrichedPillsData is false when status is not error', function(assert) {
  const state = new ReduxDataHelper().language().pillsDataPopulated().build();
  const pD = enrichedPillsData(state);
  assert.equal(pD.length, 2, 'returns correct number of pill data');
  assert.equal(pD[0].meta.metaName, 'a', 'transforms meta correctly');
  assert.equal(pD[0].operator.displayName, '=', 'transforms operator correctly');
  assert.equal(pD[0].value, 'x', 'transforms value correctly');
});

test('selectedPills returns only those pills that are selected', function(assert) {
  const state = new ReduxDataHelper()
    .language()
    .pillsDataPopulated()
    .makeSelected(['1'])
    .build();
  const pD = selectedPills(state);
  assert.equal(pD.length, 1, 'returns correct number of pill data');
  assert.equal(pD[0].meta, 'a', 'transforms meta correctly');
  assert.equal(pD[0].operator, '=', 'transforms operator correctly');
  assert.equal(pD[0].value, 'x', 'transforms value correctly');
});