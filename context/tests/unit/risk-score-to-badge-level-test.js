import { riskScoreToBadgeLevel } from 'context/helpers/risk-score-to-badge-level';
import { module, test } from 'qunit';

module('Unit | Helper | context/risk score to badge level');

// Replace this with your real tests.
test('it works', function(assert) {
  const result = riskScoreToBadgeLevel(42);
  assert.ok(result);
});
