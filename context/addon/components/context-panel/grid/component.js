import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Component from '@ember/component';
import { getData, getOrder, getWarningInfo } from 'context/util/context-data-modifier';
import { inject as service } from '@ember/service';

const stateToComputed = ({ context: { context } }) => ({
  lookupData: context.lookupData
});

const GridComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__config-grid',
  flashMessages: service(),
  i18n: service(),
  isCalledOnce: false,

  @computed('lookupData.[]', 'dataSourceDetails')
  dataSourceData([lookupData], dataSourceDetails) {
    const dsData = getData(lookupData, dataSourceDetails);
    const orderDetails = getOrder(lookupData, dataSourceDetails);
    const orderedArray = [];
    const warningData = getWarningInfo(lookupData, dataSourceDetails);
    if (warningData && !this.get('isCalledOnce')) {
      this.set('isCalledOnce', true);
      const warningMessage = `context.error.archer.${warningData.type}`;
      const flashMessage = warningData.data + this.get('i18n').t(warningMessage);
      this.get('flashMessages').warning(flashMessage);
    }

    if (dsData && orderDetails) {
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
