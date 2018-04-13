import { truncateText, getRiskScoreClassName } from '../util/data';

const ELBOW_CURVE = 140;
/**
 * Custom path function that connects node with lines. Calculate start and end position of link. Elbow function uses
 * the svg Paths to draw a path between the two nodes. Need to attach the curve to middle of the rectangle node and
 * should start from some distance to add collapse button
 * @param d current node
 * @returns {string}
 * @private
 */
export const elbow = function(d, nodeWidth, distanceFromNode) {
  return `M${ d.y - (nodeWidth / 2) },${ d.x}
            C${ d.parent.y + ELBOW_CURVE},
            ${ d.x} ${ d.parent.y + ELBOW_CURVE },
            ${ d.parent.x} ${ d.parent.y + distanceFromNode },
            ${ d.parent.x}`;
};


/**
 * Use a different elbow function for enter and exit nodes. This is necessary because the function above assumes
 * that the nodes are stationary along the x axis.
 * @private
 */
export const transitionElbow = function(d) {
  return `M${ d.source.y },${ d.source.x}
            C${ d.target.y },${ d.source.x} 
             ${ d.target.y },${ d.target.x} 
             ${ d.target.y },${ d.target.x}`;
};

/**
 * Helper method for updating the tree rectangle node on 'update' event. It set's proper height and width for the tree node
 * Also it set's the radius for the corner
 * @param node
 * @param width
 * @param height
 * @param rx
 * @param ry
 * @param x
 * @param y
 * @public
 */
export const updateRect = function({ node, width = 0, height = 0, rx = 0, ry = 0, x = 0, y = 0 }) {
  node.select('rect')
    .attr('rx', rx)
    .attr('ry', ry)
    .attr('width', width)
    .attr('height', height)
    .attr('x', x)
    .attr('y', y);
};

/**
 * Append rectangle tree node the SVG also it add's the risk score class based the score
 * @param node
 * @param className
 * @param width
 * @param height
 * @param x
 * @param y
 * @param rx
 * @param ry
 * @public
 */
export const appendRect = function({ node, className = 'node-rect', width = 0, height = 0, x = 0, y = 0, rx = 0, ry = 0 }) {
  node.append('rect')
    .attr('class', (d) => `${className} ${getRiskScoreClassName(d.data.riskScore)}`)
    .attr('width', width)
    .attr('height', height)
    .attr('rx', rx)
    .attr('ry', ry)
    .attr('x', x)
    .attr('y', y);
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
export const appendText = function({ node, dy, dx, text, className, anchor, opacity }) {
  node.append('text')
    .attr('class', className)
    .attr('dx', dx)
    .attr('dy', dy)
    .attr('text-anchor', anchor)
    .style('fill-opacity', opacity)
    .text(truncateText(text));
};
/**
 * Updates the text property on 'update' event
 * @param node
 * @param className
 * @param dy
 * @param dx
 * @param opacity
 * @public
 */
export const updateText = function({ node, className, dy, dx = 0, opacity = 0 }) {
  node.select(`.${className}`)
    .attr('dy', dy)
    .attr('dx', dx)
    .style('fill-opacity', opacity);

};


