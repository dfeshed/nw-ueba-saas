import Component from 'ember-component';
import { throttle } from 'ember-runloop';
import connect from 'ember-redux/components/connect';
import { toggleByteStyling } from 'recon/actions/visual-creators';
import layout from './template';

const stateToComputed = ({ recon }) => ({
  hasStyledBytes: recon.packets.hasStyledBytes
});

const dispatchToActions = {
  toggleByteStyling
};

const ByteShadingComponent = Component.extend({
  layout,

  actions: {
    _toggleByteStyling() {
      // TODO: Remove once https://github.com/knownasilya/ember-toggle/pull/72
      // is merged.
      throttle(this, this.send, 'toggleByteStyling', 50);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ByteShadingComponent);
