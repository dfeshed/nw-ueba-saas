import Ember from 'ember';
import { drag } from 'd3-drag';
import { event } from 'd3-selection';

const { run } = Ember;

const MAX_TEXT_LENGTH = 30;

// Helper that truncates a text string if it is larger than a given limit and appends "..".
function truncateText(text) {
  const len = (text || '').length;
  if (len <= MAX_TEXT_LENGTH) {
    return text;
  } else {
    return `${text.substr(0, MAX_TEXT_LENGTH)}..`;
  }
}

// Drag handlers for newly created DOM.
// When wired up, these will have `this` bound to the component instance.
function dragstarted(d) {
  this.set('isDragging', true);
  if (!event.active) {
    this.simulation.alphaTarget(0.0075).restart();
  }
  d.fx = d.x;
  d.fy = d.y;
}
function dragged(d) {
  d.fx = event.x;
  d.fy = event.y;
}
function dragended() {
  if (!event.active) {
    this.simulation.alphaTarget(0);
  }
  this.setProperties({
    isDragging: false,
    dataHasBeenDragged: true
  });
}

/**
 * Default function used to perform a d3 data join for the `rsa-fast-force` component.
 * @assumes The function is invoked with its `this` context assigned to the component instance.
 * @assumes The component has `this.nodesLayer` and `this.linksLayer` properties, each of which are d3 selections
 * that point to the nodes container DOM element & links container DOM element, respectively.
 *
 * @returns {{ nodes: object, links: object }} An object with a pair of d3 selections. To be used by simulation tick handler.
 * @public
 */
export default function() {
  const data = this.get('data');

  const nodesAll = this.nodesLayer
    .selectAll('.rsa-force-layout-node')
    .data(data.nodes);

  // Remove node DOM for exiting data.
  nodesAll.exit().remove();

  // Build node DOM for entering data.
  const nodesEnter = nodesAll.enter();
  const nodesEnterGroup = nodesEnter.append('g')
    .attr('class', (d) => `rsa-force-layout-node ${d.type}`);
  nodesEnterGroup.append('circle')
    .attr('class', 'circle')
    .attr('cx', 0)
    .attr('cy', 0)
    .attr('r', (d) => d.r);
  nodesEnterGroup.append('text')
    .attr('class', 'text')
    .attr('x', 0)
    .attr('y', 0)
    .attr('dy', '0.35em')
    .text((d) => truncateText(d.text));
  nodesEnterGroup.append('title')
    .text((d) => d.text);
  nodesEnterGroup.call(
    drag()
      .on('start', run.bind(this, dragstarted))
      .on('drag', run.bind(this, dragged))
      .on('end', run.bind(this, dragended))
  );

  const linksAll = this.linksLayer
    .selectAll('.rsa-force-layout-link')
    .data(data.links);

  // Remove link DOM for exiting data.
  linksAll.exit().remove();

  // Build link DOM for entering data.
  const linksEnter = linksAll.enter();
  const linksEnterGroup = linksEnter.append('g')
    .attr('class', (d) => `rsa-force-layout-link ${d.type}`);
  linksEnterGroup.append('line')
    .attr('class', 'line')
    .attr('x1', 0)
    .attr('y1', 0)
    .attr('x2', 0)
    .attr('y2', 0);
  linksEnterGroup.append('text')
    .attr('class', 'text')
    .attr('x', 0)
    .attr('y', 0)
    .attr('dy', '-0.2em')
    .text((d) => d.text);

  return {
    nodes: nodesEnterGroup.merge(nodesAll),
    links: linksEnterGroup.merge(linksAll)
  };
}