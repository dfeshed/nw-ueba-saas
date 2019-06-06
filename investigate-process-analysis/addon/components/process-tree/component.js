import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { run } from '@ember/runloop';
import { connect } from 'ember-redux';
import _ from 'lodash';
import { select, event, selectAll } from 'd3-selection';
import { zoom, zoomIdentity } from 'd3-zoom';
import { tree, hierarchy } from 'd3-hierarchy';
import { set } from '@ember/object';
import {
  addNodeContent,
  addSelectedClass,
  addLinks,
  appendExpandCollapseIcon,
  getNewNodes,
  updateStyle,
  onNodeExit, onNodeUpdate,
  prepareTreeData,
  documentTitle
} from './helpers/content';
import { toggleProcessDetailsVisibility } from 'investigate-process-analysis/actions/creators/process-visuals';
import { inject as service } from '@ember/service';
import { processDetails } from 'investigate-process-analysis/reducers/process-properties/selectors';
import { fetchProcessDetails } from 'investigate-process-analysis/actions/creators/process-properties';
import { resetFilterValue } from 'investigate-process-analysis/actions/creators/process-filter';
import { sendTetherEvent } from 'component-lib/utils/tooltip-trigger';
import zoomed from './helpers/zoomed';
import copyToClipboard from 'component-lib/utils/copy-to-clipboard';
import { CONST, ICON } from './const';

import {
  isStreaming,
  children,
  selectedProcessPath
} from 'investigate-process-analysis/reducers/process-tree/selectors';

import {
  getFileProperty,
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
  processName: state.processAnalysis.processTree.queryInput ? state.processAnalysis.processTree.queryInput.pn : '',
  selectedServerId: state.processAnalysis.processTree.selectedServerId,
  fileProperty: state.processAnalysis.processTree.fileProperty || {}
});

const dispatchToActions = {
  setSelectedProcess,
  getParentAndChildEvents,
  getChildEvents,
  fetchProcessDetails,
  selectedProcessEvents,
  resetFilterValue,
  toggleProcessDetailsVisibility,
  getFileProperty
};

let freeIdCounter = 0;
let nodeIdCounter = 0;
let hideEvent = null;
let displayEvent = null;

const TreeComponent = Component.extend({

  zoomed,

  eventBus: service(),

  currentElement: null,

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

  nodeSize: [CONST.LINK_WIDTH, CONST.LINK_HEIGHT],

  /**
   * Specify the gap between the two node
   * @property
   * @public
   */
  nodeSeparation: 5.75,

  /**
   * Icon to represent the collapse tree node. If you want to add the icon in D3 svg we use font-icon code. Here it's
   * using rsa-font-icons, you can get the icon code from the _icons.scss, to make it unicode append the 'u' ex: \ue9ad
   * @property
   * @public
   */

  rootNode: null,

  process: null,

  @computed('nodeSize', 'nodeSeparation')
  treeInstance(nodeSize, nodeSeparation) {
    const treeInstance = tree()
      .nodeSize(nodeSize)
      .separation(() => {
        return nodeSeparation;
      });
    return treeInstance;
  },

  @computed('fileProperty', 'process')
  processData(fileProperty, process) {
    const properties = ['userAll', 'signature', 'reputationStatus', 'fileStatus', 'directoryDst', 'paramDst'];
    const displayProperties = {};
    properties.forEach(function(prop) {
      if (prop === 'signature') {
        const { signature } = fileProperty;
        displayProperties.signature = signature && signature.signer ? 'Signed' : 'Unsigned';
        displayProperties.signer = signature && signature.signer ? signature.signer : '';
      } else {
        displayProperties[prop] = fileProperty[prop] || '';
      }
    });
    displayProperties.directoryDst = process.directoryDst;
    displayProperties.paramDst = process.paramDst;
    displayProperties.userAll = process.userAll;
    return displayProperties;
  },

  getSVG: (element) => {
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

    const toolBarButtons = document.querySelector('.process-details-buttons');

    if (this.isDestroyed) {
      return;
    }
    // clear the tree
    this.set('rootNode', null);
    // If query input changes then need to re-render the tree
    if (this.get('queryInput')) {
      selectAll('.tree-layer > *').remove();
      const { checksum, pn, vid } = this.get('queryInput');
      const onComplete = () => {

        const { children, selectedProcessId, path } = this.getProperties('children', 'selectedProcessId', 'path');

        const selectedProcess = children.filter((child) => child.processId === selectedProcessId);

        const defaultSelectedProcess = selectedProcess[0] ? selectedProcess[0] : { processId: vid };

        this.send('setSelectedProcess', defaultSelectedProcess);
        if (children && children.length) {

          const rootNode = prepareTreeData(children, selectedProcessId, path); // Only initial load

          const root = hierarchy(rootNode[0], (d) => {
            return d.children || null;
          });

          root.x0 = 0;
          root.y0 = 0;

          if (this.isDestroyed) {
            return;
          }
          this.set('rootNode', root);
          this.set('hasEvents', true);
          updateStyle(toolBarButtons, 'block');
        } else {
          // Hide the close button
          updateStyle(toolBarButtons, 'none');
          this.set('hasEvents', false);
        }


        document.title = documentTitle(pn, this.get('queryInput'));
        this.send('selectedProcessEvents', this.get('selectedProcessId'), {});
        this._initializeChart();
        const hashes = [checksum];
        this.send('fetchProcessDetails', { hashes }, this.get('selectedServerId'));
      };
      this.send('getParentAndChildEvents', this.get('selectedProcessId'), { onComplete });
    }
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

    // Needed for zooming
    this.centeringElement = el.select('.centering-element');

    const parent = el.select('svg');
    parent.call(zoomBehaviour);

    this.parent = parent;
    if (rootNode) {
      // Reset Zoom
      const transform = zoomIdentity
        .scale(1)
        .translate(0, 0);
      zoomBehaviour.transform(parent, transform);

      this._buildChart(rootNode);
      addSelectedClass(selectedProcessId);
    }
  },

  /**
   * Build the chart for given source and root node
   * @param source
   * @private
   */
  _buildChart(source) {

    const { rootNode, getSVG, treeInstance, element } = this.getProperties('rootNode', 'getSVG', 'treeInstance', 'element');

    const svg = getSVG(element);

    // Re calculate the tree layout
    const tree = treeInstance(rootNode);

    const nodes = tree.descendants();
    const links = nodes.slice(1);

    // Creating the links with enter, update and exit functionality
    addLinks(svg, links, source);

    // Creating the nodes with enter, update and exit functionality
    this._addNodes(svg, nodes, source);

    // Stash the old positions for transition.
    nodes.forEach((process) => {
      process.x0 = process.x;
      process.y0 = process.y;
    });
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
    onNodeUpdate(node, nodeEnter);
    onNodeExit(node, source);
  },

  _showPopup(element, d) {
    this.set('currentElement', element);
    run.later(async() => {
      if (!element) {
        return;
      }
      let children = d.children || [];
      if (d._children) {
        children = children.concat(d._children);
      }
      sendTetherEvent(element, 'process-filter', this.get('eventBus'), 'display', { node: d, children });
    }, 200);
  },

  _appendExpandCollapseIcon(nodeEnter, counter) {
    const self = this;
    let tempCounter = counter;
    const expandWrapper = nodeEnter.append('g')
      .attr('class', 'button-wrapper')
      .attr('id', function() {
        tempCounter++;
        return `expand-${tempCounter}`;
      })
      .on('click', function(d) {
        event.stopImmediatePropagation();
        /*
         * d.children => visible child nodes
         * d._children => hidden child nodes
         * d.data._children => raw child nodes
         */
        if (d._children || d.children) { // Child nodes are already fetched and partially drawn. Initial state
          if (d.data._children && d.data._children.length) {
            const newNodes = getNewNodes(d, d.data._children);
            d._children = d._children ? [...d._children, ...newNodes] : newNodes;
            d.data._children = null;
          }
          if (d.children) {
            d.children.forEach((node) => {
              if (!node.selected && node.data.selected) {
                set(node, 'selected', true);
              }
            });
          }
          self._showPopup(this, d);
        } else { // Children not fetched
          self._getChildProcess(d, this);
        }

        this.setAttribute('class', `${this.getAttribute('class')} process-filter`);
      });
    tempCounter = counter;
    const collapseWrapper = nodeEnter.append('g')
      .attr('class', 'button-wrapper')
      .attr('id', function() {
        tempCounter++;
        return `collapse-${tempCounter}`;
      })
      .on('click', function(d) {
        event.stopImmediatePropagation();
        self.collapseProcess(d);
      });
    appendExpandCollapseIcon(expandWrapper, collapseWrapper);
  },

  _onNodeEnter(node, source) {
    const self = this;
    const eventBus = this.get('eventBus');
    const nodeEnter = node.enter().append('g')
      .attr('class', 'process')
      .attr('test-id', function() {
        nodeIdCounter++;
        return `process-${nodeIdCounter}`;
      })
      .attr('data-id', function(d) {
        return d.data.processId;
      })
      .attr('transform', () => {
        return `translate(${ source.y0 + CONST.NODE_WIDTH / 2 },${ source.x0 })`;
      })
      .on('click', run.bind(this, 'processProperties'))
      .on('mousedown', function() {
        event.stopImmediatePropagation();
      });
    this._appendExpandCollapseIcon(nodeEnter, freeIdCounter);

    const processNode = nodeEnter.append('g')
      .attr('id', function() {
        freeIdCounter++;
        return `endpoint-process-${freeIdCounter}`;
      })
      .on('mouseover', function(d) {
        const element = this;
        element.setAttribute('class', 'panel1');
        if (hideEvent) {
          run.cancel(hideEvent);
          hideEvent = null;
        }
        const checksum = d.data.checksum ? d.data.checksum : d.data['checksum.dst'];
        const hashes = [checksum];

        const event = run.later(async() => {
          if (!element) {
            return;
          }
          self.set('process', d.data);
          await self.send('getFileProperty', { hashes }, self.get('selectedServerId'));
          sendTetherEvent(element, 'panel1', eventBus, 'display', processDetails(d.data));
        }, 200);
        displayEvent = event;
      })
      .on('mouseleave', function(d) {
        const element = this;
        element.setAttribute('class', 'panel1');
        if (displayEvent) {
          run.cancel(displayEvent);
          displayEvent = null;
        }
        const event = run.later(() => {
          if (element) {
            sendTetherEvent(element, 'panel1', eventBus, 'hide', d);
          }
        }, 200);
        hideEvent = event;

      });
    return addNodeContent(processNode, nodeEnter);
  },

  _getChildProcess(d, element) {
    const onComplete = () => {
      const children = this.get('children');
      if (children && children.length) {
        const nodes = getNewNodes(d, children);
        d._children = nodes;
      }
      this._showPopup(element, d);
    };
    this.send('getChildEvents', d.data.processId, { onComplete });
  },

  expandProcess(node, children) {
    node.children = children.filter((node) => node.selected);
    node._children = children.filter((node) => !node.selected);
    node.data._children = null;

    this._buildChart(node);
  },

  expandAllProcess(node, children) {
    children.forEach((node) => {
      if (!node.selected) {
        set(node, 'selected', true);
      }
    });
    node.children = children;
    node.data._children = null;
    node._children = null;

    this._buildChart(node);
  },

  processProperties(d) {
    // recenter
    const transform = zoomIdentity
      .scale(1)
      .translate(-d.y, -d.x);
    this.parent.transition()
      .duration(CONST.DURATION)
      .call(this.get('zoomBehaviour').transform, transform);

    const checksum = d.data.checksum ? d.data.checksum : d.data['checksum.dst'];
    const hashes = [checksum];
    this.send('fetchProcessDetails', { hashes }, this.get('selectedServerId'));
    this.send('setSelectedProcess', _.omit(d.data, 'children'));
    if (!this.get('isProcessDetailsVisible')) {
      this.send('toggleProcessDetailsVisibility');
    }
    addSelectedClass(d.data.processId);
    this.send('resetFilterValue', d.data.processId);
    document.title = documentTitle(d.data.processName, this.get('queryInput'));
  },

  collapseProcess(d) {
    event.stopImmediatePropagation();
    const currentElement = this.get('currentElement');
    if (currentElement) {
      sendTetherEvent(currentElement, 'process-filter', this.get('eventBus'), 'hide');
    }
    if (d.children) {
      d._children = d._children ? [...d._children, ...d.children] : d.children;
      d._children.forEach((node) => {
        if (node.selected) {
          set(node, 'selected', false);
        }
      });
      d.children = null;
    }
    d.data.expanded = false;
    this._buildChart(d);
    select(`*[data-id='${ d.data.processId }']`).select('text.text-icon').text(ICON.EXPAND);
    select(`*[data-id='${ d.data.processId }']`).select('text.collapse-icon').text('');
  },

  willDestroyElement() {
    freeIdCounter = 0;
    nodeIdCounter = 0;
  },

  actions: {
    copyLaunchArgument(launchArguments) {
      copyToClipboard(launchArguments);
    },
    appendNodes({ node, children }) {
      node.data.expanded = true;
      if (node.data.childCount) {
        this.expandAllProcess(node, children);

        select(`*[data-id='${ node.data.processId }']`).select('text.text-icon').text(ICON.FILTER);
        select(`*[data-id='${ node.data.processId }']`).select('text.collapse-icon').text(ICON.COLLAPSE);
      }
    },

    filterNodes({ node, children }) {
      node.data.expanded = true;
      if (node.data.childCount) {
        this.expandProcess(node, children);

        select(`*[data-id='${ node.data.processId }']`).select('text.text-icon').text(ICON.FILTER);
        select(`*[data-id='${ node.data.processId }']`).select('text.collapse-icon').text(ICON.COLLAPSE);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(TreeComponent);
