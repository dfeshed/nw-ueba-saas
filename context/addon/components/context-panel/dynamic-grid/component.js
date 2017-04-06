import layout from './template';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';
import * as ContextActions from 'context/actions/context-creators';
import { riskScoreToBadgeLevel } from 'context/helpers/risk-score-to-badge-level';


const stateToComputed = ({ context }) => ({
  activeTabName: context.activeTabName
});

const dispatchToActions = (dispatch) => ({
  activate: (tabName) => dispatch(ContextActions.updateActiveTab(tabName))
});

const DynamicGridComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__grid',

  @computed('contextData', 'activeTabName')
  getDataSourceData: (contextData, activeTabName) => {
    return (activeTabName === 'overview') ? contextData.data.slice(0, 5) : contextData.data;
  },

  actions: {
    activate(option) {
      this.send('activatePanel', option);
    }
  },

  @computed('data.IIOCScore', 'dSDetails')
  badgeLevel: (score, details) => {
    return riskScoreToBadgeLevel([score, details.dataSourceGroup]).badgeLevel;
  }
});
export default connect(stateToComputed, dispatchToActions)(DynamicGridComponent);
