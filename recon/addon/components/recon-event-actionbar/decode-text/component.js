import Component from '@ember/component';
import computed from 'ember-computed';
import { connect } from 'ember-redux';
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
  isCompressed: computed.not('isDecoded')
});

export default connect(stateToComputed, dispatchToActions)(DecodeTextComponent);
