import layout from './template';
import Component from 'ember-component';
import connect from 'ember-redux/components/connect';

const stateToComputed = ({ context }) => ({
  activeTabName: context.activeTabName,
  meta: context.meta,
  lookupData: context.lookupData
});

const EndpointComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__endpoint'
});
export default connect(stateToComputed)(EndpointComponent);
