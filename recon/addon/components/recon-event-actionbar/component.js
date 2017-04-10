import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import { isTextView, isFileView, isPacketView } from 'recon/selectors/type-selectors';
import { isLogEvent } from 'recon/selectors/event-type-selectors';
import * as VisualActions from 'recon/actions/visual-creators';
import layout from './template';

const dispatchToActions = (dispatch) => ({
  togglePayloadOnly: (payloadOnly) => dispatch(VisualActions.togglePayloadOnly(payloadOnly))
});

const stateToComputed = ({ recon, recon: { data, visuals } }) => ({
  view: data.currentReconView.code,
  isTextView: isTextView(recon),
  isFileView: isFileView(recon),
  isPacketView: isPacketView(recon),
  isLogEvent: isLogEvent(recon),
  isPayloadOnly: visuals.isPayloadOnly
});

const reconEventActionbar = Component.extend({
  layout,
  classNames: ['recon-event-actionbar']
});

export default connect(stateToComputed, dispatchToActions)(reconEventActionbar);