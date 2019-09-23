export default function connectedComponents(nodes) {
  if (!nodes.length) {
    return 0;
  }

  let i;
  let curGroup = 1;
  const numVertices = nodes.length;

  let corruptNodeCount = 0;
  for (i = 0; i < numVertices; ++i) {
    nodes[i].ccGroup = 0;
    if (!nodes[i].incomingLinks || !nodes[i].outgoingLinks) {
      corruptNodeCount++;
    }
  }

  // the node object does not have incomingLinks and outgoingLinks array properties which are required to find
  // the connected-components. Hence assume the data is a single connected group.
  if (corruptNodeCount > 0) {
    nodes.forEach((node) => node.ccGroup = 1);
    return 1;
  }

  for (i = 0; i < numVertices; ++i) {
    if (nodes[i].ccGroup !== 0) {
      continue;
    }
    const toVisit = [nodes[i]];
    nodes[i].ccGroup = curGroup;
    while (toVisit.length > 0) {
      const node = toVisit.pop();
      const neighbourhood = [...(node.incomingLinks.map((link) => link.source)), ...(node.outgoingLinks.map((link) => link.target))];
      for (let j = 0; j < neighbourhood.length; ++j) {
        const neighbour = neighbourhood[j];
        if (neighbour.ccGroup === 0) {
          neighbour.ccGroup = curGroup;
          toVisit.push(neighbour);
        }
      }
    }
    curGroup++;
  }
  // return the number of disjoint groups
  return curGroup - 1;
}