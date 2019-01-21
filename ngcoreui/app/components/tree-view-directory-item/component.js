import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { changeDirectory, selectNode } from 'ngcoreui/actions/actions';
import { isFlag, FLAGS } from 'ngcoreui/services/transport/flag-helper';

const dispatchToActions = {
  changeDirectory,
  selectNode
};

const treeViewDirectoryItem = Component.extend({
  node: null,

  classNameBindings: [
    'isStatOrConfigNode:thin-horizontal-scroll',
    'order',
    'requiresRestart:error-text'
  ],

  @computed('node')
  isStatNode: (node) => isFlag(node.nodeType, FLAGS.STAT_NODE),

  @computed('node')
  isConfigNode: (node) => isFlag(node.nodeType, FLAGS.CONFIG_NODE),

  @computed('isStatNode', 'isConfigNode')
  isStatOrConfigNode: (isStatNode, isConfigNode) => isStatNode || isConfigNode,

  @computed('isStatNode', 'isConfigNode')
  order: (isStatNode, isConfigNode) => {
    if (isConfigNode) {
      return 'order-2';
    } else if (isStatNode) {
      return 'order-1';
    } else {
      return false;
    }
  },

  @computed('node')
  requiresRestart: (node) => isFlag(node.nodeType, FLAGS.CONFIG_VALUE_RESTART_NEEDED)
});

export default connect(undefined, dispatchToActions)(treeViewDirectoryItem);
