import { transition } from 'd3-transition';
import { easeCubicInOut } from 'd3-ease';
import boundingBox from 'respond/utils/force-layout/bounding-box';

// Maximum zoom factor that should be used to center nodes.
// Helpful for cosmetic reasons; sometimes zooming too large looks awkward.
const MAX_ZOOM = 1;

function roundUpToNearestTen(x) {
  return Math.ceil((x + 1) / 10) * 10;
}

/**
 * Visually centers the rendered nodes within the SVG container, using an SVG transform with a configurable transition.
 *
 * Computes a center using all the nodes in `this.get('data.nodes')` which is assumed to be defined & up to date.
 * Implements the centering by applying an SVG transform to an SVG element (<g>) that contains all the SVG
 * nodes and links. Assumes that element is stored as `this.centeringElement`; otherwise if that property value
 * is not defined, this method does nothing.
 *
 * @param {function|string} [easing=d3.easeCubicInOut] Specifies the easing to use when applying the transform.
 * If set explicitly to 'none', no easing is used and the transform is applied immediately.
 * @param {Number} [duration=300] Specifies duration of transition to use when applying the transform. If set
 * explicitly to 0, no transition is used and the transform is applied immediately.
 * @public
 */
export default function(easing = easeCubicInOut, duration = 300) {
  let el = this.centeringElement;
  if (!el) {
    return;
  }

  // Determine the box size & position to fit into.  Round the sizes up to the nearest tens, in order to avoid
  // unseemly visual "bouncing" that is caused by minor variances in the nodes' coordinates.
  const width = roundUpToNearestTen(this.element.clientWidth || 0);
  const height = roundUpToNearestTen(this.element.clientHeight || 0);
  if (!width || !height) {
    return;
  }
  const fitToSize = this.get('fitToSize') || {};
  const fitToWidth = roundUpToNearestTen(fitToSize.width) || width;
  const fitToHeight = roundUpToNearestTen(fitToSize.height) || height;
  const fitToCenter = (fitToSize.left + fitToWidth / 2) || (width / 2);
  const fitToMiddle = (fitToSize.top + fitToHeight / 2) || (height / 2);

  // The transform will include a scale in order to ensure that the nodes' bounding box fits within `fitToSize`.
  const box = boundingBox(
    this.get('data.nodes'),
    this.get('nodeMaxStrokeWidth'),
    width,
    height
  );
  const k = (fitToWidth && fitToHeight) ? Math.min(MAX_ZOOM, fitToWidth / box.width, fitToHeight / box.height) : 1;

  // Cache the resulting zoom scale for future reference.
  this.set('zoom', Number(k).toFixed(1));

  // Compose an SVG transform that will:
  const transform = [
    // (1) move the center of the bounding box to the origin (0,0)
    `translate(${[(-1 * box.center).toFixed(3), (-1 * box.middle).toFixed(3)]})`,
    // (2) apply the scale factor
    `scale(${k.toFixed(3)})`,
    // (3) move the bounding box to the center of the `fitToSize` rectangle (or, if undefined, center of component).
    `translate(${[ fitToCenter.toFixed(3), fitToMiddle.toFixed(3)]})`
  ].reverse().join(' ');  // Reverse the array order because SVG transforms are applied right to left.

  // Apply the transform to SVG, possibly with a transition.
  if (transition && ((easing === 'none') || !duration)) {
    el.interrupt();     // stops any previous transition that may still be on-going
  } else {
    el = el.transition()  // overwrites any previous transition that may still be on-going
      .ease(easing)
      .duration(duration);
  }
  el.attr('transform', transform);
}