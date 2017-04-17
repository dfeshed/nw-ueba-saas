import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import { isTextView, isFileView, isPacketView } from 'recon/selectors/type-selectors';
import { isHttpData, isLogEvent } from 'recon/reducers/meta/selectors';
import layout from './template';

const stateToComputed = ({ recon }) => ({
  isTextView: isTextView(recon),
  isFileView: isFileView(recon),
  isPacketView: isPacketView(recon),
  isLogEvent: isLogEvent(recon),
  isHttpEvent: isHttpData(recon)
});

const reconEventActionbar = Component.extend({
  layout,
  classNames: ['recon-event-actionbar']
});

export default connect(stateToComputed)(reconEventActionbar);