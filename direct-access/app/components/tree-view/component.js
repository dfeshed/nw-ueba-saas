import Component from '@ember/component';
import { connect } from 'ember-redux';
import { changeDirectory } from 'direct-access/actions/actions';
import { description } from 'direct-access/reducers/selectors';

const stateToComputed = (state) => ({
  treePath: state.treePath,
  description: description(state),
  selectedNode: state.selectedNode
});

const dispatchToActions = {
  changeDirectory
};

const treeViewComponent = Component.extend({
  tagName: 'vbox'
});

export default connect(stateToComputed, dispatchToActions)(treeViewComponent);