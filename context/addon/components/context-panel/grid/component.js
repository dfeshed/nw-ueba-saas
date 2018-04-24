import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Component from '@ember/component';
import { getData } from 'context/util/context-data-modifier';
import _ from 'lodash';

const stateToComputed = ({ context: { context } }) => ({
  lookupData: context.lookupData

});

const GridComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__config-grid',

  @computed('lookupData.[]', 'dataSourceDetails')
  dataSourceData([lookupData], dataSourceDetails) {
    const [ dsData ] = getData(lookupData, dataSourceDetails);
    return _.omit(dsData, 'Url');
  }
});
export default connect(stateToComputed)(GridComponent);
