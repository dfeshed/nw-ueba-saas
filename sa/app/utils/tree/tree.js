/**
 * @file Tree class
 * Represents a generic hierarchical set of data nodes, with parent-child relationships (1 to many).
 * @public
 */
import Ember from 'ember';

const {
  get,
  Object: EmberObject
} = Ember;

// Helper function that searches a tree's nodes recursively.
// To test if the given node matches the given value, first tries to call `node.isEqual(value)` if defined;
// otherwise, just uses triple equals with node's value.
function recursiveFind(value, node) {
  if (node) {
    let nodeValue = get(node, 'value');
    if ((typeof node.isEqual === 'function') ? node.isEqual(value) : (value === nodeValue)) {
      return node;
    }
    let children = get(node, 'children'),
      len = (children && children.length) || 0,
      i;
    for (i = 0; i < len; i++) {
      if (recursiveFind(value, children[i])) {
        return children[i];
      }
    }
  }
  return null;
}

export default EmberObject.extend({
  /**
   * The root node of the tree. Typically an instance of utils/tree/node, with properties `value`, `children` & `parent`.
   * @type {object}
   * @public
   */
  root: null,

  /**
   * Adds a given node to the tree.
   * @param {object} node The node to be added.
   * @param {object} parent Optional parent node whose `children` will hold the newly added node. If not given,
   * the `root` node is assumed; if root is not given, the new node becomes the root.
   * @returns {object} This tree instance, for chaining.
   * @public
   */
  add(node, parent) {
    if (node) {
      parent = parent || this.get('root');
      if (!parent) {
        this.set('root', node);
        node.set('parent', null);
      } else {
        parent.addChild(node);
      }
    }
    return this;
  },

  /**
   * Removes a given node from the tree.
   * @param {object} node The node to be removed.
   * @returns {object} This tree instance, for chaining.
   * @public
   */
  remove(node) {
    if (node) {
      let parent = node.get('parent');
      if (parent) {
        parent.removeChild(node);
      } else if (this.get('root') === node) {
        this.set('root', null);
      }
    }
    return this;
  },

  /**
   * Recursive search thru the tree nodes.
   * @param {*} value The value being searched for.
   * @param {object} startNode Optional starting node; if unspecified, the tree root node is assumed.
   * @returns {object} The first tree node that matches the given value, if any; null otherwise.
   * @public
   */
  find(value, startNode) {
    return recursiveFind(value, startNode || this.get('root'));
  }
});
