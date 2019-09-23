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

  const {
    isDragging,
    arrowWidth,
    shouldShowNodes,
    shouldShowLinks,
    alphaStop,
    autoCenter,
    dataHasBeenDragged,
    userHasZoomed
  } = this.getProperties(
    'isDragging',
    'arrowWidth',
    'shouldShowNodes',
    'shouldShowLinks',
    'alphaStop',
    'autoCenter',
    'dataHasBeenDragged',
    'userHasZoomed'
  );

  const alphaCurrent = this.simulation.alpha();
  this.set('alphaCurrent', alphaCurrent);

  if (shouldShowNodes) {
    this.joined.nodes
      .attr('transform', (d) => `translate(${d.x},${d.y})`);
  }

  const shouldStop = !isDragging && (alphaCurrent <= alphaStop);
  this.set('shouldShowLinkText', !isDragging && shouldStop);
  if (shouldShowLinks) {
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

  // Determine if we should auto-center the nodes visually. Don't want to disrupt the user's workflow.
  const alphaMagnitudeWas = this._lastAlphaMagnitude;
  const alphaMagnitudeIs = (Math.log(alphaCurrent) / Math.log(10)).toFixed(0);
  const alphaChangedSignificantly = alphaMagnitudeWas !== alphaMagnitudeIs;
  if (alphaChangedSignificantly) {
    this._lastAlphaMagnitude = alphaMagnitudeIs;
  }

  const shouldAutoCenter = autoCenter &&
    (shouldStop || alphaChangedSignificantly) && // simulation is done or has cooled substantially
    !dataHasBeenDragged && // user has not dragged
    !userHasZoomed; // user has not manually zoomed

  if (shouldAutoCenter) {
    // For auto center, use a slower duration than the default so we don't startle the user.
    run(this, 'center', null, 700);
  }

  if (shouldStop) {
    this.stop();
  }
}
