import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Component from '@ember/component';
import { getErrorMessage } from 'context/util/context-data-modifier';


const stateToComputed = ({ context }) => ({
  dataSources: context.dataSources,
  activeTabName: context.activeTabName,
  lookupData: context.lookupData
});
const liveConnectDsGroups = ['LiveConnect-Ip', 'LiveConnect-Domain', 'LiveConnect-File'];
const DSHeaderComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__data__header',

  @computed('contextData', 'lookupData.[]', 'dSDetails')
  dsData(contextData, [lookupData], dSDetails) {
    if (!lookupData) {
      return;
    }
    const data = contextData && (contextData.liveConnectData || contextData.resultList);
    if (data) {
      return contextData;
    }
    return lookupData[dSDetails.dataSourceGroup];
  },

  @computed('dsData')
  errorMessage(dsData) {
    if (dsData && dsData.liveConnectData) {
      return '';
    }
    return getErrorMessage(dsData, this.get('i18n'));
  },
  @computed('dSDetails')
  dsTypeMarketing({ dataSourceGroup }) {
    return this.get('i18n').t(`context.marketingDSType.${dataSourceGroup}`);

  },
  @computed('dataSources', 'activeTabName', 'dSDetails')
  isConfigured(dataSources, activeTabName, { dataSourceGroup }) {
    if (!dataSources) {
      return true;
    }
    const dataSource = dataSources.find((dataSource) => dataSource.dataSourceType.indexOf(activeTabName) === 0);
    return (dataSource.dataSourceType === 'Endpoint' ? dataSource.details[dataSourceGroup] : dataSource).isConfigured;
  },

  @computed('activeTabName')
  showLcMarketingText: (activeTabName) => liveConnectDsGroups.includes(activeTabName)
});
export default connect(stateToComputed)(DSHeaderComponent);
