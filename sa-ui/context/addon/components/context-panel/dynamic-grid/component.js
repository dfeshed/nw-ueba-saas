import { computed } from '@ember/object';
import layout from './template';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { getData } from 'context/utils/context-data-modifier';
import { isEmpty } from 'ember-utils';

const stateToComputed = ({ context: { context } }) => ({
  lookupData: context.lookupData
});

const DynamicGridComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__grid',

  dataSourceData: computed('lookupData.[]', 'dataSourceDetails', function() {
    const [lookupData] = this.lookupData;
    return getData(lookupData, this.dataSourceDetails);
  }),

  hasDataSourceData: computed('dataSourceData', function() {
    return !isEmpty(this.dataSourceData);
  })

});
export default connect(stateToComputed)(DynamicGridComponent);
