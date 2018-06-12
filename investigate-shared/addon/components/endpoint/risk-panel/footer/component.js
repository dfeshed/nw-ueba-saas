import layout from './template';
import computed from 'ember-computed-decorators';
import Component from '@ember/component';

export default Component.extend({
  layout,
  classNames: 'risk-properties-panel__footer',

  @computed('data', 'activeDataSourceTab')
  dSResultCount(data, activeDataSourceTab) {
    const count = data && data.resultList ? data.resultList.length : 0;
    const dataSource = `${count } ${ this.get('i18n').t(`investigateShared.endpoint.riskPanel.footer.title.${activeDataSourceTab}`)}`;
    if (data && data.resultMeta) {
      return `${dataSource } ${ this.get('i18n').t('investigateShared.endpoint.riskPanel.footer.resultCount', { count: data.resultMeta.limit })}`;
    }
    return dataSource;
  }
});