import layout from './template';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';
import { getTimeWindow } from 'context/util/context-data-modifier';


const stateToComputed = ({ context }) => ({
  lookupData: context.lookupData,
  activeTabName: context.activeTabName
});
const footerExcludedTabs = ['LiveConnect-Ip', 'LiveConnect-Domain', 'LiveConnect-File'];
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
  headerData(dsData) {
    return { count: dsData && dsData.resultList ? dsData.resultList.length : 0, timeWindow: getTimeWindow(dsData, this.get('i18n')) };
  },

  @computed('activeTabName')
  footerDSTitle(activeTabName) {
    return footerExcludedTabs.includes(activeTabName) ? 'none' : this.get('i18n').t(`context.footer.title.${activeTabName.camelize()}`);
  }
});
export default connect(stateToComputed)(FooterComponent);
