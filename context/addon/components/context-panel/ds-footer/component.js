import layout from './template';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import Component from 'ember-component';
import * as DataUtil from 'context/util/context-data-modifier';


const stateToComputed = ({ context }) => ({
  lookupData: context.lookupData
});

const DSFooterComponent = Component.extend({
  layout,

  @computed('contextData', 'lookupData.[]', 'dSDetails')
  dsData(contextData, [lookupData], dSDetails) {
    if (!lookupData) {
      return;
    }
    if (contextData) {
      return contextData;
    }
    return lookupData[dSDetails.dataSourceGroup];
  },

  @computed('dsData')
  headerData(dsData) {
    return DataUtil.getHeaderData(dsData, this.get('i18n'));
  }
});
export default connect(stateToComputed)(DSFooterComponent);
