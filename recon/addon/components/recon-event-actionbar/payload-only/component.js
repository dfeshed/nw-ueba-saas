import Component from 'ember-component';
import { throttle } from 'ember-runloop';
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
  layout,

  actions: {
    _togglePayloadOnly() {
      // TODO: Remove once https://github.com/knownasilya/ember-toggle/pull/72
      // is merged.
      throttle(this, this.send, 'togglePayloadOnly', 50);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(PayloadOnlyComponent);