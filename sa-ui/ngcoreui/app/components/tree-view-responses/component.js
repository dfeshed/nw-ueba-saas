import Component from '@ember/component';
import { connect } from 'ember-redux';
import { responses } from 'ngcoreui/reducers/selectors';
import { toggleOperationResponse, toggleResponseAsJson } from 'ngcoreui/actions/actions';

const stateToComputed = (state) => ({
  responses: responses(state),
  // TODO selectors?
  responseExpanded: state.shared.responseExpanded
});

const dispatchToActions = {
  toggleOperationResponse,
  toggleResponseAsJson
};

const treeViewResponses = Component.extend({
});

export default connect(stateToComputed, dispatchToActions)(treeViewResponses);
