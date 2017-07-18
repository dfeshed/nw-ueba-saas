import Ember from 'ember';
import linkCoords from 'respond/utils/force-layout/link-coords';

const { run } = Ember;

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

  if (!this.get('shouldHideNodes')) {
    this.joined.nodes
      .attr('transform', (d) => `translate(${d.x},${d.y})`)
      .selectAll('.circle')
        .attr('r', function(d) {
          return d.r;
        });
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
          target.r
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
  }

  const shouldStop = !isDragging && (alphaCurrent <= this.get('alphaStop'));

  // Determine if we should auto-center the nodes visually. Don't want to disrupt the user's workflow.
  const alphaMagnitudeWas = this._lastAlphaMagnitude;
  const alphaMagnitudeIs = Math.round(alphaCurrent * 10);
  const alphaChangedSignificantly = alphaMagnitudeWas !== alphaMagnitudeIs;
  if (alphaChangedSignificantly) {
    this._lastAlphaMagnitude = alphaMagnitudeIs;
  }

  const shouldAutoCenter = this.get('autoCenter') &&
    (shouldStop || alphaChangedSignificantly) &&  // simulation is done or has cooled substantially
    !this.get('dataHasBeenDragged') &&  // user has not dragged
    !this.get('userHasZoomed');         // user has not manually zoomed

  if (shouldAutoCenter) {
    run(this, 'center');
  }

  if (shouldStop) {
    this.stop();
  }
}