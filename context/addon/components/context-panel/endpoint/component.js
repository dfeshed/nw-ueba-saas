import { computed } from '@ember/object';
import layout from './template';
import Component from '@ember/component';
import { connect } from 'ember-redux';

const stateToComputed = ({ context: { context } }) => ({
  lookupData: context.lookupData
});

const EndpointComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__endpoint',

  moduleHeader: computed('lookupData.[]', function() {
    const [lookupData] = this.lookupData;
    return lookupData && lookupData.Modules ? lookupData.Modules.header : '';
  })
});
export default connect(stateToComputed)(EndpointComponent);
