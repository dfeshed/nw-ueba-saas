import Component from '@ember/component';
import { connect } from 'ember-redux';
import { togglePayloadOnly } from 'recon/actions/data-creators';
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
