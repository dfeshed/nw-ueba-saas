import _ from 'lodash';
import { isNone } from '@ember/utils';

const parseEnabled = (value) => [null, undefined].includes(value) || value;

export const normalizeRiskScoringSettings = (data) => {
  return data && data.reduce((result, { type, threshold, timeWindow, enabled }) => {
    const [ , timeWindowValue, timeWindowUnit ] = timeWindow && timeWindow.match && timeWindow.match(/(\d+)(.*)/) || ['', '', ''];
    const thresholdValue = isNone(threshold) ? '' : `${threshold}`;
    const typeLower = type && type.toLowerCase();
    const enabledValue = parseEnabled(enabled);
    result[typeLower] = {
      threshold: thresholdValue,
      timeWindow: timeWindowValue,
      timeWindowUnit,
      enabled: enabledValue
    };
    return result;
  }, {}) || {};
};

export const resetRiskScoringWhenDisabled = (data, riskScoringSettings) => {
  return _.mapValues((data), (setting, type) => {
    return setting.enabled === false ? {
      ...riskScoringSettings[type],
      enabled: false
    } : setting;
  });
};

export const denormalizeRiskScoringSettings = (data) => {
  return data && Object.keys(data).map((type) => {
    const { threshold, timeWindow, timeWindowUnit, enabled } = data[type];
    const thresholdValue = threshold && parseInt(threshold, 10) || 0;
    const timeWindowValue = timeWindow && timeWindowUnit && `${timeWindow}${timeWindowUnit}` || '';
    const typeUpper = type && type.toUpperCase();
    const enabledValue = parseEnabled(enabled);
    return {
      'type': typeUpper,
      threshold: thresholdValue,
      timeWindow: timeWindowValue,
      enabled: enabledValue
    };
  }) || [];
};
