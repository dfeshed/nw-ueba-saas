import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  isFileView,
  isPacketView,
  isTextView
} from 'recon/reducers/visuals/selectors';
import {
  isEndpointEvent,
  isHttpData,
  isLogEvent
} from 'recon/reducers/meta/selectors';
import layout from './template';

const stateToComputed = ({ recon, recon: { packets } }) => ({
  isEndpointEvent: isEndpointEvent(recon),
  isFileView: isFileView(recon),
  isHttpEvent: isHttpData(recon),
  isLogEvent: isLogEvent(recon),
  isPacketView: isPacketView(recon),
  isPayloadOnly: packets.isPayloadOnly,
  isTextView: isTextView(recon)
});

const reconEventActionbar = Component.extend({
  layout,
  classNames: ['recon-event-actionbar']
});

export default connect(stateToComputed)(reconEventActionbar);
