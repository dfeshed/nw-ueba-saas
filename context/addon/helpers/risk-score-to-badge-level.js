import { contextRiskScoreThreshold } from 'context/config/constants';
import { helper } from 'ember-helper';

export function riskScoreToBadgeLevel(params) {
  const riskScore = params;
  if (riskScore < contextRiskScoreThreshold.LOW) {
    return 'low';
  } else if (riskScore < contextRiskScoreThreshold.MEDIUM) {
    return 'medium';
  } else if (riskScore < contextRiskScoreThreshold.HIGH) {
    return 'high';
  } else {
    return 'danger';
  }
}

export default helper(riskScoreToBadgeLevel);
