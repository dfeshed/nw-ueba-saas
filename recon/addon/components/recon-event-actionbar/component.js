import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import { isTextView, isFileView, isPacketView } from 'recon/selectors/type-selectors';
import { isLogEvent } from 'recon/selectors/event-type-selectors';
import layout from './template';

const { Component } = Ember;

const stateToComputed = ({ recon, recon: { data } }) => ({
  view: data.currentReconView.code,
  isTextView: isTextView(recon),
  isFileView: isFileView(recon),
  isPacketView: isPacketView(recon),
  isLogEvent: isLogEvent(recon)
});

const reconEventActionbar = Component.extend({
  layout,
  classNames: ['recon-event-actionbar']
});

export default connect(stateToComputed)(reconEventActionbar);