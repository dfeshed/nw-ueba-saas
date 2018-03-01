import Component from '@ember/component';
import { connect } from 'ember-redux';
import { toggleByteStyling } from 'recon/actions/visual-creators';
import layout from './template';

const stateToComputed = ({ recon }) => ({
  hasStyledBytes: recon.packets.hasStyledBytes
});

const dispatchToActions = {
  toggleByteStyling
};

const ByteShadingComponent = Component.extend({
  layout
});

export default connect(stateToComputed, dispatchToActions)(ByteShadingComponent);
