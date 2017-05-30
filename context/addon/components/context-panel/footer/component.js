import layout from './template';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';
import * as DataUtil from 'context/util/context-data-modifier';


const stateToComputed = ({ context }) => ({
  lookupData: context.lookupData,
  activeTabName: context.activeTabName
});

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
    return { count: dsData ? dsData.resultList.length : 0, timeWindow: DataUtil.getHeaderData(dsData, this.get('i18n')) };
  }
});
export default connect(stateToComputed)(FooterComponent);
