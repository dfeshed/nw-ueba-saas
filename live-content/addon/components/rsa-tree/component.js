import Ember from 'ember';
import computed, { none } from 'ember-computed-decorators';

const { Component, isPresent } = Ember;


/**
 * A Tree view component to represent hierarchical information such as taxonomies or folder trees.
 * @extends Ember.Component
 * @public
 */
export default Component.extend({
  tagName: 'ul',
  classNames: ['rsa-tree'],
  classNameBindings: ['isRoot:root', 'hasSelectableNodes:selectable', 'showTreeLines'],

  /**
   * The component to be rendered as a node of the three. Extensions of this base component can be used
   * @type {string}
   * @default 'rsa-tree/rsa-tree-node'
   * @public
   */
  nodeComponentName: 'rsa-tree/rsa-tree-node',

  /**
   * Expresses whether the tree is the root tree (rather than a branch tree). If the tree has no
   * parent node, it is root. If it does have a parent node, then this tree is a branch in a
   * larger tree.
   * @type {boolean}
   * @public
   */
  @none('parentNode') isRoot: false,

  /**
   * The parent node of this tree, if one exists, indicating that it is a branch in a larger tree.
   * @private
   */
  parentNode: null,

  /**
   * The property in the nodes objects that represents the "value" of the node.
   * @type {string}
   * @public
   */
  nodeValueProperty: 'id',

  /**
   * The property in the nodes objects that represents the display name to be shown to the user as the label
   * for each node.
   * @type {string}
   * @public
   */
  nodeDisplayNameProperty: 'name',

  /**
   * The property in the nodes objects that represents the list of child nodes. If the node object is a leaf node,
   * then the value is either null or empty array.
   * @type {Array}
   * @public
   */
  nodeChildrenProperty: 'children',

  /**
   * Expresses whether the nodes in the tree are selectable, in which case state will be held for the currently
   * selected node, if one exists.
   * @type {boolean}
   * @public
   */
  hasSelectableNodes: true,

  /**
   * The currently selected node in the tree, or null
   * @type {Object}
   * @public
   */
  selectedNode: null,

  /**
   * The value of the currently selected node or null
   * @public
   */
  @computed('selectedNode')
  selectedValue(selectedNode) {
    return isPresent(selectedNode) ? selectedNode[this.get('nodeValueProperty')] : null;
  },

  /**
   * Expresses whether nodes with children should be expanded. If false, the child nodes will be collapsed
   * by default
   * @type {boolean}
   * @public
   */
  expanded: false,

  /**
   * Expresses whether lines should be displayed to better delineate the various levels in the tree
   * @type {boolean}
   * @public
   */
  showTreeLines: false,

  actions: {
    handleSelect(node) {
      if (node === this.get('selectedNode')) {
        node = null;
      }
      this.set('selectedNode', node);
      this.sendAction('onchange', this.get('selectedValue'));
    }
  }
});
