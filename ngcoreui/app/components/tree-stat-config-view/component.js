import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { deselectStat, setConfigValue } from 'ngcoreui/actions/actions';
import { liveSelectedNode, selectedIsConfigNode, configSetResult, selectedNodeRequiresRestart } from 'ngcoreui/reducers/selectors';

const stateToComputed = (state) => ({
  selectedNode: state.selectedNode,
  liveSelectedNode: liveSelectedNode(state),
  selectedIsConfigNode: selectedIsConfigNode(state),
  configSetResult: configSetResult(state),
  selectedNodeRequiresRestart: selectedNodeRequiresRestart(state)
});

const dispatchToActions = {
  deselectStat,
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
