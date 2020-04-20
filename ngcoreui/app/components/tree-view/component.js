import Component from '@ember/component';
import { connect } from 'ember-redux';
import { changeDirectory } from 'ngcoreui/actions/actions';
import { description } from 'ngcoreui/reducers/selectors';

const stateToComputed = (state) => ({
  // TODO selectors?
  treePath: state.shared.treePath,
  description: description(state),
  selectedNode: state.shared.selectedNode,
  responseExpanded: state.shared.responseExpanded
});

const dispatchToActions = {
  changeDirectory
};

const treeViewComponent = Component.extend({
  tagName: 'vbox',
  classNames: ['max-width', 'max-height']
});

export default connect(stateToComputed, dispatchToActions)(treeViewComponent);