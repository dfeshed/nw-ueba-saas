import Component from '@ember/component';
import { connect } from 'ember-redux';
import { changeDirectory } from 'ngcoreui/actions/actions';
import { currentDirectoryContents, isNotRoot, pathParent } from 'ngcoreui/reducers/selectors';

const dispatchToActions = {
  changeDirectory
};

const stateToComputed = (state) => ({
  currentDirectoryContents: currentDirectoryContents(state),
  isNotRoot: isNotRoot(state),
  pathParent: pathParent(state)
});

const treeViewDirectoryContents = Component.extend({
});

export default connect(stateToComputed, dispatchToActions)(treeViewDirectoryContents);
