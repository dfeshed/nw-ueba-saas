import layout from './template';
import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import { needToDisplay } from 'context/util/context-data-modifier';
import computed from 'ember-computed-decorators';

const stateToComputed = ({ context }) => ({
  lookupData: context.lookupData,
  dataSources: context.dataSources
});

const EndpointComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__endpoint',

  @computed('lookupData.[]', 'dataSources')
  columnLength: ([lookupData], dataSources) => {
    if (!needToDisplay(null, lookupData, { dataSourceGroup: 'Modules' }, dataSources)) {
      return { IOC: 'col-xs-12', Modules: 'col-xs-0' };
    }
    if (!needToDisplay(null, lookupData, { dataSourceGroup: 'IOC' }, dataSources)) {
      return { IOC: 'col-xs-0', Modules: 'col-xs-12' };
    }
    return { IOC: 'col-xs-5', Modules: 'col-xs-7' };
  }

});
export default connect(stateToComputed)(EndpointComponent);
