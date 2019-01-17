import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { endpointMeta, eventTime, eventCategory, hostName, user } from 'recon/reducers/meta/selectors';


const stateToComputed = (state) => ({
  endpointMeta: endpointMeta(state),
  eventTime: eventTime(state),
  hostName: hostName(state),
  user: user(state),
  eventCategory: eventCategory(state)
});

const EndpointRecon = Component.extend({
  layout,
  classNames: ['recon-event-detail-endpoint']
});

export default connect(stateToComputed)(EndpointRecon);
