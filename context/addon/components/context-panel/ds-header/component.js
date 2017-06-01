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
      return contextData;
    }
    return lookupData[dSDetails.dataSourceGroup];
  },

  @computed('dsData')
  errorMessage(dsData) {
    return getErrorMessage(dsData, this.get('i18n'));
  },
  @computed('dataSources', 'dSDetails')
  isConfigured(dataSources, { dataSourceGroup }) {
    if (!dataSources) {
      return true;
    }
    return dataSources.find((dataSource) => {
      if (['Machines', 'Modules', 'IOC'].includes(dataSourceGroup)) {
        return dataSource.details[dataSourceGroup];
      } else if (dataSource.dataSourceType.indexOf(dataSourceGroup) === 0) {
        return dataSource;
      }
    }).isConfigured;
  },
  @computed('activeTabName')
  dataSourceName(activeTabName) {
    return this.get('i18n').t(`context.header.title.${activeTabName.camelize()}`);
  }
});
export default connect(stateToComputed)(DSHeaderComponent);