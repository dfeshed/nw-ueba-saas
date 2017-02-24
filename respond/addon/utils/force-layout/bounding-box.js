/**
 * Computes the bounding box of a given set of nodes with a given maximum stroke width.
 *
 * The "bounding box" refers to the smallest rectangle which contains the given nodes.
 * Any nodes with an optional `isHidden` property set to truthy will be excluded from the calculation.
 * Assumes each of the (non-hidden) nodes has `x`, `y` & `r` properties specifying its position and radius.
 *
 * @param {object[]} nodes An array of objects, each of which has `x`, `y` & `r` numeric properties.
 * @param {number} maxStrokeWidth The maximum possible stroke width for any of the nodes. Used as padding in the
 * calculation, in order to avoid collisions with the nodes' strokes.
 * @param {number} defaultWidth Default width for the bounding box. Only used if no node data can be determined.
 * @param {number} defaultHeight Default height for the bounding box. Only used if no node data can be determined.
 *
 * @returns {{ width: number, height: number, top: number, bottom: number, left: number, right: number, center: number, middle: number }}
 * @public
 */
export default function forceLayoutBoundingBox(nodes, maxStrokeWidth = 0, defaultWidth = 0, defaultHeight = 0) {
  const box = {
    top: 0,
    bottom: defaultHeight,
    left: 0,
    right: defaultWidth
  };

  const visible = (nodes || []).rejectBy('isHidden');
  if (visible.length) {
    const first = visible.popObject();
    box.left = first.x - first.r - maxStrokeWidth;
    box.right = first.x + first.r + maxStrokeWidth;
    box.top = first.y - first.r - maxStrokeWidth;
    box.bottom = first.y + first.r + maxStrokeWidth;

    visible.forEach((node) => {
      box.left = Math.min(box.left, node.x - node.r - maxStrokeWidth);
      box.right = Math.max(box.right, node.x + node.r + maxStrokeWidth);
      box.top = Math.min(box.top, node.y - node.r - maxStrokeWidth);
      box.bottom = Math.max(box.bottom, node.y + node.r + maxStrokeWidth);
    });
  }

  box.center = (box.left + box.right) / 2;
  box.middle = (box.top + box.bottom) / 2;
  box.width = box.right - box.left;
  box.height = box.bottom - box.top;

  return box;
}
