import Ember from 'ember';
import computed from 'ember-computed-decorators';
import { forceSimulation, forceLink, forceManyBody, forceCollide, forceCenter } from 'd3-force';
import { select } from 'd3-selection';
import { zoom } from 'd3-zoom';
import dataJoin from './util/data-join';
import ticked from './util/ticked';
import zoomed from './util/zoomed';
import center from './util/center';

/* global addResizeListener */
/* global removeResizeListener */

const {
  Component,
  set,
  run
} = Ember;

/**
 * @class d3 Fast Force Layout component
 * A d3 force layout for rendering nodes and links, wrapped as an Ember component.
 * See d3 Force Layout docs for more details: https://github.com/mbostock/d3/wiki/Force-Layout
 *
 * This component is called "fast" because it uses canonical d3 techniques (i.e., imperative code) to manipulate DOM,
 * rather than relying on declarative techniques (i.e., Ember templates).
 *
 * @public
 */
export default Component.extend({

  // Wrap the SVG canvas in a div. The div can be set to any size, while the SVG element by default fills the div
  // with its width & height set to 100%.
  tagName: 'div',
  classNames: 'rsa-force-layout',
  classNameBindings: ['alphaLevelClass', 'isDragging'],
  attributeBindings: ['zoom:data-zoom'],

  @computed('alphaLevel')
  alphaLevelClass: ((alphaLevel) => `alpha-level-${alphaLevel}`),

  // d3 force layout configuration properties.
  // For details, see d3 API docs: https://github.com/mbostock/d3/wiki/Force-Layout
  charge: -1000,
  chargeDistance: 250,
  linkStrength: 0.5,
  linkDistance: 150,
  centerX: 700,
  centerY: 450,
  alphaInitial: 0.5,

  /**
   * The current alpha measurement of the simulation. This quantity is read from the d3 algorithm at run-time.
   *
   * The "alpha" value is a measure of a simulation's activity; low alpha means the simulation is settling into
   * a steady state of node coordinates; high alpha means the coordinates are changing significantly per iteration.
   *
   * @type {number}
   * @readonly
   * @private
   */
  alphaCurrent: 0,

  /**
   * A number between 0 (lowest) and 4 (highest) indicating the range of the simulation's current "alpha".
   *
   * @type {number}
   * @readonly
   * @private
   */
  @computed('alphaCurrent')
  alphaLevel(current) {
    let level = 0;
    if (current > 0.05) {
      level = 4;
    } else if (current > 0.01) {
      level = 3;
    } else if (current > 0.0075) {
      level = 2;
    } else if (current > 0.005) {
      level = 1;
    }
    return level;
  },

  /**
   * Configurable radius of the node circles. Assumes all node circles have the same radius.
   *
   * Used when:
   * (1) applying a default radius to the nodes data (only for nodes which are missing a radius);
   * (2) computing the endpoints of the links that connect circles (the endpoints lie on the circumferences of the
   * circles, not the centers of the circles); and
   * (3) for avoiding node collisions (overlap).
   *
   * @type number
   * @public
   */
  nodeRadius: 25,

  /**
   * Configurable maximum stroke width of the node circles.
   *
   * This should be to the largest stroke width that will be used for any node circles (e.g., if selected/highlighted
   * node circles get an extra thick stroke, use that stroke width here). This width is not applied in the DOM, but
   * rather is only used to compute the layout in order to avoid collisions.
   *
   * @type number
   * @public
   */
  nodeMaxStrokeWidth: 1,

  /**
   * Configurable lower limit on zoom scale.
   * The user will not be able shrink the graph smaller than this by using scrolling nor gestures.
   *
   * @type {number}
   * @public
   */
  zoomMin: 0.1,

  /**
   * Configurable upper limit on zoom scale.
   * The user will not be able enlarge the graph larger than this by using scrolling nor gestures.
   *
   * @type {number}
   * @public
   */
  zoomMax: 2,

  /**
   * The current SVG scale factor that is being applied. Read-only.
   *
   * When zooming is applied, this property is automatically updated with the current zoom scale value, to 1 decimal
   * precision.  It is exposed here as an attr in order to use it in an HTML attribute binding, so
   * we can write zoom-specific conditional CSS. For example, we can choose to enlarge/shrink font sizes based on zoom level.
   *
   * @type {number}
   * @readonly
   * @private
   */
  zoom: 1,

  /**
   * Indicates whether or not the user is currently panning and/or zooming the visualization.
   *
   * @type {number}
   * @readonly
   * @public
   */
  isDragging: false,

  /**
   * An object with `nodes` & `links` properties, which hold an array (possibly empty) of nodes & links (respectively)
   * that will be layed out by the d3 force-directed layout algorithm.
   *
   * As per the d3 force-layout spec, each node & link is an object with certain properties:
   * Each node is an object with `x`, `y` & `r` numeric properties.
   * Each link is an object with `source` & `target` properties, which point to members of `nodes`.
   *
   * Additionally, this component assumes that every node & link will have an `id` attr (type: string), and those ids
   * will be unique across all nodes & links in `data`. This requirement allows us to support some useful features
   * such as selections and object constancy.
   *
   * @type {{ nodes: object[], links: object[] }}
   * @public
   */
  @computed
  data: {
    get() {
      return this._data || { nodes: [], links: [] };
    },
    set(value = {}) {
      const was = this._data;
      if (was === value) {
        return was;
      }

      if (!value.nodes) {
        set(value, 'nodes', []);
      }
      if (!value.links) {
        set(value, 'links', []);
      }

      // Ensure all the given nodes (if any) have a radius.
      // Doing this here, rather than later, saves us from having to check for this later during the simulation.
      const radius = this.get('nodeRadius');
      value.nodes.forEach(function(node) {
        if (!node.r) {
          node.r = radius;
        }
      });

      this._data = value;

      // Must use `run.next` to ensure that when the called function calls `this.get('data')`, this function will
      // have already returned and Ember will have updated the `data` property value.
      run.next(this, '_dataDidChange');

      return this._data;
    }
  },

  /**
   * Configurable function responsible for removing DOM for exiting data, building DOM for entering data, and
   * updating DOM for updated data.
   *
   * Here "data" means the business data in nodes & links, but typically does not mean coordinates.
   * The coordinates are computed by this component's simulation, which will not yet have started when `dataJoin` is invoked.
   *
   * @assumes This function will be invoked with its `this` context set to this component instance.
   * @type {function}
   * @public
   */
  dataJoin,

  /**
   * Configurable d3 callback to be invoked each time a single frame of the simulation if computed.
   * See d3 Force Layout docs for more details: https://github.com/mbostock/d3/wiki/Force-Layout
   * Responsible for applying the latest coordinates from the simulation to DOM.
   *
   * @assumes This function will be invoked with its `this` context set to this component instance.
   * @type {function}
   * @public
   */
  ticked,

  /**
   * Configurable d3 callback to be invoked each time a d3 zoom event is emitted by user interaction.
   * See d3 zoom docs for more details: https://github.com/d3/d3-zoom
   * Responsible for applying the zoom transform to DOM.
   *
   * @assumes This function will be invoked with its `this` context set to this component instance.
   * @type {function}
   * @public
   */
  zoomed,

  /**
   * If truthy, instructs this visualization to center its nodes visually whenever either (a) this component is
   * resized, or (b) the simulation is (re-)run.
   *
   * @type {boolean}
   * @public
   */
  autoCenter: false,

  /**
   * Configurable callback that visually centers the nodes in the current `fitTo` rectangle.
   *
   * @assumes This function will be invoked with its `this` context set to this component instance.
   * @type {function}
   * @public
   */
  center,

  /**
   * Specifies whether or not the user has dragged the current `data`.
   *
   * This property will automatically be set to `true` after the user drag moves a node in the layout. If the `data`
   * is reset, then this property will automatically be reset to `false`.
   *
   * Initially the data is laid out programmatically by the simulation, but subsequently the user can drag-move nodes
   * if they want to. If they do, we must resume the simulation (but not from scratch, we just give it a nudge) to adjust
   * the layout as needed. Typically, if `autoCenter` is truthy, we re-center the graph once the simulation is done,
   * but if the simulation was restarted only because the user dragged data, then we shouldn't re-center once the
   * simulation re-finishes.  So this property will help us make that distinction.
   *
   * @readonly
   * @type {boolean}
   * @private
   */
  dataHasBeenDragged: false,

  // Triggers a restart of the layout algorithm when new data arrives.
  // Stops current simulation (if any), updates DOM then (re-)starts simulation.
  _dataDidChange() {
    this.stop();

    // Reset flag that tells us if user has manually dragged this dataset around.
    this.set('dataHasBeenDragged', false);

    const { simulation } = this;
    if (!simulation) {
      // component hasn't rendered yet, exit
      return;
    }

    // Join the data to the DOM using configurable function.
    // Optimization: Cache the result (if any) so that tick handler can use it subsequently.
    this.joined = this.get('dataJoin').apply(this, []);

    // Feed the data to the simulation & restart.
    const { nodes, links } = this.get('data');
    simulation
      .nodes(nodes)
      .force('link').links(links);

    if (nodes.length) {
      this.start();
    }
  },

  /**
   * Starts the current simulation with its current data set, if any.
   * Responsible for respecting the `alphaInitial` & `skipFirstFrames` settings.
   * @public
   */
  start() {
    const { simulation } = this;
    if (!simulation) {
      // component hasn't rendered yet, exit
      return;
    }
    simulation
      .alpha(this.get('alphaInitial'))
      .restart();

    // Skip some of the initial d3 force animation by executing the first frames' computations now, manually.
    for (let i = this.get('skipFirstFrames') || 0; i > 0; i--) {
      simulation.tick();
    }
  },

  // Creates a d3 force simulation with configurable forces.
  // Caches some frequently used DOM in component, for performance.
  _initSimulation() {
    const simulation = this.simulation = forceSimulation();

    const {
      linkDistance,
      linkStrength,
      charge,
      chargeDistance,
      centerX,
      centerY,
      nodeRadius,
      nodeMaxStrokeWidth
    } = this.getProperties(
      'linkDistance',
      'linkStrength',
      'charge',
      'chargeDistance',
      'centerX',
      'centerY',
      'nodeRadius',
      'nodeMaxStrokeWidth'
    );

    // Define a link force that pulls nodes together.
    if (linkDistance && linkStrength) {
      const linkForce = forceLink()
        .id((d) => d.id)
        .distance(linkDistance)
        .strength(linkStrength);
      simulation.force('link', linkForce);
    }

    // Define a charge force that can repel or attract nodes.
    if (charge && chargeDistance) {
      const chargeForce = forceManyBody()
        .strength(charge)
        .distanceMax(chargeDistance);
      simulation.force('charge', chargeForce);
    }

    // Define a force the centers the nodes around a given coordinate.
    if (!isNaN(centerX) && !isNaN(centerY)) {
      const centerForce = forceCenter(centerX, centerY);
      simulation.force('center', centerForce);
    }

    // Define a force that prevents node collisions (overlap).
    const collideForce = forceCollide()
      .radius(nodeRadius * 3 + nodeMaxStrokeWidth * 2);
    simulation.force('collide', collideForce);

    // Wire up tick handler that applies simulation coordinates to DOM.
    simulation.on('tick', run.bind(this, 'ticked'));

    // Optimization: Cache some frequently-used DOM.
    const el = select(this.element);
    this.svg = el.select('svg');
    this.linksLayer = el.select('.links-layer');
    this.nodesLayer = el.select('.nodes-layer');
    this.centeringElement = el.select('.centering-element');

    // Define a callback that responds to d3 "zoom events" by applying the event's pan & scale numbers to DOM.
    const zoomCallback = run.bind(this, 'zoomed');

    // Wire up a d3 "zoom behavior" that listens for scroll/pinch/drag gestures on our <svg> element, converting those
    // DOM events into d3 "zoom events" that we can then handle with our callback.
    this.zoomBehavior = zoom()
      .scaleExtent([
        this.get('zoomMin'),
        this.get('zoomMax')
      ])
      .on('start', () => {
        el.classed('is-panning', true);
      })
      .on('end', () => {
        el.classed('is-panning', false);
      })
      .on('zoom', zoomCallback);
    this.svg.call(this.zoomBehavior);

    // Manually kick off simulation if we have data already.
    this._dataDidChange();
  },

  /**
   * Stops the current simulation and resets current alpha property to reflect that the simulation is stopped.
   *
   * To stop the simulation, we should always call this method rather than directly calling `simulation.stop()`,
   * in order to ensure that `alphaCurrent` stays in sync.
   *
   * Unfortunately, we can't just attach a listener for simulation `end` events because `.stop()` doesn't trigger one.
   *
   * @public
   */
  stop() {
    if (this.simulation) {
      this.simulation.stop();
      this.set('alphaCurrent', 0);
    }
  },

  // Releases d3 simulation, clear cached DOM.
  _teardownSimulation() {
    this.stop();
    this.simulation = this.nodesLayer = this.linksLayer = this.centeringElement = this.svg = this.joined = this.zoomBehavior = null;
  },

  // Attaches resize event listener if auto-centering is enabled.
  _initResizer() {
    if (this.get('autoCenter')) {
      this._resizeCallback = () => {
        run.throttle(this, 'center', 250, false);
      };
      addResizeListener(this.element, this._resizeCallback);
    }
  },

  // Detaches resize event listener.
  _teardownResizer() {
    if (this._resizeCallback) {
      removeResizeListener(this.element, this._resizeCallback);
      this._resizeCallback = null;
    }
  },

  didInsertElement() {
    this._super(...arguments);
    run.schedule('afterRender', this, '_initSimulation');
    run.schedule('afterRender', this, '_initResizer');
  },

  willDestroyElement() {
    this._teardownSimulation();
    this._teardownResizer();
    this._super(...arguments);
  }
});
