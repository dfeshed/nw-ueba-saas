import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import { toggleKnownSignatures } from 'recon/actions/visual-creators';
import layout from './template';

const stateToComputed = ({ recon }) => ({
  hasSignaturesHighlighted: recon.packets.hasSignaturesHighlighted
});

const dispatchToActions = {
  toggleKnownSignatures
};

const CommonFilePatternsComponent = Component.extend({
  layout
});

export default connect(stateToComputed, dispatchToActions)(CommonFilePatternsComponent);
