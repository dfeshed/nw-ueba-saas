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
  const alphaLevelWas = this.get('alphaLevel');
  this.set('alphaCurrent', alphaCurrent);
  const alphaLevelIs = this.get('alphaLevel');
  const alphaLevelChanged = alphaLevelWas !== alphaLevelIs;

  const isDragging = this.get('isDragging');

  if (isDragging || alphaLevelIs <= 3) {
    this.joined.nodes
      .attr('transform', (d) => `translate(${d.x},${d.y})`)
      .selectAll('.circle')
        .attr('r', function(d) {
          return d.r;
        });
  }

  if (isDragging || alphaLevelIs <= 1) {
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

  if (!isDragging && alphaLevelIs === 0) {
    this.stop();
  }

  // If autoCenter is enabled, we center the graph each time the alpha level changes, EXCEPT if either
  // (a) the user is dragging a node; or
  // (b) the user has previously dragged a node.
  // If dragging is occuring or has occured, autoCenter could disrupt the user's workflow.
  if (this.get('autoCenter') && alphaLevelChanged && !isDragging && !this.get('dataHasBeenDragged') && !this.get('userHasZoomed')) {
    run(this, 'center');
  }
}