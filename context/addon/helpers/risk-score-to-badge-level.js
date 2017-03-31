import { contextRiskScoreThreshold } from 'context/config/constants';
import { helper } from 'ember-helper';

export function riskScoreToBadgeLevel(params) {
  const riskScore = params;
  if (riskScore <= contextRiskScoreThreshold.LOW) {
    return {
      badgeLevel: 'low',
      style: 'rsa-context-panel__risk-badge__low-risk'
    };
  } else if (riskScore <= contextRiskScoreThreshold.MEDIUM) {
    return {
      badgeLevel: 'medium',
      style: 'rsa-context-panel__risk-badge__medium-risk'
    };
  } else if (riskScore <= contextRiskScoreThreshold.HIGH) {
    return {
      badgeLevel: 'high',
      style: 'rsa-context-panel__risk-badge__high-risk'
    };
  } else {
    return {
      badgeLevel: 'danger',
      style: 'rsa-context-panel__risk-badge__danger-risk'
    };
  }
}

export default helper(riskScoreToBadgeLevel);
