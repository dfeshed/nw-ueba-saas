import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { changeActiveTab, changeDirectory, selectNode, deselectNode } from 'ngcoreui/actions/actions';
import pathParent from 'ngcoreui/reducers/selectors/path-parent';
import { isFlag, FLAGS } from 'ngcoreui/services/transport/flag-helper';

export default Route.extend({
  redux: service(),
  transport: service(),
  router: service(),

  model(params) {
    let path = `/${params.path}`;
    const redux = this.get('redux');
    redux.dispatch(changeActiveTab('tree'));
    if (path === '/tree') {
      path = '/';
    }

    const state = redux.store.getState().shared;
    const currentURL = this.get('router').get('currentURL');

    if (path === '/') {
      redux.dispatch(changeDirectory(path));
    // If we have a folder loaded, the route is being called from a link we created.
    // We do not need to check for the existence of the node, and we can find it's type in state
    } else if (state.treePathContents && state.treePathContents.nodes) {
      // If we are trying to navigate to a parent of the current node, we know that is a folder node.
      let isPathAncestor = false;
      let tempTreePath = state.treePath;
      while (tempTreePath) {
        tempTreePath = pathParent(tempTreePath);
        if (tempTreePath === path) {
          isPathAncestor = true;
          break;
        }
      }
      if (path === pathParent(currentURL) && state.selectedNode) {
        // If we have a stat/config node currently selected, we do not need to perform ls again
        redux.dispatch(deselectNode());
      } else if (isPathAncestor) {
        if (state.selectedNode) {
          redux.dispatch(deselectNode());
        }
        redux.dispatch(changeDirectory(path));
      // Otherwise, we should look and see what type of node we're trying to navigate to first
      } else {
        const node = state.treePathContents.nodes.find((testNode) => {
          return testNode.path === path;
        });
        if (!node) {
          throw new Error(`Internal link clicked that was not available in state: ${path}`);
        }
        if (isFlag(node.nodeType, FLAGS.FOLDER_NODE)) {
          redux.dispatch(changeDirectory(path));
        } else if (isFlag(node.nodeType, FLAGS.STAT_NODE) || isFlag(node.nodeType, FLAGS.CONFIG_NODE)) {
          redux.dispatch(selectNode(node));
        }
      }
    // If the tree state is empty, then the page is being loaded from a URL and we have to query that node to
    // determine its type
    } else {
      const transport = this.get('transport');
      transport.send(path, {
        message: 'info'
      }).then((message) => {
        const { node } = message;
        if (isFlag(node.nodeType, FLAGS.FOLDER_NODE)) {
          redux.dispatch(changeDirectory(path));
        } else if (isFlag(node.nodeType, FLAGS.STAT_NODE) || isFlag(node.nodeType, FLAGS.CONFIG_NODE)) {
          redux.dispatch(changeDirectory(pathParent(path)));
          redux.dispatch(selectNode(node));
        } else {
          // We don't know how to handle this node
          this.replaceWith(pathParent(path));
          throw new Error(`Not sure how to handle node with type of ${node.nodeType}`);
        }
      }).catch(() => {
        this.replaceWith('/');
      });
    }
  }
});
