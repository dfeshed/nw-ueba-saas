import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { endpointMeta, eventTime, eventCategory, hostName, user } from 'recon/reducers/meta/selectors';


const stateToComputed = ({ recon }) => ({
  endpointMeta: endpointMeta(recon),
  eventTime: eventTime(recon),
  hostName: hostName(recon),
  user: user(recon),
  eventCategory: eventCategory(recon)
});

const EndpointRecon = Component.extend({
  layout,
  classNames: ['recon-event-detail-endpoint']
});

export default connect(stateToComputed)(EndpointRecon);
