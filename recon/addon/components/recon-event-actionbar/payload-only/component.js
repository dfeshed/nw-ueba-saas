import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import { togglePayloadOnly } from 'recon/actions/visual-creators';
import layout from './template';

const dispatchToActions = {
  togglePayloadOnly
};

const stateToComputed = ({ recon: { packets } }) => ({
  isPayloadOnly: packets.isPayloadOnly
});

const PayloadOnlyComponent = Component.extend({
  layout
});

export default connect(stateToComputed, dispatchToActions)(PayloadOnlyComponent);