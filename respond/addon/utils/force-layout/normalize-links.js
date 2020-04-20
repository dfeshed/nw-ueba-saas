const ATTRIBUTE_LINKS = ['is named', 'as', 'belongs to', 'has file'];

/*
* We only want 'attribute' nodes to be close to each other, NOT relationships like 'communicates with'
* */
function _isAttributeLink(link) {
  return ATTRIBUTE_LINKS.includes(link.type);
}

/*
*
* adjust link distances for single-degree "leaf" nodes, so that they are close to their neighbour. This reduces the
* chances of having overlapping links, and make the association more obvious.
*
* */
export default function normalizeLinks(links) {
  if (!links.length) {
    return;
  }

  links.forEach((link) => {
    if (link.source.incomingLinks && link.source.outgoingLinks && link.target.incomingLinks && link.target.outgoingLinks) {
      const sourceDegree = link.source.incomingLinks.length + link.source.outgoingLinks.length;
      const targetDegree = link.target.incomingLinks.length + link.target.outgoingLinks.length;
      if (_isAttributeLink(link) && (sourceDegree === 1 || targetDegree === 1)) {
        // have a buffer of 50 so that link text has some space to be visible
        link.linkDistance = link.source.r + link.target.r + 25;
      }
    }
  });
}