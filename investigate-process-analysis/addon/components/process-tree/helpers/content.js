import { CONST, DISTANCE, ICON } from '../const';
import { appendIcon, appendText, elbow, transitionElbow, updateText } from './d3-helpers';
import { truncateText } from '../util/data';
import { hierarchy } from 'd3-hierarchy';
import { select, selectAll } from 'd3-selection';

const _getRiskScoreClass = (riskScore) => {
  if (riskScore === null) {
    return '';
  }

  let riskClass = 'is-low';
  if (riskScore <= 30) {
    riskClass = 'is-low';
  } else if (riskScore <= 69) {
    riskClass = 'is-medium';
  } else if (riskScore <= 99) {
    riskClass = 'is-high';
  } else if (riskScore > 99) {
    riskClass = 'is-danger';
  }
  return `local-risk-score ${riskClass}`;
};

export const addSelectedClass = (id) => {

  // To toggle the existing selection
  const previousSelection = select(`*[data-id='${id}']`).select('.machine-count').classed('selected');
  // Update the node style. First unselect previously selected nodes
  selectAll('circle.process').classed('selected', false);
  selectAll('.process-icon').classed('selected', false);
  selectAll('.process-name').classed('selected', false);
  selectAll('.machine-count').classed('selected', false);
  selectAll('.process-type').classed('selected', false);
  selectAll('.score-text').classed('selected', false);
  selectAll('rect').classed('selected', false);
  select(`*[data-id='${id}']`).select('circle.process').classed('selected', !previousSelection);
  select(`*[data-id='${id}']`).select('.process-icon').classed('selected', !previousSelection);
  select(`*[data-id='${id}']`).selectAll('.process-type').classed('selected', !previousSelection);
  select(`*[data-id='${id}']`).select('.process-name').classed('selected', !previousSelection);
  select(`*[data-id='${id}']`).select('.score-text').classed('selected', !previousSelection);
  select(`*[data-id='${id}']`).select('.machine-count').classed('selected', !previousSelection);
  select(`*[data-id='${id}']`).select('rect').classed('selected', !previousSelection);
  return !previousSelection;
};

/**
 * Creates the tree type data from the flat array, based on processId and parentId
 * @param eventsData
 * @param selectedProcess
 * @param path
 * @returns {Array}
 * @private
 */
export const prepareTreeData = (eventsData, selectedProcessId, path) => {
  const hashTable = {};
  eventsData.forEach((aData) => hashTable[aData.processId] = { ...aData, children: [], _children: [] });
  const dataTree = [];
  eventsData.forEach((aData) => {
    hashTable[aData.processId].expanded = selectedProcessId === aData.processId;
    if (aData.parentId) {
      if (path.includes(aData.processId) || selectedProcessId === aData.parentId) {
        hashTable[aData.processId].selected = true;
        hashTable[aData.parentId].children.push(hashTable[aData.processId]);
      } else {
        hashTable[aData.parentId]._children.push(hashTable[aData.processId]);
      }
    } else {
      dataTree.push(hashTable[aData.processId]);
    }
  });
  return dataTree;
};

/**
 * Adding the links between nodes.
 * @param svg
 * @param links
 * @param source
 * @private
 */
export const addLinks = (svg, links, source) => {
  const link = svg.selectAll('path.link')
    .data(links, (d) => d.data ? d.data.processId : d.processId);

  const linkEnter = link.enter().append('path')
    .attr('class', 'link')
    .attr('d', () => {
      const o = { x: source.x0, y: (source.y0 + CONST.NODE_WIDTH / 2) };
      return transitionElbow({ source: o, target: o });
    });

  const linkUpdate = linkEnter.merge(link);

  // Update the old links positions
  linkUpdate.transition()
    .duration(CONST.DURATION)
    .attr('d', (d) => elbow(d, CONST.NODE_WIDTH));

  // Remove any exiting links
  link.exit().transition()
    .duration(CONST.DURATION)
    .attr('d', () => {
      const o = { x: source.x, y: (source.y + CONST.NODE_WIDTH / 2) };
      return transitionElbow({ source: o, target: o });
    })
    .remove();
};


export const addNodeContent = (processNode, nodeEnter) => {
  processNode.append('rect')
    .attr('width', CONST.NODE_WIDTH)
    .attr('height', CONST.NODE_HEIGHT)
    .attr('x', -CONST.NODE_WIDTH / 2 + 5)
    .attr('y', -CONST.NODE_HEIGHT / 2)
    .attr('rx', CONST.RADIUS)
    .attr('class', 'process');

  appendText({
    className: 'process-name',
    node: nodeEnter,
    dx: 0,
    dy: 0,
    opacity: 0,
    text: (d) => truncateText(d.data.processName)
  });
  appendText({
    className: 'machine-count',
    node: nodeEnter,
    dx: 0,
    dy: DISTANCE.MACHINE_COUNT_Y,
    opacity: 1,
    text: () => truncateText('on (0) hosts')
  });

  // draw risk score
  processNode.append('circle')
    .attr('cx', DISTANCE.SCORE_X)
    .attr('cy', DISTANCE.SCORE_Y)
    .attr('r', CONST.RADIUS)
    .attr('style', (d) => d.data.localScore == null ? 'display: none;' : null)
    .attr('class', (d) => _getRiskScoreClass(d.data.localScore));

  processNode.append('text')
    .attr('dx', DISTANCE.SCORE_X)
    .attr('dy', DISTANCE.SCORE_Y + 4)
    .text((d) => d.data.localScore)
    .attr('class', 'score-text');


  appendIcon({ className: 'process-icon', node: nodeEnter, fontSize: '30px', text: '\ue944', dx: DISTANCE.ICON_X, dy: DISTANCE.ICON_Y }); // file icon
  appendIcon({ className: 'process-type', node: nodeEnter, fontSize: '15px', text: (d) => d.data.eventCategory && d.data.eventCategory.hasNetwork ? '\uea7b' : '', dx: DISTANCE.PROCESS_TYPE_X, dy: DISTANCE.PROCESS_TYPE_Y }); // network
  appendIcon({ className: 'process-type', node: nodeEnter, fontSize: '15px', text: (d) => d.data.eventCategory && d.data.eventCategory.hasFile ? '\uea7a' : '', dx: DISTANCE.PROCESS_TYPE_X + DISTANCE.ICON_WIDTH, dy: DISTANCE.PROCESS_TYPE_Y }); // file
  appendIcon({ className: 'process-type', node: nodeEnter, fontSize: '15px', text: (d) => d.data.eventCategory && d.data.eventCategory.hasRegistry ? '\uea79' : '', dx: DISTANCE.PROCESS_TYPE_X + (DISTANCE.ICON_WIDTH * 2), dy: DISTANCE.PROCESS_TYPE_Y }); // registry


  appendText({
    className: 'child-count',
    node: nodeEnter,
    dx: (d) => (CONST.NODE_WIDTH / 2) + CONST.COLLAPSE_ICON_SIZE - (d.data.childCount.toString().length),
    dy: 20,
    opacity: 1,
    text: (d) => d.data.childCount ? `(${d.data.childCount})` : ''
  });
  return nodeEnter;
};

export const appendExpandCollapseIcon = (expandWrapper, collapseWrapper) => {
  const text = (d) => {
    let retValue = '';
    if (d.data.childCount) {
      if (d.children) {
        retValue = ICON.FILTER;
      } else {
        retValue = ICON.EXPAND;
      }
    }
    return retValue;
  };
  appendIcon({
    className: 'text-icon',
    node: expandWrapper, dx: (CONST.NODE_WIDTH / 2) + (CONST.COLLAPSE_ICON_SIZE / 2) + CONST.SPACING,
    fontSize: CONST.COLLAPSE_ICON_SIZE,
    text
  });
  const collapseIcon = (d) => {
    if (d.data.childCount && d.children) {
      return ICON.COLLAPSE;
    }
    return '';
  };
  appendIcon({
    className: 'collapse-icon',
    node: collapseWrapper, dx: (CONST.NODE_WIDTH / 2) + (CONST.COLLAPSE_ICON_SIZE / 2) + CONST.SPACING + 20,
    fontSize: CONST.COLLAPSE_ICON_SIZE,
    text: collapseIcon
  });
};

export const onNodeUpdate = (node, nodeEnter) => {
  const nodeUpdate = node.merge(nodeEnter)
    .transition()
    .duration(CONST.DURATION)
    .attr('transform', (d) => `translate(${ d.y },${ d.x })`);

  updateText({ className: 'process-name', node: nodeUpdate, dx: 0, dy: '1em', opacity: 1 });
  updateText({ className: 'machine-count', node: nodeUpdate, dx: 0, dy: '2em', opacity: 1 });
};

export const onNodeExit = (node, source) => {
  const nodeExit = node.exit()
    .transition()
    .duration(CONST.DURATION)
    .attr('transform', () => `translate(${ source.y + CONST.NODE_WIDTH / 2 - 20},${ source.x })`)
    .remove();

  updateText({ className: 'process-name', dy: '-3em', node: nodeExit });
};

export const getNewNodes = (selectedNode, children) => {
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
};


export const updateStyle = (element, style) => {
  if (element) {
    element.style.display = style;
  }
};

export const documentTitle = (processName, queryInput) => {
  const { hn: hostName } = queryInput;
  return hostName ? `${hostName} - ${processName}` : `${processName}`;
};