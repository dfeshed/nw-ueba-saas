import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';
import { isTextView, isFileView, isPacketView } from 'recon/selectors/type-selectors';
import { isLogEvent } from 'recon/selectors/event-type-selectors';
import * as VisualActions from 'recon/actions/visual-creators';
import layout from './template';

const dispatchToActions = (dispatch) => ({
  togglePayloadOnly: () => dispatch(VisualActions.togglePayloadOnly())
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
  classNames: ['recon-event-actionbar'],

  // This is temporary, and is used only to react to the PayloadOnly property.
  // Its sole purpose is to change the styling of the button. This will
  // probably go away once a real toggle switch is created.
  @computed('isPayloadOnly')
  payloadButtonStyle(isPayloadOnly) {
    return isPayloadOnly ? 'primary' : 'standard';
  }
});

export default connect(stateToComputed, dispatchToActions)(reconEventActionbar);