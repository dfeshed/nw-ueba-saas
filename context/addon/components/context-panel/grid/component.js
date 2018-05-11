import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Component from '@ember/component';
import { getData, getOrder } from 'context/util/context-data-modifier';

const stateToComputed = ({ context: { context } }) => ({
  lookupData: context.lookupData

});

const GridComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__config-grid',

  @computed('lookupData.[]', 'dataSourceDetails')
  dataSourceData([lookupData], dataSourceDetails) {
    const dsData = getData(lookupData, dataSourceDetails);
    const orderDetails = getOrder(lookupData, dataSourceDetails);
    const orderedArray = [];
    if (dsData) {
      dsData.forEach((data) => {
        orderDetails.forEach((order) => {
          const orderedData = {};
          orderedData[order] = data[order];
          orderedArray.push({ ...orderedData });
        });
      });
      return orderedArray;
    }
  }
});
export default connect(stateToComputed)(GridComponent);
