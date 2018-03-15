import layout from './template';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Component from '@ember/component';
import { getData } from 'context/util/context-data-modifier';
import { isEmpty } from 'ember-utils';

const stateToComputed = ({ context: { context } }) => ({
  lookupData: context.lookupData
});

const DynamicGridComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__grid',

  @computed('lookupData.[]', 'dataSourceDetails')
  dataSourceData: ([lookupData], dataSourceDetails) => getData(lookupData, dataSourceDetails),

  @computed('dataSourceData')
  hasDataSourceData: (dataSourceData) => !isEmpty(dataSourceData)

});
export default connect(stateToComputed)(DynamicGridComponent);
