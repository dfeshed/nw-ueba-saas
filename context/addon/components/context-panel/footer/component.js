import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';
import { getTimeWindow } from 'context/util/context-data-modifier';


const stateToComputed = ({ context }) => ({
  lookupData: context.lookupData,
  activeTabName: context.activeTabName
});
const footerExcludedTabs = ['LiveConnect-Ip', 'LiveConnect-Domain', 'LiveConnect-File'];
const footerIncludedTabs = ['Incidents', 'Alerts', 'Users'];
const FooterComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__footer',

  @computed('contextData', 'lookupData.[]', 'activeTabName')
  dsData(contextData, [lookupData], activeTabName) {
    if (!lookupData) {
      return;
    }
    if (contextData) {
      return contextData;
    }
    return lookupData[activeTabName === 'Endpoint' ? 'Machines' : activeTabName];
  },

  @computed('dsData')
  footerTimeStamp(dsData) {
    return getTimeWindow(dsData, this.get('i18n'));
  },

  @computed('dsData', 'activeTabName')
  dSResultCount(dsData, activeTabName) {
    if (footerExcludedTabs.includes(activeTabName)) {
      return '';
    } else {
      const count = dsData && dsData.resultList ? dsData.resultList.length : 0;
      const dataSource = `${count } ${ this.get('i18n').t(`context.footer.title.${activeTabName.camelize()}`)}`;
      if (footerIncludedTabs.includes(activeTabName)) {
        if (dsData && dsData.resultMeta) {
          return `${dataSource } ${ this.get('i18n').t('context.footer.resultCount', { count: dsData.resultMeta.limit })}`;
        }
      }
      return dataSource;
    }
  }
});
export default connect(stateToComputed)(FooterComponent);
