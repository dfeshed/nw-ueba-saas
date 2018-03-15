import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Component from '@ember/component';
import { getErrorMessage } from 'context/util/context-data-modifier';
import { onLiveConnectTab } from 'context/reducers/tabs/selectors';


const stateToComputed = ({ context: { context, tabs } }) => ({
  dataSources: tabs.dataSources,
  activeTabName: tabs.activeTabName,
  onLiveConnectTab: onLiveConnectTab(tabs),
  lookupData: context.lookupData
});
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
    return this.get('i18n').t(`context.marketingDSType.${dataSourceGroup}`);

  },
  @computed('dataSources', 'activeTabName', 'dataSourceDetails')
  isConfigured(dataSources, activeTabName, { dataSourceGroup }) {
    if (!dataSources) {
      return true;
    }
    const dataSource = dataSources.find((dataSource) => dataSource.dataSourceType.indexOf(activeTabName) === 0);
    return (dataSource.dataSourceType === 'Endpoint' ? dataSource.details[dataSourceGroup] : dataSource).isConfigured;
  }
});
export default connect(stateToComputed)(DSHeaderComponent);
