import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
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

  @computed('contextData', 'lookupData.[]', 'dataSourceDetails')
  dsData(contextData, [lookupData], dataSourceDetails) {
    if (!lookupData) {
      return;
    }
    const data = contextData && (contextData.liveConnectData || contextData.resultList);
    if (data) {
      return contextData;
    }
    return lookupData[dataSourceDetails.dataSourceGroup];
  },

  @computed('dsData')
  errorMessage(dsData) {
    if (dsData && dsData.liveConnectData) {
      return '';
    }
    return getErrorMessage(dsData, this.get('i18n'));
  },
  @computed('dataSourceDetails')
  dsTypeMarketing({ dataSourceGroup }) {
    if (dataSourceGroup === FILE_REPUTATION_SERVER) {
      return this.get('i18n').t('context.fileReputationMarketingText');
    }
    const dataSourceName = this.get('i18n').t(`context.marketingDSType.${dataSourceGroup}`);
    const marketingText = this.get('i18n').t('context.marketingText');
    return dataSourceName + marketingText;
  },

  @computed('dataSources', 'activeTabName', 'dataSourceDetails')
  isConfigured: {
    get() {
      const dataSources = this.get('datasources');
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
  }
});
export default connect(stateToComputed)(DSHeaderComponent);
