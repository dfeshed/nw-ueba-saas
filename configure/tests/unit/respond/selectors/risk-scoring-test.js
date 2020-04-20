import { module, test } from 'qunit';
import { normalizedState } from '../../../integration/component/respond/risk-scoring/data';
import { getRiskScoringExpanded, getRiskScoringStatus, getRiskScoringSettings } from 'configure/reducers/respond/risk-scoring/selectors';

const { configure: { respond: { riskScoring: { riskScoringSettings } } } } = normalizedState;

module('Unit | Utility | Respond Risk Scoring Selectors');

test('getRiskScoringSettings selector returns the risk scoring settings', function(assert) {
  assert.expect(1);

  assert.deepEqual(getRiskScoringSettings(normalizedState), riskScoringSettings);
});

test('getRiskScoringStatus selector returns the risk scoring status', function(assert) {
  assert.expect(1);

  assert.deepEqual(getRiskScoringStatus(normalizedState), 'completed');
});

test('getRiskScoringExpanded selector returns the risk scoring expanded', function(assert) {
  assert.expect(1);

  assert.deepEqual(getRiskScoringExpanded(normalizedState), false);
});
