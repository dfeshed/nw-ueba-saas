import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { isFlag, FLAGS } from 'ngcoreui/services/transport/flag-helper';
import { deselectNode, setConfigValue } from 'ngcoreui/actions/actions';
import { liveSelectedNode, selectedIsConfigNode, configSetResult, selectedNodeRequiresRestart, pathToUrlSegment } from 'ngcoreui/reducers/selectors';

const stateToComputed = (state) => ({
  // TODO selectors?
  selectedNode: state.shared.selectedNode,
  liveSelectedNode: liveSelectedNode(state),
  selectedIsConfigNode: selectedIsConfigNode(state),
  configSetResult: configSetResult(state),
  selectedNodeRequiresRestart: selectedNodeRequiresRestart(state),
  pathToUrlSegment: pathToUrlSegment(state)
});

const dispatchToActions = {
  deselectNode,
  setConfigValue
};

const treeStatView = Component.extend({
  configValue: '',

  @computed('selectedNode')
  hasNoGetPermission: (selectedNode) => {
    return isFlag(selectedNode.nodeType, FLAGS.NODE_GET_ROLE_MISSING);
  },

  @computed('configSetResult')
  configSetSuccess: (configSetResult) => {
    return configSetResult === true;
  },

  @computed('configSetResult')
  configSetError: (configSetResult) => {
    return typeof configSetResult === 'string';
  }
});

export default connect(stateToComputed, dispatchToActions)(treeStatView);
