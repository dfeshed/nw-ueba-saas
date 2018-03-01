import { run } from '@ember/runloop';
import linkCoords from 'respond/utils/force-layout/link-coords';

/**
 * Default tick handler used by the `rsa-fast-force` component.
 * @assumes The function is invoked with its `this` context assigned to the component instance.
 * @assumes The component's `joined` property contains an object with d3 selections `{ nodes, links }`.  Typically this
 * object is created by the component's configurable `dataJoin` function.
 * @public
 */
export default function() {

  const alphaCurrent = this.simulation.alpha();
  this.set('alphaCurrent', alphaCurrent);

  const isDragging = this.get('isDragging');
  const arrowWidth = this.get('arrowWidth');

  if (!this.get('shouldHideNodes')) {
    this.joined.nodes
      .attr('transform', (d) => `translate(${d.x},${d.y})`);
  }

  if (!this.get('shouldHideLinks')) {
    this.joined.links
      .each((d) => {
        const { source, target } = d;
        d.coords = linkCoords(
          source.x,
          source.y,
          source.r,
          target.x,
          target.y,
          target.r,
          arrowWidth
        );
      })
      .attr('transform', (d) => `translate(${d.source.x},${d.source.y})`)
      .select('.line')
      .attr('x1', (d) => d.coords.x1)
      .attr('y1', (d) => d.coords.y1)
      .attr('x2', (d) => d.coords.x2)
      .attr('y2', (d) => d.coords.y2);

    this.joined.links
      .select('.text')
      .attr('transform', (d) => d.coords.textTransform);

    this.joined.links
      .select('use')
      .attr('transform', (d) => d.coords.arrowTransform);
  }

  const shouldStop = !isDragging && (alphaCurrent <= this.get('alphaStop'));

  // Determine if we should auto-center the nodes visually. Don't want to disrupt the user's workflow.
  const alphaMagnitudeWas = this._lastAlphaMagnitude;
  const alphaMagnitudeIs = (Math.log(alphaCurrent) / Math.log(10)).toFixed(0);
  const alphaChangedSignificantly = alphaMagnitudeWas !== alphaMagnitudeIs;
  if (alphaChangedSignificantly) {
    this._lastAlphaMagnitude = alphaMagnitudeIs;
  }

  const shouldAutoCenter = this.get('autoCenter') &&
    (shouldStop || alphaChangedSignificantly) &&  // simulation is done or has cooled substantially
    !this.get('dataHasBeenDragged') &&  // user has not dragged
    !this.get('userHasZoomed');         // user has not manually zoomed

  if (shouldAutoCenter) {
    // For auto center, use a slower duration than the default so we don't startle the user.
    run(this, 'center', null, 700);
  }

  if (shouldStop) {
    this.stop();
  }
}
