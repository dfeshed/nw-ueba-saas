import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import * as VisualActions from 'recon/actions/visual-creators';
import layout from './template';

const dispatchToActions = (dispatch) => ({
  togglePayloadOnly: () => dispatch(VisualActions.togglePayloadOnly())
});

const stateToComputed = ({ recon: { visuals } }) => ({
  isPayloadOnly: visuals.isPayloadOnly
});

const PayloadOnlyComponent = Component.extend({
  layout
});

export default connect(stateToComputed, dispatchToActions)(PayloadOnlyComponent);