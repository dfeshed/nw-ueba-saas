import layout from './template';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';
import * as ContextActions from 'context/actions/context-creators';
import * as DataUtil from 'context/util/context-data-modifier';

const stateToComputed = ({ context }) => ({
  activeTabName: context.activeTabName,
  lookupData: context.lookupData
});

const dispatchToActions = (dispatch) => ({
  activate: (tabName) => dispatch(ContextActions.updateActiveTab(tabName))
});

const DataTableComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__context-data-table',

  actions: {
    viewAll(option) {
      this.send('activate', option);
    }
  },

  @computed('contextData', 'lookupData.[]', 'activeTabName', 'dSDetails')
  getDataSourceData(contextData, [lookupData], activeTabName, dSDetails) {
    if (contextData) {
      return contextData.data;
    }
    return DataUtil.getData(lookupData, dSDetails, activeTabName);
  },

  @computed('contextData', 'lookupData.[]', 'dSDetails')
  needToDisplay: (contextData, [lookupData], dSDetails) => DataUtil.needToDisplay(contextData, lookupData, dSDetails)
});
export default connect(stateToComputed, dispatchToActions)(DataTableComponent);
