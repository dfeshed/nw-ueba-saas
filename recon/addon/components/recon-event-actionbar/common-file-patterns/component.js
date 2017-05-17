import Component from 'ember-component';
import { throttle } from 'ember-runloop';
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
  layout,

  actions: {
    _toggleKnownSignatures() {
      // TODO: Remove once https://github.com/knownasilya/ember-toggle/pull/72
      // is merged.
      throttle(this, this.send, 'toggleKnownSignatures', 50);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(CommonFilePatternsComponent);
