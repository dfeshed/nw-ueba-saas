import { event } from 'd3-selection';

/**
 * Default zoom handler used by the `rsa-fast-force` component.
 *
 * @assumes The function is invoked with its `this` context assigned to the component instance.
 * @assumes The zoom transform should be applied to the d3 selection stored as `this.centeringElement`; otherwise if
 * that property value is not defined, this method does nothing.
 *
 * @public
 */
export default function() {
  const { centeringElement } = this;
  if (!centeringElement) {
    return;
  }

  // Apply the zoom transform (i.e., translation + scale) to the SVG centering element.
  centeringElement.attr('transform', event.transform);

  // Also update the component's `zoom` attr, so we can use it in classNameBindings to enable zoom-specific CSS rules.
  this.set('zoom', event.transform.k.toFixed(1));

  if (!this.get('isCentering')) {
    this.set('userHasZoomed', true);
  }
}