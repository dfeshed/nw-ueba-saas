import layout from './template';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';
import { riskScoreToBadgeLevel } from 'context/helpers/risk-score-to-badge-level';
import * as DataUtil from 'context/util/context-data-modifier';


const stateToComputed = ({ context }) => ({
  activeTabName: context.activeTabName,
  meta: context.meta,
  lookupData: context.lookupData
});

const DynamicGridComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__grid',

  @computed('lookupData.[]', 'dSDetails')
  getDataSourceData: ([lookupData], dSDetails) => DataUtil.getData(lookupData, dSDetails),

  @computed('contextData', 'lookupData.[]', 'dSDetails')
  needToDisplay: (contextData, [lookupData], dSDetails) => DataUtil.needToDisplay(contextData, lookupData, dSDetails),

  @computed('data.IIOCScore', 'dSDetails')
  badgeLevel: (score, details) => {
    return riskScoreToBadgeLevel([score, details.dataSourceGroup]).badgeLevel;
  }
});
export default connect(stateToComputed)(DynamicGridComponent);
