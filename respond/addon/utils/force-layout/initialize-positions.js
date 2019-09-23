import { max } from 'd3-array';

function _countGroups(nodes) {
  return max(nodes, (d) => d.ccGroup);
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
  const initialRadius = 10;
  const initialAngle = Math.PI * (3 - Math.sqrt(5));

  const groupInfos = [];
  const numGroups = _countGroups(nodes);
  const radius = 500;

  // The centers (x,y) of the groups are arranged in a circle around 0,0 with radius 500. nodeIndex is a property that
  // will be incremented as we process nodes belonging to a particular group and is used to calculate the
  // phyllotaxis radius and angle.
  for (let i = 0; i < numGroups; i++) {
    groupInfos.push({
      nodeIndex: 0,
      x: radius * Math.cos(2 * Math.PI * i / numGroups),
      y: radius * Math.sin(2 * Math.PI * i / numGroups)
    });
  }

  let node;

  for (let i = 0; i < nodes.length; ++i) {
    node = nodes[i];
    const groupInfo = groupInfos[(node.ccGroup) - 1];

    const nodeRadius = initialRadius * Math.sqrt(groupInfo.nodeIndex);
    const nodeAngle = groupInfo.nodeIndex * initialAngle;

    node.x = nodeRadius * Math.cos(nodeAngle) + groupInfo.x;
    node.y = nodeRadius * Math.sin(nodeAngle) + groupInfo.y;

    groupInfo.nodeIndex++;
  }

  return groupInfos;
}