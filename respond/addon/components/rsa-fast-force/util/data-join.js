import { drag } from 'd3-drag';
import { event } from 'd3-selection';
import run from 'ember-runloop';
import { dasherize } from 'ember-string';
import NodeTypes from 'respond/utils/entity/node-types';

const MAX_TEXT_LENGTH = 30;

// Helper that truncates a text string if it is larger than a given limit.
// If the given type is a file hash, the text is truncated as "abcdefgh ... tuvwvxyz";
// otherwise, it is simply truncated after the first several characters and appended with "...".
function truncateText(text, type) {
  const len = (text || '').length;
  if (len <= MAX_TEXT_LENGTH) {
    return text;
  } else if (String(type) === NodeTypes.FILE_HASH) {
    return `${text.substr(0, 8)} ... ${text.substr(-8)}`;
  } else {
    return `${text.substr(0, MAX_TEXT_LENGTH)}...`;
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
    .data(data.nodes, function(d) {
      return d.id;
    });

  // Remove node DOM for exiting data.
  nodesAll.exit().remove();

  // Build node DOM for entering data.
  const nodesEnter = nodesAll.enter();
  const nodesEnterGroup = nodesEnter.append('g')
    .attr('class', (d) => `rsa-force-layout-node ${dasherize(d.type || '')}`);
  nodesEnterGroup.append('circle')
    .attr('class', 'circle')
    .attr('cx', 0)
    .attr('cy', 0)
    .attr('r', this.get('nodeMinRadius'));
  nodesEnterGroup.append('text')
    .attr('class', 'text')
    .attr('x', 0)
    .attr('y', 0)
    .attr('dy', '0.35em')
    .text((d) => truncateText(d.text, d.type));
  nodesEnterGroup.append('title')
    .text((d) => d.text);
  nodesEnterGroup.call(
    drag()
      .on('start', run.bind(this, dragstarted))
      .on('drag', run.bind(this, dragged))
      .on('end', run.bind(this, dragended))
  );

  // Now update the radii of all the nodes.
  const nodesUpdate = nodesEnterGroup.merge(nodesAll);
  nodesUpdate.select('.circle').transition().duration(500)
    .attr('r', function(d) {
      return d.r;
    });

  const linksAll = this.linksLayer
    .selectAll('.rsa-force-layout-link')
    .data(data.links, function(d) {
      return d.id;
    });

  // Remove link DOM for exiting data.
  linksAll.exit().remove();

  // Build link DOM for entering data.
  const linksEnter = linksAll.enter();
  const linksEnterGroup = linksEnter.append('g')
    .attr('class', (d) => `rsa-force-layout-link ${dasherize(d.type || '')}`);
  linksEnterGroup.append('line')
    .attr('class', 'line')
    .attr('x1', 0)
    .attr('y1', 0)
    .attr('x2', 0)
    .attr('y2', 0);
  const { arrowWidth, arrowHeight } = this.getProperties('arrowWidth', 'arrowHeight');
  linksEnterGroup.append('use')
    .attr('xlink:href', '#force-layout__arrow')
    .attr('x', 0)
    .attr('y', -1 * (arrowHeight / 2))
    .attr('width', arrowWidth)
    .attr('height', arrowHeight);
  linksEnterGroup.append('text')
    .attr('class', 'text')
    .attr('x', 0)
    .attr('y', 0)
    .attr('dy', '-0.2em')
    .text((d) => d.text);
  linksEnterGroup.append('title')
    .text((d) => d.text);

  // Now update the stroke widths of all the links.
  const linksUpdate = linksEnterGroup.merge(linksAll);
  linksUpdate.select('.line')
    .style('stroke-width', (d) => {
      return `${d.stroke}px`;
    });

  return {
    nodes: nodesUpdate,
    links: linksUpdate
  };
}
