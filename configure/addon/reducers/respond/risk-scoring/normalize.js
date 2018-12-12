export const normalizeRiskScoringSettings = (data) => {
  return data && data.reduce((result, { type, threshold, timeWindow }) => {
    const [ , timeWindowValue, timeWindowUnit ] = timeWindow && timeWindow.match && timeWindow.match(/(\d+)(.*)/) || ['', '', ''];
    const thresholdValue = threshold ? `${threshold}` : '';
    const typeLower = type && type.toLowerCase();
    result[typeLower] = {
      threshold: thresholdValue,
      timeWindow: timeWindowValue,
      timeWindowUnit
    };
    return result;
  }, {}) || {};
};

export const denormalizeRiskScoringSettings = (data) => {
  return data && Object.keys(data).map((type) => {
    const thresholdValue = data[type].threshold;
    const threshold = thresholdValue && parseInt(thresholdValue, 10) || 0;
    const value = data[type].timeWindow;
    const unit = data[type].timeWindowUnit;
    const timeWindow = value && unit && `${value}${unit}` || '';
    const typeUpper = type && type.toUpperCase();
    return {
      'type': typeUpper,
      threshold,
      timeWindow
    };
  }) || [];
};
