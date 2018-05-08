import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { run } from '@ember/runloop';
import { connect } from 'ember-redux';

import { select, event, selectAll } from 'd3-selection';
import { zoom } from 'd3-zoom';
import { tree, hierarchy } from 'd3-hierarchy';
import { transitionElbow, elbow, appendText, updateText, appendIcon } from './helpers/d3-helpers';

import zoomed from './helpers/zoomed';


import { isStreaming, children, rootProcess } from 'investigate-process-analysis/reducers/process-tree/selectors';
import { getEvents } from 'investigate-process-analysis/actions/creators/events-creators';
import { fetchProcessDetails } from 'investigate-process-analysis/actions/data-creators/process-properties';
import { truncateText } from './util/data';

const stateToComputed = (state) => ({
  rootProcess: rootProcess(state),
  isStreaming: isStreaming(state),
  children: children(state)
});

const dispatchToActions = {
  getEvents,
  fetchProcessDetails
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

  zoom: 0.8,

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
  rectWidth: 56,

  /**
   * Tree node rectangle height
   * @property
   * @public
   */
  rectHeight: 56,

  nodeSize: [56, 56],

  /**
   * Specify the gap between the two node
   * @property
   * @public
   */
  nodeSeparation: 2,

  /**
   * Icon to represent the collapse tree node. If you want to add the icon in D3 svg we use font-icon code. Here it's
   * using rsa-font-icons, you can get the icon code from the _icons.scss, to make it unicode append the 'u' ex: \ue9ad
   * @property
   * @public
   */
  collapseIcon: '\ue9ad', // Unicode

  expandIcon: '\ue904', // Unicode

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

  _getRootNode() {
    const rootNode = this.get('rootProcess');
    const children = this.get('children');
    const childCount = children ? children.length : 0;

    if (childCount) {
      rootNode.children = children;
      rootNode.childCount = childCount;
      rootNode.expanded = true;
    }

    rootNode.id = 1;
    return rootNode;
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
      .attr('d', (d) => elbow(d, rectWidth));

    // Remove any exiting links
    link.exit().transition()
      .duration(duration)
      .attr('d', () => {
        const o = { x: source.x, y: (source.y + rectWidth / 2) };
        return transitionElbow({ source: o, target: o });
      })
      .remove();
  },

  _getNewNodes(selectedNode, children) {
    return children.map((item) => {
      const newNode = hierarchy(item);
      newNode.depth = selectedNode.depth + 1;
      newNode.height = selectedNode.height - 1;
      newNode.parent = selectedNode;
      return newNode;
    });
  },

  _appendExpandCollapseIcon(nodeEnter, collapseIcon, expandIcon, width) {

    const collapseWrapper = nodeEnter.append('g')
      .attr('class', 'button-wrapper')
      .on('click', run.bind(this, 'expandCollapseProcess'));

    const text = (d) => {
      if (d.data.childCount || d.children) {
        if (d.data.expanded) {
          return collapseIcon;
        } else {
          return expandIcon;
        }
      }
      return '';
    };
    appendIcon({ className: 'text-icon', node: collapseWrapper, dx: (width / 2) + 14, fontSize: '1.25em', text });
  },

  _onNodeEnter(node, source) {
    const { expandIcon, collapseIcon, rectWidth: width } = this.getProperties('expandIcon', 'collapseIcon', 'rectWidth');
    const nodeEnter = node.enter().append('g')
      .attr('class', 'process')
      .attr('data-id', function(d) {
        return d.data.id;
      })
      .attr('transform', () => {
        return `translate(${ source.y0 + width / 2 },${ source.x0 })`;
      })
      .on('click', run.bind(this, 'processProperties'))
      .on('mousedown', function() {
        event.stopImmediatePropagation();
      });

    this._appendExpandCollapseIcon(nodeEnter, collapseIcon, expandIcon, width);

    nodeEnter.append('circle')
      .attr('class', 'process');

    appendText({ className: 'process-name', node: nodeEnter, dx: 0, dy: 0, opacity: 0, text: (d) => truncateText(d.data.processName) });

    appendIcon({ className: 'process-icon', node: nodeEnter, fontSize: '2.5em', text: '\ue944' });

    appendText({ className: 'child-count', node: nodeEnter, dx: (width / 2) + 26, dy: 0, opacity: 1, text: (d) => d.data.childCount ? d.data.childCount : '' });

    return nodeEnter;
  },

  _onNodeUpdate(node, nodeEnter) {
    const { duration } = this.getProperties('duration');
    const nodeUpdate = node.merge(nodeEnter)
      .transition()
      .duration(duration)
      .attr('transform', (d) => `translate(${ d.y },${ d.x })`);

    nodeUpdate.select('circle.process')
      .attr('cursor', 'pointer')
      .attr('r', '2rem');

    updateText({ className: 'process-name', node: nodeUpdate, dx: 0, dy: '3em', opacity: 1 });
  },

  _onNodeExit(node, source) {
    const { rectWidth: width, duration } = this.getProperties('rectWidth', 'duration');
    const nodeExit = node.exit()
      .transition()
      .duration(duration)
      .attr('transform', () => `translate(${ source.y + width / 2 - 20},${ source.x })`)
      .remove();

    updateText({ className: 'process-name', dy: '-3em', node: nodeExit });
  },
  /**
   *
   * @param svg
   * @param nodes
   * @param source
   * @private
   */
  _addNodes(svg, nodes, source) {
    const node = svg.selectAll('g.process').data(nodes, (process) => process.data ? process.data.id : process.id);

    const nodeEnter = this._onNodeEnter(node, source);
    this._onNodeUpdate(node, nodeEnter);
    this._onNodeExit(node, source);

  },

  _collapse(d) {
    if (d.children) {
      d._children = d.children;
      d._children.forEach(run.bind(this, '_collapse'));
      d.children = null;
    }
  },

  didReceiveAttrs() {
    this._super(...arguments);

    if (this.isDestroyed) {
      return;
    }

    this.set('rootNode', null);
    // If query input changes then need to re-render the tree
    if (this.get('queryInput')) {
      const onComplete = () => {
        const rootNode = this._getRootNode();

        const root = hierarchy(rootNode, (d) => d.children || []);
        root.x0 = 0;
        root.y0 = 0;

        if (this.isDestroyed) {
          return;
        }
        this.set('rootNode', root);

        this._initializeChart();
      };
      this.send('getEvents', null, { onComplete });
      const { checksum } = this.get('queryInput');
      const hashes = [checksum];
      this.send('fetchProcessDetails', { hashes });
    }
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
      d.y = d.depth * 220;
    });

    // Creating the links with enter, update and exit functionality
    this._addLinks(svg, links, source);

    // Creating the nodes with enter, update and exit functionality
    this._addNodes(svg, nodes, source);

    // Stash the old positions for transition.
    nodes.forEach((process) => {
      process.x0 = process.x;
      process.y0 = process.y;
    });
  },

  expandCollapseProcess(d) {
    event.stopImmediatePropagation();
    d.data.expanded = !d.data.expanded;
    if (d.data.childCount) {
      if (d.data.expanded) {
        this.expandProcess(d);
      } else {
        this.collapseProcess(d);
      }

      const { expandIcon, collapseIcon } = this.getProperties('expandIcon', 'collapseIcon');
      const icon = d.data.expanded ? collapseIcon : expandIcon;
      select(`*[data-id='${ d.data.id }']`).select('text.text-icon').text(icon);
    }
  },

  expandProcess(d) {
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

  processProperties(d) {
    const checksum = d.data.checksum ? d.data.checksum : d.data['checksum.dst'];
    const hashes = [checksum];
    this.send('fetchProcessDetails', { hashes });

    // Update the node style
    selectAll('circle.process').classed('selected', false);
    selectAll('.process-icon').classed('selected', false);
    select(`*[data-id='${ d.data.id }']`).select('circle.process').classed('selected', true);
    select(`*[data-id='${ d.data.id }']`).select('.process-icon').classed('selected', true);
  },

  collapseProcess(d) {
    event.stopImmediatePropagation();
    if (d.children) {
      d._children = d.children;
      d.children = null;
    }
    this.buildChart(d);
  }
});

export default connect(stateToComputed, dispatchToActions)(TreeComponent);
