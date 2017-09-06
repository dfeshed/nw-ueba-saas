/**
 * @file Tree Node class
 * Represents a generic single node in a tree.
 * @public
 */
import Ember from 'ember';

const {
  computed,
  Object: EmberObject
} = Ember;

export default EmberObject.extend({
  /**
   * The node's value; can be anything, but typically a hash of data values.
   * @type {*}
   * @public
   */
  value: null,

  /**
   * Reference to this node's parent node. The root node of a tree would have no parent.
   * @type {object}
   * @public
   */
  parent: null,

  /**
   * Array of child nodes; empty by default.
   * @type {object[]}
   * @public
   */
  children: computed(() => {
    return [];
  }),

  /**
   * Adds a given node this node's children. Responsible for setting the given node's `parent`.
   * @param {object} childNode The node to be added.
   * @returns {object} This node instance, for chaining.
   * @public
   */
  addChild(childNode) {
    if (childNode) {
      this.get('children').pushObject(childNode);
      childNode.set('parent', this);
    }
    return this;
  },

  /**
   * Adds a given node this node's children. Clears the given node's `parent` only if it points to this node.
   * @param {object} childNode The node to be removed.
   * @returns {object} This node instance, for chaining.
   * @public
   */
  removeChild(childNode) {
    if (childNode) {
      this.get('children').removeObject(childNode);
      if (childNode.get('parent') === this) {
        childNode.set('parent', null);
      }
    }
    return this;
  }
});
