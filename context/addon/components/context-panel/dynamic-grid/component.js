import layout from './template';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';
import { riskScoreToBadgeLevel } from 'context/helpers/risk-score-to-badge-level';
import { getData } from 'context/util/context-data-modifier';


const stateToComputed = ({ context }) => ({
  lookupData: context.lookupData,
  dataSources: context.dataSources
});

const DynamicGridComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__grid',

  @computed('lookupData.[]', 'dSDetails')
  getDataSourceData: ([lookupData], dSDetails) => getData(lookupData, dSDetails),

  @computed('data.IIOCScore', 'dSDetails')
  badgeLevel: (score, details) => {
    return riskScoreToBadgeLevel([score, details.dataSourceGroup]).badgeLevel;
  }
});
export default connect(stateToComputed)(DynamicGridComponent);
