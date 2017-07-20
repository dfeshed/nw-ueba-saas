import radToDeg from 'respond/utils/math/rad-to-deg';
import { svgTranslation } from 'respond/helpers/svg/translation';
import { svgRotation } from 'respond/helpers/svg/rotation';

/**
 * Given the position and radii of two nodes, computes the start & end coordinates for a link that connects those nodes.
 * The link is essentially a line from the start node's center to the target node's center, EXCEPT that the line starts &
 * stops at the nodes' borders, not at their centers.
 * @param {number} sourceX The X coordinate of the source node.
 * @param {number} sourceY The Y coordinate of the source node.
 * @param {number} sourceR The radius of the source node.
 * @param {number} targetX The X coordinate of the target node.
 * @param {number} targetY The Y coordinate of the target node.
 * @param {number} targetR The radius of the target node.
 * @returns {{x1, x2, y1, y2, textTransform}} An object with properties that specify the link's start coordinates,
 * end coordinates, and an SVG transform for positioning text at the center of the link.
 * @public
 */
export default function forceLayoutLinkCoords(sourceX, sourceY, sourceR, targetX, targetY, targetR) {
  const rad = Math.atan2(targetY - sourceY, targetX - sourceX) || 0;
  const dr = Math.sqrt(Math.pow(targetX - sourceX, 2) + Math.pow(targetY - sourceY, 2));
  const x1 = sourceR * Math.cos(rad) || 0;
  const y1 = sourceR * Math.sin(rad) || 0;
  const x2 = Math.max(0, dr - targetR) * Math.cos(rad) || 0;
  const y2 = Math.max(0, dr - targetR) * Math.sin(rad) || 0;
  const deg = radToDeg(rad);
  const lineLength = Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
  const textOffset = sourceR + lineLength / 2;
  const textTransform = `${svgRotation(deg)} ${svgTranslation(textOffset, 0)}`;
  const arrowOffset = sourceR + lineLength;
  const arrowTransform = `${svgRotation(deg)} ${svgTranslation(arrowOffset, 0)}`;
  return {
    sourceX,
    sourceY,
    x1: x1.toFixed(1),
    x2: x2.toFixed(1),
    y1: y1.toFixed(1),
    y2: y2.toFixed(1),
    textTransform,
    arrowTransform
  };
}
