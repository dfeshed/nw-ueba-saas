import { event } from 'd3-selection';

export default function() {
  const { centeringElement } = this;
  if (!centeringElement) {
    return;
  }

  // Apply the zoom transform (i.e., translation + scale) to the SVG centering element.
  centeringElement.attr('transform', event.transform);

  // Also update the component's `zoom` attr, so we can use it in classNameBindings to enable zoom-specific CSS rules.
  this.set('zoom', event.transform.k.toFixed(1));
}
