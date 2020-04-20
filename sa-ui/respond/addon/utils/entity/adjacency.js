/*
* Given a barebones nodes and links ( without the incomingLinks and outgoingLinks properties ), this method adds that
* adjacency information to the link-information to each of the nodes.
*
* This is useful for simplifying the data-creation for unit and component-integration testing.
* */
export default function createAdjacency(nodes, links) {
  nodes.forEach((n) => n.incomingLinks = n.outgoingLinks = []);
  links.forEach((l) => {
    l.source.outgoingLinks.push(l);
    l.target.incomingLinks.push(l);
  });
}