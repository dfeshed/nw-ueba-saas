import { module, test } from 'qunit';

import {
  enrichedPillsData,
  selectedPills,
  canQueryNextGen,
  freeFormText
} from 'investigate-events/reducers/investigate/next-gen/selectors';
import ReduxDataHelper from '../../../helpers/redux-data-helper';

module('Unit | Selectors | next-gen');

test('enrichedPillsData is false when status is not error', function(assert) {
  const state = new ReduxDataHelper().language().pillsDataPopulated().build();
  const pD = enrichedPillsData(state);
  assert.equal(pD.length, 2, 'returns correct number of pill data');
  assert.equal(pD[0].meta.metaName, 'a', 'transforms meta correctly');
  assert.equal(pD[0].operator.displayName, '=', 'transforms operator correctly');
  assert.equal(pD[0].value, '\'x\'', 'transforms value correctly');
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
  assert.equal(pD[0].value, '\'x\'', 'transforms value correctly');
});

test('canQueryNextGen is true when a service, summary, time-range, isDirty and NO invalid pill is present', function(assert) {
  const state = new ReduxDataHelper()
    .language()
    .pillsDataPopulated()
    .hasRequiredValuesToQuery(true)
    .build();

  const canQuery = canQueryNextGen(state);
  assert.ok(canQuery, 'Selector returns true if service, summary, time-range, isDirty and NO invalid pill is present');
});

test('canQueryNextGen is false when a service, summary, time-range, isDirty exists but an invalid pill is present', function(assert) {
  const state = new ReduxDataHelper()
    .language()
    .pillsDataPopulated()
    .hasRequiredValuesToQuery(true)
    .markInvalid(['1'])
    .build();

  const canQuery = canQueryNextGen(state);
  assert.notOk(canQuery, 'Selector returns false if service, summary, time-range, isDirty exists but an invalid pill is present');
});

test('freeFormText is set properly', function(assert) {
  const state = new ReduxDataHelper()
    .language()
    .pillsDataPopulated()
    .build();

  const text = freeFormText(state);
  assert.equal(text, 'a = \'x\' && b = \'y\'', 'freeFormText is set properly');
});
