const COUNT_OFFSET = 55;
/**
 * Custom path function that connects node with lines. Calculate start and end position of link. Elbow function uses
 * the svg Paths to draw a path between the two nodes. Need to attach the curve to middle of the rectangle node and
 * should start from some distance to add collapse button
 * @param d current node
 * @returns {string}
 * @private
 */
export const elbow = function(d, boxWidth) {
  const sourceX = d.x;
  const sourceY = d.y + (boxWidth / 2);
  const targetX = d.parent.x;
  const targetY = d.parent.y - (boxWidth / 2);

  return `M${sourceY - boxWidth},${sourceX}
          H${ sourceY + (targetY - sourceY) / 2}
          V${ targetX}
          H${ targetY + boxWidth + COUNT_OFFSET}`;
};

/**
 * Use a different elbow function for enter
 * and exit nodes. This is necessary because
 * the function above assumes that the nodes
 * are stationary along the x axis.
 * @public
 */
export const transitionElbow = function(d) {
  return `M${ d.source.y },${ d.source.x }H${ d.source.y }V${ d.source.x }H${ d.source.y }`;
};

export const appendIcon = function({ node, fontSize, className, opacity = 1, text, dx = 0, dy = 0 }) {
  return node.append('text')
    .attr('class', className)
    .style('fill-opacity', opacity)
    .attr('text-anchor', 'middle')
    .attr('dominant-baseline', 'central')
    .attr('dx', dx)
    .attr('dy', dy)
    .attr('font-family', 'nw-icon-library-all-1')
    .attr('font-size', fontSize)
    .text(text);
};

/**
 * Append text to SVG element
 * @param node
 * @param dy
 * @param dx
 * @param text
 * @param className
 * @param anchor
 * @param opacity
 * @public
 */
export const appendText = function({ node, dy, dx, className, anchor, opacity, text }) {
  node.append('text')
    .attr('class', className)
    .attr('dx', dx)
    .attr('dy', dy)
    .attr('dominant-baseline', 'central')
    .attr('text-anchor', anchor)
    .style('fill-opacity', opacity)
    .text(text);
};
/**
 * Updates the text property on 'update' event
 * @param node
 * @param className
 * @param dy
 * @param dx
 * @param opacity
 * @param anchor
 * @param fill
 * @public
 */
export const updateText = function({ node, className, dy, dx = 0, opacity = 0, anchor = 'middle', fill = 'rgba(155,155,155,.8)' }) {
  node.select(`.${className}`)
    .attr('dy', dy)
    .attr('dx', dx)
    .attr('fill', fill)
    .attr('text-anchor', anchor)
    .style('fill-opacity', opacity);
};


