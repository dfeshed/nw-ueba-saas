import Ember from 'ember';
import linkCoords from 'respond/utils/force-layout/link-coords';

const { run } = Ember;

/**
 * Default tick handler used by the `rsa-fast-force` component.
 * @assumes The function is invoked with its `this` context assigned to the component instance.
 * @assumes The component's `joined` property contains an object with d3 selections `{ nodes, links }`.  Typically this
 * object is created by the component's configurable `dataJoin` function.
 *
 * @public
 */
export default function() {

  const alphaCurrent = this.simulation.alpha();
  const alphaLevelWas = this.get('alphaLevel');
  this.set('alphaCurrent', alphaCurrent);
  const alphaLevelIs = this.get('alphaLevel');
  const alphaLevelChanged = alphaLevelWas !== alphaLevelIs;

  if (alphaLevelIs <= 3) {
    this.joined.nodes
      .attr('transform', (d) => `translate(${d.x},${d.y})`);
  }

  if (alphaLevelIs <= 1) {
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

  if (alphaLevelIs === 0) {
    this.simulation.stop();
  }

  if (alphaLevelChanged) {
    const center = this.get('center');
    if (center) {
      run(this, center);
    }
  }
}