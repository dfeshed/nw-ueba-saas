import layout from './template';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';
import { getErrorMessage } from 'context/util/context-data-modifier';


const stateToComputed = ({ context }) => ({
  dataSources: context.dataSources,
  activeTabName: context.activeTabName,
  lookupData: context.lookupData
});

const DSHeaderComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__data__header',

  @computed('contextData', 'lookupData.[]', 'dSDetails')
  dsData(contextData, [lookupData], dSDetails) {
    if (!lookupData) {
      return;
    }
    if (contextData) {
      return (contextData.liveConnectData || contextData.resultList) ? contextData : null;
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
  @computed('dataSources', 'activeTabName', 'dSDetails')
  isConfigured(dataSources, activeTabName, { dataSourceGroup }) {
    if (!dataSources) {
      return true;
    }
    const dataSource = dataSources.find((dataSource) => dataSource.dataSourceType.indexOf(activeTabName) === 0);
    return (dataSource.dataSourceType === 'Endpoint' ? dataSource.details[dataSourceGroup] : dataSource).isConfigured;
  }
});
export default connect(stateToComputed)(DSHeaderComponent);