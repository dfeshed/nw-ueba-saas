import { histogram, max } from 'd3-array';
import _ from 'lodash';

/*
  * The centers (x,y) of the groups are arranged in a circle around 0,0 with radius globalRadius.
  * */
function _initializeGroupInfos(nodes) {
  const RAD = 1000;
  const numGroups = max(nodes, (d) => d.ccGroup);
  const groupInfos = [];
  const globalRadius = numGroups === 1 ? 0 : RAD;
  const groupRadius = numGroups === 1 ? RAD - 50 : RAD * Math.sin(Math.PI / numGroups) - 50;

  for (let i = 0; i < numGroups; i++) {
    groupInfos.push({
      groupRadius,
      x: globalRadius * Math.cos(2 * Math.PI * i / numGroups),
      y: globalRadius * Math.sin(2 * Math.PI * i / numGroups)
    });
  }
  return groupInfos;
}

/*
* Find the high degree nodes, given a set of nodes
* */
function _computeHighDegreeNodes(nodes) {
  const BIN_THRESHOLD = 9;
  const connectivity = (n) => n.incomingLinks.length + n.outgoingLinks.length;

  const hist = histogram().value(connectivity);
  return hist(nodes).filter((bin) => bin.length > 0 && bin.x0 > BIN_THRESHOLD).flat();
}

/*
* Initialize regular ( low-degree ) nodes as a spiral around the group center
* */
function _initRegular(nodes, groupInfo) {
  const INITIAL_RADIUS = 10;
  const INITIAL_ANGLE = Math.PI * (3 - Math.sqrt(5));

  for (let i = 0; i < nodes.length; ++i) {
    const nodeRadius = INITIAL_RADIUS * Math.sqrt(i);
    const nodeAngle = INITIAL_ANGLE * i;
    nodes[i].x = nodeRadius * Math.cos(nodeAngle) + groupInfo.x;
    nodes[i].y = nodeRadius * Math.sin(nodeAngle) + groupInfo.y;
  }
}

/*
* Initialize the high-degree nodes on the boundary of the group-circle.
* */
function _initHighDegree(nodes, groupInfo) {
  const numNodes = nodes.length;

  for (let i = 0; i < numNodes; i++) {
    nodes[i].fx = groupInfo.groupRadius * Math.cos(2 * Math.PI * i / numNodes) + groupInfo.x;
    nodes[i].fy = groupInfo.groupRadius * Math.sin(2 * Math.PI * i / numNodes) + groupInfo.y;
  }
}

function _init(nodes) {
  const groupInfos = _initializeGroupInfos(nodes);
  const groupedNodes = Object.values(_.groupBy(nodes, (n) => n.ccGroup));

  groupedNodes.forEach((nodesInAGroup) => {
    const groupInfo = groupInfos[nodesInAGroup[0].ccGroup - 1];

    // initialize regular node positions
    _initRegular(nodesInAGroup, groupInfo);

    // initialize high degree node positions
    const highDegreeNodes = _computeHighDegreeNodes(nodesInAGroup);
    _initHighDegree(highDegreeNodes, groupInfo);
  });

  return groupInfos;
}

/*
* Based on the initializeNodes() function in d3-force/simulation.js
*
* Takes into account disjoint groups of the nodes, and initializes each group in the same phyllotaxis arrangement, but
* around different centers.
* */
export default function initializePositions(nodes) {
  if (!nodes.length) {
    return false;
  }

  // reset the original positions set by previous iterations.
  nodes.forEach((n) => n.x = n.y = n.fx = n.fy = null);

  const groupInfos = _init(nodes);

  return {
    nodes,
    groupInfos
  };
}