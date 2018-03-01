import layout from './template';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

const stateToComputed = ({ context }) => ({
  lookupData: context.lookupData
});

const EndpointComponent = Component.extend({
  layout,
  classNames: 'rsa-context-panel__endpoint',

  @computed('lookupData.[]')
  moduleHeader: ([lookupData]) => lookupData && lookupData.Modules ? lookupData.Modules.header : ''
});
export default connect(stateToComputed)(EndpointComponent);
