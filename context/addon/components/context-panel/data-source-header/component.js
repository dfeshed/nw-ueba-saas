import { computed } from '@ember/object';
import layout from './template';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { getErrorMessage } from 'context/utils/context-data-modifier';
import { onLiveConnectTab } from 'context/reducers/tabs/selectors';


const stateToComputed = ({ context: { context, tabs } }) => ({
  dataSources: tabs.dataSources,
  activeTabName: tabs.activeTabName,
  onLiveConnectTab: onLiveConnectTab(tabs),
  lookupData: context.lookupData
});

const FILE_REPUTATION_SERVER = 'FileReputationServer';
const DSHeaderComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__data__header',

  dsData: computed('contextData', 'lookupData.[]', 'dataSourceDetails', function() {
    const [lookupData] = this.lookupData;
    if (!lookupData) {
      return;
    }
    const data = this.contextData && (this.contextData.liveConnectData || this.contextData.resultList);
    if (data) {
      return this.contextData;
    }
    return lookupData[this.dataSourceDetails.dataSourceGroup];
  }),

  errorMessage: computed('dsData', function() {
    if (this.dsData && this.dsData.liveConnectData) {
      return '';
    }
    return getErrorMessage(this.dsData, this.get('i18n'));
  }),

  dsTypeMarketing: computed('dataSourceDetails', function() {
    if (this.dataSourceDetails.dataSourceGroup === FILE_REPUTATION_SERVER) {
      return this.get('i18n').t('context.fileReputationMarketingText');
    }
    const dataSourceName = this.get('i18n').t(`context.marketingDSType.${this.dataSourceDetails.dataSourceGroup}`);
    const marketingText = this.get('i18n').t('context.marketingText');
    return dataSourceName + marketingText;
  }),

  isConfigured: computed('dataSources', 'activeTabName', 'dataSourceDetails', {
    get() {
      const dataSources = this.get('dataSources');
      const activeTabName = this.get('activeTabName');
      const { dataSourceGroup } = this.get('dataSourceDetails');
      if (!dataSources) {
        return true;
      }
      const dataSource = dataSources.find((dataSource) => dataSource.dataSourceType.indexOf(activeTabName) === 0);
      return (dataSource.dataSourceType === 'Endpoint' ? dataSource.details[dataSourceGroup] : dataSource).isConfigured;
    },

    set(key, value) {
      return value;
    }
  })
});
export default connect(stateToComputed)(DSHeaderComponent);
