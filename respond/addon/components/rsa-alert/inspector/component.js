import Component from '@ember/component';
import { connect } from 'ember-redux';

const stateToComputed = ({ respond: { alert: { info, infoStatus } } }) => ({
  info,
  infoStatus
});

/**
 * @class Alert Inspector component
 * An Alert Inspector, similar to the one used inside an `rsa-explorer` in the `rsa-alerts` route,
 * except that its state is bound to the `respond.alert` redux state.
 * @public
 */
const AlertInspector = Component.extend({
  tagName: '',
  info: null,
  infoStatus: null
});

export default connect(stateToComputed)(AlertInspector);


