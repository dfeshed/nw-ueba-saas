import Component from 'ember-component';
import { throttle } from 'ember-runloop';
import computed from 'ember-computed';
import connect from 'ember-redux/components/connect';
import { decodeText } from 'recon/actions/data-creators';
import layout from './template';

const stateToComputed = ({ recon }) => ({
  isDecoded: recon.text.decode
});

const dispatchToActions = {
  decodeText
};

const DecodeTextComponent = Component.extend({
  layout,
  isCompressed: computed.not('isDecoded'),

  actions: {
    _decodeText() {
      // TODO: Remove once https://github.com/knownasilya/ember-toggle/pull/72
      // is merged.
      throttle(this, this.send, 'decodeText', 50);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DecodeTextComponent);
