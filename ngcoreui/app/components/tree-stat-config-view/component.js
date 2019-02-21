import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { deselectNode, setConfigValue } from 'ngcoreui/actions/actions';
import { liveSelectedNode, selectedIsConfigNode, configSetResult, selectedNodeRequiresRestart, pathToUrlSegment } from 'ngcoreui/reducers/selectors';

const stateToComputed = (state) => ({
  selectedNode: state.selectedNode,
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
