/**
 * Default function used to render a "filter" on the DOM of the `rsa-fast-force` component.
 * @assumes The function is invoked with its `this` context assigned to the component instance.
 * @assumes The component has `this.joined.nodes` and `this.joined.links` properties, each of which are d3 selections
 * that point to the nodes DOM elements & links DOM elements, respectively.
 *
 * @public
 */
export default function() {
  const { nodes, links } = this.joined || {};

  if (nodes) {
    nodes.classed('is-hidden', (d) => d.isHidden);
  }

  if (links) {
    links.classed('is-hidden', (d) => d.isHidden)
      .select('use')
      .attr('href', (d) => d.isHidden ? '#force-layout__arrow--disabled' : '#force-layout__arrow');
  }
}
