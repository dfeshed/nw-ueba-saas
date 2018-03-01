import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Component from '@ember/component';
import { getData } from 'context/util/context-data-modifier';


const stateToComputed = ({ context }) => ({
  lookupData: context.lookupData,
  dataSources: context.dataSources
});

const DynamicGridComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__grid',

  @computed('lookupData.[]', 'dSDetails')
  getDataSourceData: ([lookupData], dSDetails) => getData(lookupData, dSDetails)

});
export default connect(stateToComputed)(DynamicGridComponent);
