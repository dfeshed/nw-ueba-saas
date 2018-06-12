import layout from './template';
import computed from 'ember-computed-decorators';
import Component from '@ember/component';

const getTimeWindow = (dsData, i18n) => {
  if (dsData && dsData.resultMeta && dsData.resultMeta.timeQuerySubmitted) {
    let timeWindow = i18n.t('investigateShared.endpoint.riskPanel.timeUnit.allData');
    const timeCount = dsData.resultMeta['timeFilter.timeUnitCount'];
    if (timeCount) {
      let timeUnitString = dsData.resultMeta['timeFilter.timeUnit'];
      const timeUnit = timeCount > 1 ? `${timeUnitString}S` : `${timeUnitString}`;
      timeUnitString = i18n.t(`investigateShared.endpoint.riskPanel.timeUnit.${timeUnit}`);
      timeWindow = `${timeCount} ${timeUnitString}`;
    }
    return {
      lastUpdated: dsData.resultMeta.timeQuerySubmitted,
      timeWindow
    };
  }
};

export default Component.extend({
  layout,
  classNames: 'risk-properties-panel__header',

  @computed('data')
  headerTimeStamp(data) {
    return getTimeWindow(data, this.get('i18n'));
  }
});