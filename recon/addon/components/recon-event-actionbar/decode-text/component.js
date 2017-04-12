import Component from 'ember-component';
import computed from 'ember-computed';
import connect from 'ember-redux/components/connect';
import { decodeText } from 'recon/actions/data-creators';
import layout from './template';

const stateToComputed = ({ recon }) => ({
  isDecoded: recon.data.decode
});

const dispatchToActions = (dispatch) => ({
  decodeText: () => dispatch(decodeText())
});

const DecodeTextComponent = Component.extend({
  layout,
  isCompressed: computed.not('isDecoded')
});

export default connect(stateToComputed, dispatchToActions)(DecodeTextComponent);
