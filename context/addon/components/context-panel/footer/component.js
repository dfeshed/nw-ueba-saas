import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Component from '@ember/component';
import { getTimeWindow } from 'context/util/context-data-modifier';


const stateToComputed = ({ context: { context: { lookupData }, tabs: { activeTabName } } }) => ({
  lookupData,
  activeTabName
});
// Need not to show footer for below data sources as there always going to be single infomration.
const footerExcludedTabs = ['LiveConnect-Ip', 'LiveConnect-Domain', 'LiveConnect-File'];
const footerIncludedTabs = ['Incidents', 'Alerts', 'Users'];
const FooterComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__footer',

  @computed('lookupData.[]', 'activeTabName')
  dataSourceData([lookupData], activeTabName) {
    if (!lookupData) {
      return;
    }
    return lookupData[activeTabName === 'Endpoint' ? 'Machines' : activeTabName];
  },

  @computed('dataSourceData')
  footerTimeStamp(dataSourceData) {
    return getTimeWindow(dataSourceData, this.get('i18n'));
  },

  @computed('dataSourceData', 'activeTabName')
  dSResultCount(dataSourceData, activeTabName) {
    if (footerExcludedTabs.includes(activeTabName)) {
      return '';
    } else {
      const count = dataSourceData && dataSourceData.resultList ? dataSourceData.resultList.length : 0;
      const dataSource = `${count } ${ this.get('i18n').t(`context.footer.title.${activeTabName.camelize()}`)}`;
      if (footerIncludedTabs.includes(activeTabName)) {
        if (dataSourceData && dataSourceData.resultMeta) {
          return `${dataSource } ${ this.get('i18n').t('context.footer.resultCount', { count: dataSourceData.resultMeta.limit })}`;
        }
      }
      return dataSource;
    }
  }
});
export default connect(stateToComputed)(FooterComponent);
