import layout from './template';
import Component from 'ember-component';
import connect from 'ember-redux/components/connect';

const stateToComputed = ({ context }) => ({
  lookupData: context.lookupData,
  dataSources: context.dataSources
});

const EndpointComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__endpoint'
});
export default connect(stateToComputed)(EndpointComponent);
