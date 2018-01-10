import { transition } from 'd3-transition';
import { easeCubicInOut } from 'd3-ease';
import { zoomIdentity } from 'd3-zoom';
import boundingBox from 'respond/utils/force-layout/bounding-box';

function applyCentering(i, isCentering) {
  if (this.isDestroyed || this.isDestroying) {
    return;
  }
  if (i === 0) {  // only do this once per transition, not once per data point
    this.set('isCentering', isCentering);
  }
}

// Maximum zoom factor that should be used to center nodes.
// Helpful for cosmetic reasons; sometimes zooming too large looks awkward.
const MAX_ZOOM = 1;

function roundUpToNearestTen(x) {
  return Math.ceil((x + 1) / 10) * 10;
}

/**
 * Visually centers the nodes within the current `fitToSize` rectangle.
 *
 * Uses the coordinates in the `data.nodes` array to determine the coordinates of a "bounding box" that contains
 * all the nodes. Then pans & zooms the display so that this bounding box will fit, centered, in the rectangle
 * specified by the current `fitToSize` property value.
 *
 * To implement the pan & zoom, leverages the `this.zoomBehavior` of this component, which is assumed to exist.
 * @assumes This component's `this.zoomBehavior` applies to the component's `this.svg`, both of which are presumably defined.
 *
 * @param {function|string} [easing=d3.easeCubicInOut] Specifies the easing to use when applying the transform.
 * If set explicitly to 'none', no easing is used and the transform is applied immediately.
 * @param {Number} [duration=300] Specifies duration of transition to use when applying the transform. If set
 * explicitly to 0, no transition is used and the transform is applied immediately.
 * @public
 */
export default function(easing, duration = 300) {
  easing = easing || easeCubicInOut;

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

  // Compute the coords of the "bounding box" that contains all the nodes.
  const box = boundingBox(
    this.get('data.nodes'),
    this.get('nodeMaxStrokeWidth'),
    width,
    height
  );

  // The transform will first apply a scale to ensure that the nodes' bounding box fits within `fitToSize`.
  const scale = (fitToWidth && fitToHeight) ? Math.min(MAX_ZOOM, fitToWidth / box.width, fitToHeight / box.height) : 1;

  // The transform will then apply a translation to center the nodes within the `fitToSize` rectangle.
  // Find the distance between the `fitToSize` rectangle's center point and the center point of the (scaled) bounding box.
  const diff = {
    x: fitToCenter - box.center * scale,
    y: fitToMiddle - box.middle * scale
  };
  // Our transform should translate the content across this physcal distance, but it will be applied AFTER the scale,
  // so we need to divide by the same scale amount in order to compensate for the distortion.
  const translation = {
    x: diff.x / scale,
    y: diff.y / scale
  };

  if (isNaN(translation.x) || isNaN(translation.y)) {
    return;
  }

  // Create a transform object with the computed scale & translation.
  const transform = zoomIdentity
    .scale(scale)
    .translate(translation.x, translation.y);

  // Apply the transform to the selection that the zoom behavior is wired to, possibly with a transition.
  let selection = this.svg;
  const useTransition = !!transition && (easing !== 'none') && !!duration;
  if (!useTransition) {

    // Stop any previous transition that may still be on-going.
    selection.interrupt();

  } else {
    // Overwrites any previous transition that may still be on-going.
    selection = selection.transition()
      .ease(easing)
      .duration(duration)
      .on('end interrupt', (d, i) => {
        const isCentering = false;
        applyCentering.call(this, i, isCentering);
      })
      .on('start', (d, i) => {
        const isCentering = true;
        applyCentering.call(this, i, isCentering);
      });
  }

  if (!useTransition) {
    this.set('isCentering', true);
  }
  this.zoomBehavior.transform(selection, transform);
  if (!useTransition) {
    this.set('isCentering', false);
  }
}