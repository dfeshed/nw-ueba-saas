import layout from './template';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';
import * as ContextActions from 'context/actions/context-creators';
import { riskScoreToBadgeLevel } from 'context/helpers/risk-score-to-badge-level';
import * as DataUtil from 'context/util/context-data-modifier';


const stateToComputed = ({ context }) => ({
  activeTabName: context.activeTabName,
  meta: context.meta,
  lookupData: context.lookupData
});

const dispatchToActions = (dispatch) => ({
  activate: (tabName) => dispatch(ContextActions.updateActiveTab(tabName))
});

const DynamicGridComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__grid',

  @computed('lookupData.[]', 'activeTabName', 'dSDetails')
  getDataSourceData: ([lookupData], activeTabName, dSDetails) => DataUtil.getData(lookupData, dSDetails, activeTabName),

  @computed('contextData', 'lookupData.[]', 'dSDetails')
  needToDisplay: (contextData, [lookupData], dSDetails) => DataUtil.needToDisplay(contextData, lookupData, dSDetails),

  @computed('activeTabName', 'meta')
  showViewAll: (activeTabName, meta) => activeTabName === 'overview' && meta === 'USER',

  @computed('data.IIOCScore', 'dSDetails')
  badgeLevel: (score, details) => {
    return riskScoreToBadgeLevel([score, details.dataSourceGroup]).badgeLevel;
  }
});
export default connect(stateToComputed, dispatchToActions)(DynamicGridComponent);
