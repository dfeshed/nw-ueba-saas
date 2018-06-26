import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { run } from '@ember/runloop';
import { connect } from 'ember-redux';
import _ from 'lodash';
import { select, event, selectAll } from 'd3-selection';
import { zoom } from 'd3-zoom';
import { tree, hierarchy } from 'd3-hierarchy';
import { transitionElbow, elbow, appendText, updateText, appendIcon } from './helpers/d3-helpers';
import { ieEdgeDetection } from 'component-lib/utils/browser-detection';
import { toggleProcessDetailsVisibility } from 'investigate-process-analysis/actions/creators/process-visuals';
import $ from 'jquery';
import { inject as service } from '@ember/service';
import { processDetails } from 'investigate-process-analysis/reducers/process-properties/selectors';
import { fetchProcessDetails } from 'investigate-process-analysis/actions/creators/process-properties';
import { resetFilterValue } from 'investigate-process-analysis/actions/creators/process-filter';
import { sendTetherEvent } from 'component-lib/utils/tooltip-trigger';
import { truncateText } from './util/data';
import zoomed from './helpers/zoomed';

import {
  isStreaming,
  children,
  selectedProcessPath
} from 'investigate-process-analysis/reducers/process-tree/selectors';

import {
  getParentAndChildEvents,
  getChildEvents,
  setSelectedProcess,
  selectedProcessEvents } from 'investigate-process-analysis/actions/creators/events-creators';


const stateToComputed = (state) => ({
  isStreaming: isStreaming(state),
  children: children(state),
  path: selectedProcessPath(state),
  selectedProcessId: state.processAnalysis.processTree.queryInput ? state.processAnalysis.processTree.queryInput.vid : '',
  isProcessDetailsVisible: state.processAnalysis.processVisuals.isProcessDetailsVisible,
  processName: state.processAnalysis.processTree.queryInput ? state.processAnalysis.processTree.queryInput.pn : ''
});

const dispatchToActions = {
  setSelectedProcess,
  getParentAndChildEvents,
  getChildEvents,
  fetchProcessDetails,
  selectedProcessEvents,
  resetFilterValue,
  toggleProcessDetailsVisibility
};

let freeIdCounter = 0;
let hideEvent = null;
let displayEvent = null;

const COLLAPSE_ICON_SIZE = 16;
const COUNT_SPACING = 2;
const SPACING = 5;

const TreeComponent = Component.extend({

  zoomed,

  eventBus: service(),

  classNames: 'process-tree',

  classNameBindings: ['isStreaming:show-nodes:hide-nodes'],

  attributeBindings: ['zoom:data-zoom'],

  isStreaming: false,

  hasEvents: true,
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
  rectWidth: 46,

  /**
   * Tree node rectangle height
   * @property
   * @public
   */
  rectHeight: 46,

  nodeSize: [46, 46],

  /**
   * Specify the gap between the two node
   * @property
   * @public
   */
  nodeSeparation: 3.75,

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
    const {
      element,
      rootNode,
      zoomBehaviour,
      selectedProcessId
    } = this.getProperties('element', 'rootNode', 'zoomBehaviour', 'selectedProcessId');
    const el = select(element);
    this.centeringElement = el.select('.centering-element');

    const parent = el.select('svg');
    parent.call(zoomBehaviour);

    this.parent = parent;
    if (rootNode) {
      this.buildChart(rootNode);
      this.addSelectedClass(selectedProcessId);
    }
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
      .data(links, (d) => d.data ? d.data.processId : d.processId);

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
      if (newNode.children && newNode.children.length) {
        newNode.children.forEach((d) => {
          d.depth = newNode.depth + 1;
        });
      }
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
    appendIcon({
      className: 'text-icon',
      node: collapseWrapper, dx: (width / 2) + (COLLAPSE_ICON_SIZE / 2) + SPACING,
      fontSize: COLLAPSE_ICON_SIZE,
      text
    });
  },

  _onNodeEnter(node, source) {
    const { expandIcon, collapseIcon, rectWidth: width, eventBus } = this.getProperties('expandIcon', 'collapseIcon', 'rectWidth', 'eventBus');
    const nodeEnter = node.enter().append('g')
      .attr('class', 'process')
      .attr('data-id', function(d) {
        return d.data.processId;
      })
      .attr('transform', () => {
        return `translate(${ source.y0 + width / 2 },${ source.x0 })`;
      })
      .on('click', run.bind(this, 'processProperties'))
      .on('mousedown', function() {
        event.stopImmediatePropagation();
      });

    this._appendExpandCollapseIcon(nodeEnter, collapseIcon, expandIcon, width);

    const circle = nodeEnter.append('g')
      .attr('id', function() {
        return `endpoint-process-${freeIdCounter++}`;
      })
      .on('mouseover', function(d) {
        const $el = $(this);
        $el.addClass('panel1');
        if (hideEvent) {
          run.cancel(hideEvent);
          hideEvent = null;
        }
        const event = run.later(() => {
          if (!$el[0]) {
            return;
          }
          sendTetherEvent($el[0], 'panel1', eventBus, 'display', processDetails(d.data));
        }, 200);
        displayEvent = event;
      })
      .on('mouseleave', function(d) {
        const $el = $(this);
        $el.addClass('panel1');
        if (displayEvent) {
          run.cancel(displayEvent);
          displayEvent = null;
        }
        const event = run.later(() => {
          if ($el[0]) {
            sendTetherEvent($el[0], 'panel1', eventBus, 'hide', d);
          }
        }, 200);
        hideEvent = event;

      });

    circle.append('circle')
      .attr('class', 'process');

    appendText({
      className: 'process-name',
      node: nodeEnter,
      dx: 0,
      dy: 0,
      opacity: 0,
      text: (d) => truncateText(d.data.processName)
    });

    appendIcon({ className: 'process-icon', node: nodeEnter, fontSize: '20px', text: '\ue944' });

    appendText({
      className: 'child-count',
      node: nodeEnter,
      dx: (width / 2) + COLLAPSE_ICON_SIZE + SPACING + COUNT_SPACING,
      dy: 0,
      opacity: 1,
      text: (d) => d.data.childCount ? d.data.childCount : ''
    });

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
      .attr('r', '1.643rem');

    if (ieEdgeDetection()) { // icon is positioned according to the browser
      nodeUpdate.select('text.process-icon')
        .attr('dy', '.4em');
    }

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
    const node = svg.selectAll('g.process').data(nodes, (process) => process.data ? process.data.processId : process.processId);

    const nodeEnter = this._onNodeEnter(node, source);
    this._onNodeUpdate(node, nodeEnter);
    this._onNodeExit(node, source);

  },

  /**
   * Creates the tree type data from the flat array, based on processId and parentId
   * @param eventsData
   * @param selectedProcess
   * @param path
   * @returns {Array}
   * @private
   */
  _prepareTreeData(eventsData, selectedProcessId, path) {
    const hashTable = {};
    eventsData.forEach((aData) => hashTable[aData.processId] = { ...aData, children: [], _children: [] });
    const dataTree = [];
    eventsData.forEach((aData) => {
      hashTable[aData.processId].expanded = selectedProcessId === aData.processId;
      if (aData.parentId) {
        if (path.includes(aData.processId) || selectedProcessId === aData.parentId) {
          hashTable[aData.parentId].children.push(hashTable[aData.processId]);
        } else {
          hashTable[aData.parentId]._children.push(hashTable[aData.processId]);
        }
      } else {
        dataTree.push(hashTable[aData.processId]);
      }
    });
    return dataTree;
  },

  _documentTitle(processName) {
    const { hn: hostName } = this.get('queryInput');
    return hostName ? `${hostName} - ${processName}` : `${processName}`;
  },

  didReceiveAttrs() {
    this._super(...arguments);

    if (this.isDestroyed) {
      return;
    }
    // clear the tree
    select('.tree-layer').selectAll('*').remove();
    this.set('rootNode', null);
    // If query input changes then need to re-render the tree
    if (this.get('queryInput')) {
      const { checksum, pn, vid } = this.get('queryInput');
      const onComplete = () => {

        const { children, selectedProcessId, path } = this.getProperties('children', 'selectedProcessId', 'path');

        const selectedProcess = children.filter((child) => child.processId === selectedProcessId);

        const defaultSelectedProcess = selectedProcess[0] ? selectedProcess[0] : { processId: vid };

        this.send('setSelectedProcess', defaultSelectedProcess);
        if (children && children.length) {
          const rootNode = this._prepareTreeData(children, selectedProcessId, path); // Only initial load

          const root = hierarchy(rootNode[0], (d) => {
            return d.children || [];
          });

          root.x0 = 0;
          root.y0 = 0;

          if (this.isDestroyed) {
            return;
          }
          this.set('rootNode', root);
          this.set('hasEvents', true);
        } else {
          this.set('hasEvents', false);
        }


        document.title = this._documentTitle(pn);
        this._initializeChart();
        this.send('selectedProcessEvents', this.get('selectedProcessId'), {});
      };
      this.send('getParentAndChildEvents', this.get('selectedProcessId'), { onComplete });

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
    const links = nodes.slice(1);

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
      select(`*[data-id='${ d.data.processId }']`).select('text.text-icon').text(icon);
    }
  },

  expandProcess(d) {
    if (d._children || d.children) {
      let modifiedChildren = [];
      // Show remaining children
      if (d.data._children && d.data._children.length) {
        modifiedChildren = d.children.concat(this._getNewNodes(d, d.data._children));
        d.data._children = null;
      } else {
        modifiedChildren = d.children || [];
      }
      d.children = d._children || modifiedChildren;
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
      this.send('getChildEvents', d.data.processId, { onComplete });
    }
  },

  processProperties(d) {
    const checksum = d.data.checksum ? d.data.checksum : d.data['checksum.dst'];
    const hashes = [checksum];
    this.send('fetchProcessDetails', { hashes });
    this.send('setSelectedProcess', _.omit(d.data, 'children'));
    if (!this.get('isProcessDetailsVisible')) {
      this.send('toggleProcessDetailsVisibility');
    }
    this.addSelectedClass(d.data.processId);
    this.send('resetFilterValue', d.data.processId);
    document.title = this._documentTitle(d.data.processName);
  },

  addSelectedClass(id) {
    // Update the node style
    selectAll('circle.process').classed('selected', false);
    selectAll('.process-icon').classed('selected', false);
    select(`*[data-id='${id}']`).select('circle.process').classed('selected', true);
    select(`*[data-id='${id}']`).select('.process-icon').classed('selected', true);
  },

  collapseProcess(d) {
    event.stopImmediatePropagation();
    if (d.children) {
      d._children = d.children;
      d.children = null;
    }
    this.buildChart(d);
  },
  willDestroyElement() {
    freeIdCounter = 0;
  }
});

export default connect(stateToComputed, dispatchToActions)(TreeComponent);
