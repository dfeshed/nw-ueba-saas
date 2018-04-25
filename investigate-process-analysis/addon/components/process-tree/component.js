import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { run } from '@ember/runloop';
import { connect } from 'ember-redux';

import { select, event } from 'd3-selection';
import { zoom } from 'd3-zoom';
import { tree, hierarchy } from 'd3-hierarchy';
import { transitionElbow, elbow, updateRect, appendRect, appendText, updateText } from './helpers/d3-helpers';

import zoomed from './helpers/zoomed';


import { isStreaming, children, rootProcess } from 'investigate-process-analysis/reducers/process-tree/selectors';
import { getEvents } from 'investigate-process-analysis/actions/creators/events-creators';

const stateToComputed = (state) => ({
  rootProcess: rootProcess(state),
  isStreaming: isStreaming(state),
  children: children(state)
});

const dispatchToActions = {
  getEvents
};


const TreeComponent = Component.extend({

  zoomed,

  classNames: 'process-tree',

  classNameBindings: ['isStreaming:show-nodes:hide-nodes'],

  attributeBindings: ['zoom:data-zoom'],

  isStreaming: false,

  /**
   * D3 tree minimum zoom
   * @property
   * @public
   */
  zoomMin: 0.1,

  /**
   * D3 tree maximum zoom
   * @property
   * @public
   */
  zoomMax: 2,

  /**
   * Default animation time
   * @property
   * @public
   */
  duration: 750,

  /**
   * Tree node rectangle width
   * @property
   * @public
   */
  rectWidth: 150,

  /**
   * Tree node rectangle height
   * @property
   * @public
   */
  rectHeight: 30,

  nodeSize: [50, 50],

  /**
   * Specify the gap between the two node
   * @property
   * @public
   */
  nodeSeparation: 0.85,

  /**
   * Icon to represent the collapse tree node. If you want to add the icon in D3 svg we use font-icon code. Here it's
   * using rsa-font-icons, you can get the icon code from the _icons.scss, to make it unicode append the 'u' ex: \ue9ad
   * @property
   * @public
   */
  collapseIcon: '\ue9ad', // Unicode

  rootNode: null,

  @computed('nodeSize', 'nodeSeparation')
  treeInstance(nodeSize, nodeSeparation) {
    const treeInstance = tree()
      .nodeSize(nodeSize)
      .separation(() => {
        return nodeSeparation;
      });
    return treeInstance;
  },

  @computed('element')
  svg(element) {
    if (element) {
      const height = element.clientHeight || 0;
      const el = select(element);
      const treeSVG = el.select('.tree-layer')
        .attr('transform', `translate(150,${height / 2})`);
      return treeSVG;
    }
    return null;
  },

  @computed('element')
  zoomBehaviour(element) {
    const el = select(element);
    const zoomCallback = run.bind(this, 'zoomed');

    const zoomBehaviour = zoom()
      .scaleExtent([
        this.get('zoomMin'),
        this.get('zoomMax')
      ])
      .on('start', () => {
        el.classed('is-panning', true);
      })
      .on('end', () => {
        el.classed('is-panning', false);
      })
      .on('zoom', zoomCallback);
    return zoomBehaviour;
  },

  didReceiveAttrs() {
    this._super(...arguments);
    this.set('rootNode', null);
    // If query input changes then need to re-render the tree
    if (this.get('queryInput')) {
      const onComplete = () => {
        const rootNode = this.get('rootProcess');
        rootNode.children = this.get('children');

        const root = hierarchy(rootNode, (d) => d.children || []);
        root.x0 = 0;
        root.y0 = 0;

        this.set('rootNode', root);

        this._initializeChart();
      };
      this.send('getEvents', null, { onComplete });
    }
  },

  /**
   * Initialize the required object for rendering the D3 tree chart.
   * @private
   */
  _initializeChart() {
    const { element, rootNode, zoomBehaviour } = this.getProperties('element', 'rootNode', 'zoomBehaviour');
    const el = select(element);
    this.centeringElement = el.select('.centering-element');

    const parent = el.select('svg');
    parent.call(zoomBehaviour);

    this.parent = parent;
    // Show only 1 level of child node
    if (rootNode.children && rootNode.children.length) {
      rootNode.children.forEach(run.bind(this, '_collapse'));
    }
    this.buildChart(rootNode);
  },

  /**
   * Build the chart for given source and root node
   * @param source
   * @public
   */
  buildChart(source) {

    const { rootNode, svg, treeInstance } = this.getProperties('rootNode', 'svg', 'treeInstance');

    // Re calculate the tree layout
    const tree = treeInstance(rootNode);

    const nodes = tree.descendants();
    const links = tree.descendants().slice(1);

    // Calculating the height of the tree
    nodes.forEach((d) => {
      d.y = d.depth * 260;
    });

    // Creating the links with enter, update and exit functionality
    this._addLinks(svg, links, source);

    // Creating the nodes with enter, update and exit functionality
    this._addNodes(svg, nodes, source);

    // Creating the collapse with enter, update and exit functionality
    this._addCollapseButton(svg);

    // Stash the old positions for transition.
    nodes.forEach((process) => {
      process.x0 = process.x;
      process.y0 = process.y;
    });
  },

  /**
   * Adding the collapse icon on expanded node. To identify whether node is expanded or not using node's children
   * property. If node has the children that means node is expanded else not
   * @private
   */
  _addCollapseButton(svg) {

    const { rectWidth, duration, collapseIcon } = this.getProperties('svg', 'rectWidth', 'duration', 'collapseIcon');

    const node = svg.selectAll('g.process');

    const buttons = node.selectAll('text.text-icon')
      .data(function(d) {
        return d.children && d.children.length ? [d] : [];
      });

    const buttonEnter = buttons.enter();

    buttonEnter.append('text')
      .attr('class', 'text-icon')
      .style('fill-opacity', 0)
      .attr('dy', '8')
      .attr('font-family', 'nw-icon-library-all-1')
      .attr('font-size', function() {
        return '1em';
      })
      .on('mousedown', function() {
        // Stopping the event propagation this is required in d3.v4 else zoom wil take the priority
        event.stopImmediatePropagation();
      })
      .on('click', run.bind(this, 'collapseProcess'))
      .text(function() {
        return collapseIcon;
      });

    const buttonUpdate = node.merge(buttonEnter);

    buttonUpdate.select('text.text-icon')
      .transition()
      .duration(duration)
      .style('fill-opacity', 1)
      .attr('dx', (rectWidth / 2))
      .attr('dy', '8');


    buttons.exit()
      .transition()
      .duration(duration)
      .style('fill-opacity', 0)
      .remove();
  },

  /**
   * Adding the links between nodes.
   * @param svg
   * @param links
   * @param source
   * @private
   */
  _addLinks(svg, links, source) {
    const { rectWidth, duration } = this.getProperties('rectWidth', 'duration');

    const link = svg.selectAll('path.link')
      .data(links, (d) => d.data ? d.data.id : d.id);

    const linkEnter = link.enter().append('path')
      .attr('class', 'link')
      .attr('d', () => {
        const o = { x: source.x0, y: (source.y0 + rectWidth / 2) };
        return transitionElbow({ source: o, target: o });
      });

    const linkUpdate = linkEnter.merge(link);

    // Update the old links positions
    linkUpdate.transition()
      .duration(duration)
      .attr('d', (d) => elbow(d, rectWidth, 90));

    // Remove any exiting links
    link.exit().transition()
      .duration(duration)
      .attr('d', () => {
        const o = { x: source.x, y: (source.y + rectWidth / 2) };
        return transitionElbow({ source: o, target: o });
      })
      .remove();
  },


  /**
   *
   * @param svg
   * @param nodes
   * @param source
   * @private
   */
  _addNodes(svg, nodes, source) {
    const { rectWidth: width, rectHeight: height, duration } = this.getProperties('rectWidth', 'rectHeight', 'duration');
    const node = svg.selectAll('g.process').data(nodes, (process) => process.data ? process.data.id : process.id);

    const nodeEnter = node.enter().append('g')
      .attr('class', 'process')
      .attr('transform', () => {
        return `translate(${ source.y0 + width / 2 },${ source.x0 })`;
      })
      .on('mousedown', function() {
        event.stopImmediatePropagation();
      })
      .on('click', run.bind(this, 'expandProcess'));

    // Append the rectangle on entering the node
    appendRect({ node: nodeEnter });

    // Display process name inside the  rectangle
    appendText({ className: 'process-name', node: nodeEnter, dx: 0, dy: 0, opacity: 0 });

    const nodeUpdate = node.merge(nodeEnter)
      .transition()
      .duration(duration)
      .attr('transform', (d) => `translate(${ d.y },${ d.x })`);

    updateRect({ node: nodeUpdate, rx: 10, ry: 10, width, height, x: -(width / 2), y: -(height / 2) });
    updateText({ className: 'process-name', node: nodeUpdate, dx: -(width / 2) + 10, dy: '0.3em', opacity: 1 });

    const nodeExit = node.exit()
      .transition()
      .duration(duration)
      .attr('transform', () => `translate(${ source.y + width / 2 - 20},${ source.x })`)
      .remove();

    updateRect({ node: nodeExit });
    updateText({ className: 'process-name', dy: '-0.3em', node: nodeExit });
  },

  _collapse(d) {
    if (d.children) {
      d._children = d.children;
      d._children.forEach(run.bind(this, '_collapse'));
      d.children = null;
    }
  },

  expandProcess(d) {
    event.stopImmediatePropagation();
    if (d._children || d.children) {
      d.children = d._children;
      d._children = null;
      this.buildChart(d);
    } else {
      const onComplete = () => {
        const children = this.get('children');
        if (children && children.length) {
          const nodes = this._getNewNodes(d, children);
          d.children = nodes;
          d.data.children = nodes;
          d._children = null;
          this.buildChart(d);
        }
      };
      this.send('getEvents', d.data.processName, { onComplete });
    }
  },

  collapseProcess(d) {
    event.stopImmediatePropagation();
    if (d.children) {
      d._children = d.children;
      d.children = null;
    }
    this.buildChart(d);
  },
  _getNewNodes(selectedNode, children) {
    const nodes = children.map((item) => {
      const newNode = hierarchy(item);
      newNode.depth = selectedNode.depth + 1;
      newNode.height = selectedNode.height - 1;
      newNode.parent = selectedNode;
      return newNode;
    });
    return nodes;
  }
});

export default connect(stateToComputed, dispatchToActions)(TreeComponent);
