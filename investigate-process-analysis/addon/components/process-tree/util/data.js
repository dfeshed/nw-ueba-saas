const MAX_TEXT_LENGTH = 20;

const RISK_SCORE = {
  LOW: 20,
  MEDIUM: 50,
  HIGH: 90
};
/**
 * Returns class based on the process risk score
 * @param score
 * @returns {string}
 * @public
 */
export const getRiskScoreClassName = function(score) {
  if (score <= RISK_SCORE.LOW) {
    return 'is-low';
  } else if (score <= RISK_SCORE.MEDIUM) {
    return 'is-medium';
  } else if (score <= RISK_SCORE.HIGH) {
    return 'is-high';
  } else {
    return 'is-danger';
  }
};
/**
 * Truncate the process name
 * @param text
 * @returns {*}
 * @public
 */
export const truncateText = function(text) {
  const len = (text || '').length;
  if (len <= MAX_TEXT_LENGTH) {
    return text;
  } else {
    return `${text.substr(0, MAX_TEXT_LENGTH)}...`;
  }
};
