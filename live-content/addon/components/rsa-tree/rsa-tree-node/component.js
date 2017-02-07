import Ember from 'ember';
import computed from 'ember-computed-decorators';

const { Component, isArray, isEmpty } = Ember;


export default Component.extend({
  tagName: 'li',
  classNames: ['rsa-tree-node'],
  classNameBindings: ['hasChildren', 'isExpanded:expanded', 'isSelected:selected'],

  node: null,

  init() {
    this._super(...arguments);
    if (this.get('expandAll')) {
      this.set('expanded', true);
    }
  },

  /**
   * The property of the object that indicates the value associated with the node. The value represents
   * a unique identifier similar to a <select> option value to be used when the tree is part of a form and
   * needs to convey a selected node value. The default is "id"
   * @property nodeValueProperty
   * @public
   */
  nodeValueProperty: 'id',

  /**
   * The property of the object that indicates the display name associated with the node. The default is "name"
   * @property nodeDisplayNameProperty
   * @public
   */
  nodeDisplayNameProperty: 'name',

  /**
   * The string value representing the property name in the the node that represents the node's child objects array.
   * The default is "children"
   * @property nodeChildrenProperty
   * @public
   */
  nodeChildrenProperty: 'children',

  /**
   * Whether the tree node is expanded.
   * @property isExpanded
   * @public
   */
  @computed('hasChildren', 'expanded')
  isExpanded: (hasChildren, expanded) => !hasChildren ? false : expanded,

  /**
   * Whether the tree node is expanded
   * @property expanded
   * @public
   */
  expanded: false,

  /**
   * Returns the array of child nodes for this specific node
   * @property children
   * @public
   * @returns {Array} Returns an array of child nodes. The array is empty if no child nodes exist
   */
  @computed('node', 'nodeChildrenProperty')
  children: (node = {}, nodeChildrenProperty) => isArray(node[nodeChildrenProperty]) ? node[nodeChildrenProperty] : [],

  /**
   * Whether the node has any child nodes
   * @property hasChildren
   * @public
   * @returns {boolean} Returns true if the node has child nodes
   */
  @computed('children')
  hasChildren: (children) => !!children.length,

  /**
   * Whether the node is currently selected
   * @public
   * @property isSelected
   */
  @computed('selectedNode')
  isSelected(selectedNode) {
    let isSelected = false;

    if (!isEmpty(selectedNode)) {
      const currentNode = this.get('node');
      if (currentNode === selectedNode ||
          currentNode[this.get('nodeValueProperty')] === selectedNode) {
        isSelected = true;
      }
    }

    return isSelected;
  },

  @computed('node')
  nodeValue: (node) => node[this.get('nodeValueProperty')] || null,

  actions: {
    toggleExpand() {
      this.toggleProperty('expanded');
    },
    select() {
      this.get('onNodeSelection')(this.get('node'));
    }
  }

});
