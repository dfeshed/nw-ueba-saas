import { computed } from '@ember/object';
import layout from './template';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { getData, getOrder, getWarningInfo } from 'context/utils/context-data-modifier';
import { inject as service } from '@ember/service';
import { next } from '@ember/runloop';
import { isEmpty } from '@ember/utils';

const stateToComputed = ({ context: { context } }) => ({
  lookupData: context.lookupData
});

const GridComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__config-grid',
  flashMessages: service(),
  i18n: service(),
  isCalledOnce: false,

  dataSourceData: computed('lookupData.[]', 'dataSourceDetails', function() {
    const [lookupData] = this.lookupData;
    const dsData = getData(lookupData, this.dataSourceDetails);
    const orderDetails = getOrder(lookupData, this.dataSourceDetails);
    const orderedArray = [];
    const warningData = getWarningInfo(lookupData, this.dataSourceDetails);
    if (warningData && !this.get('isCalledOnce')) {
      this.set('isCalledOnce', true);
      const warningMessage = `context.error.archer.${warningData.type}`;
      const flashMessage = `${warningData.data}${this.get('i18n').t(warningMessage)}`;
      // Sometimes ember computed property is not triggering in current runloop. So calling warning
      // message in next run loop.
      next(this, () => {
        this.get('flashMessages').warning(flashMessage);
      });
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
  }),

  fullWidth: computed('dataSourceData', function() {
    return isEmpty(this.dataSourceData) ? '' : 'full-width';
  })
});
export default connect(stateToComputed)(GridComponent);
