import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import { decodeText } from 'recon/actions/data-creators';
import { isNotHttpData } from 'recon/selectors/meta-selectors';
import layout from './template';

const stateToComputed = ({ recon }) => ({
  isDecoded: recon.data.decode,
  isDisabled: isNotHttpData(recon)
});

const dispatchToActions = (dispatch) => ({
  decodeText: (decode) => dispatch(decodeText(decode))
});

const DecodeTextComponent = Component.extend({
  layout
});

export default connect(stateToComputed, dispatchToActions)(DecodeTextComponent);
