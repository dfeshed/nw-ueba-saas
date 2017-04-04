import layout from './template';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';
import * as ContextActions from 'context/actions/context-creators';

const stateToComputed = ({ context }) => ({
  activeTabName: context.activeTabName
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
  @computed('contextData', 'activeTabName', 'dSDetails')
  getDataSourceData: (contextData, activeTabName, dSDetails) => {
    if (!contextData) {
      return;
    }
    if (dSDetails.sortColumn) {
      contextData.data.sort((a, b) => (a[dSDetails.sortColumn] - b[dSDetails.sortColumn]));
    }
    return (activeTabName === 'overview') ? contextData.data.slice(0, 5) : contextData.data;
  },

  constructPath(incId, path) {
    path = path.replace('{0}', incId);
    return window.location.origin.concat(path);
  }

});
export default connect(stateToComputed, dispatchToActions)(DataTableComponent);
