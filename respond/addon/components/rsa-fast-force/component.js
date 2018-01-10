import Component from 'ember-component';
import layout from './template';
import set from 'ember-metal/set';
import computed, { alias } from 'ember-computed-decorators';
import { forceSimulation, forceLink, forceManyBody, forceCollide, forceCenter } from 'd3-force';
import { select } from 'd3-selection';
import { zoom } from 'd3-zoom';
import dataJoin from './util/data-join';
import filterJoin from './util/filter-join';
import ticked from './util/ticked';
import zoomed from './util/zoomed';
import center from './util/center';
import arrayToHashKeys from 'component-lib/utils/array/to-hash-keys';
import run from 'ember-runloop';
import $ from 'jquery';
import { max } from 'd3-array';
import { scaleLinear } from 'd3-scale';
import { isEmpty } from 'ember-utils';

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
  layout,
  classNames: 'rsa-force-layout',
  classNameBindings: ['shouldShowNodes:show-nodes:hide-nodes', 'shouldShowLinks:show-links:hide-links', 'isDragging'],
  attributeBindings: ['zoom:data-zoom'],

  // d3 force layout configuration properties.
  // For details, see d3 API docs: https://github.com/mbostock/d3/wiki/Force-Layout
  charge: -1000,
  chargeDistance: 250,
  collideStrength: 0.4, // strength of force that prevents node collisions (d3's default = 0.7)
  linkStrength: 0.01, // weak link strength works better with variable node radii
  linkDistance: 150,
  centerX: null,      // centering forces produce confusing physics for users; disable by default
  centerY: null,
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
   * The alpha level below which nodes will be displayed, because they are too chaotic during the "hot"
   * stage of the simulation.  The lower we set this number, the cooler the simulation must get before nodes are shown.
   * If set to zero, the nodes are always shown.
   * This also serves as a performance optimization: while we don't show the nodes, we also skip manipulating their
   * DOM, so there is less demand on the browser.
   * @type {number}
   * @public
   */
  alphaShowNodes: 0.49,

  /**
   * The alpha level below which links will be displayed, because they are too chaotic during the "hot"
   * stage of the simulation.  The lower we set this number, the cooler the simulation must get before links are shown.
   * If set to zero, the links are always shown.
   * This also serves as a performance optimization: while we don't show the links, we also skip manipulating their
   * DOM, so there is less demand on the browser.
   * @type {number}
   * @public
   */
  alphaShowLinks: 0.05,

  /**
   * The alpha level below which the simulation will stop.
   * @type {number}
   * @public
   */
  alphaStop: 0.005,

  /**
   * The minimum alpha level at which the simulation will resume if/when new data streams in.
   * If the simulation is already running when new data arrives, the data is processed and the simulation resumes
   * at the same alpha level UNLESS it is below this minimum.  In that case, this minimum alpha is applied.
   * This ensures that a very cool simulation gets re-heated enough to accommodate the newly arrived nodes.
   * @type {number}
   * @public
   */
  alphaResumeMin: 0.05,

  /**
   * Determines whether nor not nodes should be shown. Nodes are initially shown only after the simulation's alpha has
   * sufficiently cooled down to `alphaShowNodes` or less.  However, once nodes are shown, they continue to always be
   * shown, even if the simulation gets hot again later (e.g., new data records stream in).
   * @public
   */
  @computed('alphaCurrent', 'alphaShowNodes')
  shouldShowNodes(current, limit) {
    if (this._nodesWereShown) {
      return true;
    } else if (!limit || (current <= limit)) {
      this._nodesWereShown = true;
      return true;
    } else {
      return false;
    }
  },

  /**
   * Determines whether nor not links should be shown. Links are initially shown only after the simulation's alpha has
   * sufficiently cooled down to `alphaShowLinks` or less.  However, once links are shown, they continue to always be
   * shown, even if the simulation gets hot again later (e.g., new data records stream in).
   * @public
   */
  @computed('alphaCurrent', 'alphaHideLinks')
  shouldShowLinks(current, limit) {
    if (this._linksWereShown) {
      return true;
    } else if (!limit || (current <= limit)) {
      this._linksWereShown = true;
      return true;
    } else {
      return false;
    }
  },

  /**
   * Configurable maximum radius of the node circles.
   *
   * Used when:
   * (1) computing the endpoints of the links that connect circles (the endpoints lie on the circumferences of the
   * circles, not the centers of the circles); and
   * (2) for avoiding node collisions (overlap).
   *
   * @type number
   * @public
   */
  nodeMaxRadius: 100,

  /**
   * Configurable minimum radius of the node circles.
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
  nodeMinRadius: 25,

  /**
   * Configurable accessor that takes a node data point and returns the corresponding value for the radial axis.
   * By default this accessor returns the count of events associated with that node.  This is useful for entity graphs
   * whose node sizes are weighted by event volume. But it's not true in general for all force-layout graphs, so
   * we'll have to refactor this out if/when we move this component to component-lib for generic use.
   * @type function
   * @public
   */
  radialAccessor: ((d) => d.events ? d.events.length : 0),

  /**
   * A linear scale that maps the current domain of radial data to a range of radial sizes.
   * @assumes Min domain value is 1. This is true for entity graphs where radial axis is the event count, and yields
   * nicer graphs this way. But it's not true in general for all force-layout graphs, so we'll have to refactor this
   * out if/when we move this component to component-lib for generic use.
   * @type {d3.scaleLinear}
   * @private
   */
  @computed('nodeMinRadius', 'nodeMaxRadius', 'data', 'radialAccessor')
  radialScale(minRadius, maxRadius, data, radialAccessor) {
    const { nodes } = data || {};
    const minDomainValue = 1;
    let maxDomainValue = max(nodes || [], radialAccessor);
    maxDomainValue = Math.max(minDomainValue, maxDomainValue);
    // If the max domain value is below the max radius, you'll map tiny domain value to huge circles.
    maxDomainValue = Math.max(maxDomainValue, maxRadius);
    return scaleLinear()
      .domain([
        minDomainValue,
        maxDomainValue
      ])
      .range([
        minRadius,
        maxRadius
      ]);
  },

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
   * Configurable minimum stroke width (in pixels) of the link lines.
   * @type number
   * @public
   */
  linkMinWidth: 2,

  /**
   * Configurable maximum stroke width (in pixels) of the link lines.
   * @type number
   * @public
   */
  linkMaxWidth: 10,

  /**
   * Configurable accessor that takes a link data point and returns the corresponding value for the link stroke width.
   * By default this acc
   * @type function
   * @public
   */
  linkWidthAccessor: ((d) => d.events ? d.events.length : 0),

  /**
   * A linear scale that maps the current domain of link data to a range of link stroke widths.
   * @assumes Min domain value is 1. This is true for entity graphs where link width is the event count, and yields
   * nicer graphs this way. But it's not true in general for all force-layout graphs, so we'll have to refactor this
   * out if/when we move this component to component-lib for generic use.
   * @type {d3.scaleLinear}
   * @private
   */
  @computed('linkMinWidth', 'linkMaxWidth', 'data', 'linkWidthAccessor')
  linkWidthScale(linkMinWidth, linkMaxWidth, data, linkWidthAccessor) {
    const { links } = data || {};
    const minDomainValue = 1;
    let maxDomainValue = max(links || [], linkWidthAccessor);
    maxDomainValue = Math.max(minDomainValue, maxDomainValue);
    // If the max domain value is below the max width, you'll map tiny domain value to thick lines.
    maxDomainValue = Math.max(maxDomainValue, linkMaxWidth);
    return scaleLinear()
      .domain([
        minDomainValue,
        maxDomainValue
      ])
      .range([
        linkMinWidth,
        linkMaxWidth
      ]);
  },

  /**
   * Configurable width of the arrow markers that are rendered at the ends of links.
   * By default, is automatically assigned to be just a hair wider than the maximum link stroke width, but at least 10.
   * @type number
   * @public
   */
  @computed('linkMaxWidth')
  arrowWidth(linkMaxWidth) {
    const width = $.isNumeric(linkMaxWidth) ? linkMaxWidth : 0;
    return Math.max(10, width + 2);
  },

  /**
   * Configurable height of the arrow markers that are rendered at the ends of links.
   * By default, is automatically assigned to match the arrow widths.
   * @type number
   * @public
   */
  @alias('arrowWidth')
  arrowHeight: null,

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
   * Indicates whether or not the end-user has manually panned/zoomed this UI.
   *
   * When we run the simulation, we typically auto-center it once it has cooled down sufficiently. However, we don't
   * want to auto-center it if the user has already manually panned/zoomed it; we want to preserve their view.
   * Therefore we use this boolean to record that the user has manually panned/zoomed.
   *
   * @type {boolean}
   * @default false
   * @readonly
   * @private
   */
  userHasZoomed: false,

  /**
   * Indicates whether or not this component is automatically centering its viewport.
   *
   * If `autoCenter` is truthy, this component will automatically pan & zoom to center its nodes during the force-layout
   * simulation. During those times, `isCentering` is set to true, so that our pan/zoom handlers can detect that
   * the center operation was initiated by the component itself, not by the end-user.
   *
   * @type {boolean}
   * @default false
   * @readonly
   * @private
   */
  isCentering: false,

  /**
   * Indicates whether or not the user is currently panning and/or zooming the visualization.
   *
   * @type {number}
   * @readonly
   * @public
   */
  isDragging: false,

  /**
   * When given, this component will mark all the nodes/links whose `id`s are not included in `filter` as hidden.
   * Typically used in order to dim/hide a subset of `data` (via CSS) without removing the data & its DOM.
   *
   * By default, `filter` is `null` and all nodes & links in `data` are shown.
   * When `filter` is given, all nodes & links will have their `isHidden` property updated to `true` (if they
   * are in `filter`) or `false`.
   *
   * Note: if `filter` is null, it is considered an empty filter, and all data is shown. In contrast, if `filter`
   * has an empty array of node/link ids, then none of the nodes/links will be shown.
   *
   * @type { { nodeIds: string[], linkIds: string[] } }
   * @public
   */
  @computed
  filter: {
    get() {
      return this._filter;
    },
    set(value) {
      const was = this._filter;
      this._filter = value;

      const changed = (was !== value) && !(isEmpty(was) && isEmpty(value));
      if (changed) {
        // Must use `run.next` to ensure that when the called function calls `this.get('data')`, this function will
        // have already returned and Ember will have updated the `data` property value.
        run.next(this, '_filterDidChange');
      }
      return this._filter;
    }
  },

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

      this._data = value;

      // If this element has rendered, invoke handler which will update the d3 simulation.
      // Must use `run.next` to ensure that when the called function calls `this.get('data')`, this function will
      // have already returned and Ember will have updated the `data` property value.
      if (this.element) {
        run.next(this, '_dataDidChange');
      }

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
   * Configurable function responsible for showing/hiding DOM according to `filter`.
   *
   * This function is analogous to `dataJoin`, except that wherease `dataJoin` is responsible for
   * building/destroying DOM, this function is responsible for showing/hiding it without adding/removing it.
   * Typically used to dim/highlight a subset of data.
   *
   * Here "data" means the business data in nodes & links, but typically does not mean coordinates.
   * The coordinates are computed by this component's simulation, which may not yet have started when `filterJoin` is invoked.
   *
   * @assumes This function will be invoked with its `this` context set to this component instance.
   * @type {function}
   * @public
   */
  filterJoin,

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
    if (this.isDestroyed || this.isDestroying) {
      return;
    }

    // Reset flag that tells us if user has manually dragged this dataset around.
    this.set('dataHasBeenDragged', false);

    const { simulation } = this;
    if (!simulation) {
      // component hasn't rendered yet, exit
      return;
    }

    // For smoother transition, keep track of how hot the simulation was before this interruption.
    const lastAlpha = simulation.alpha();
    this.stop();

    // Compute the node radii and store in the data for future reference (e.g., DOM rendering & to avoid collisions).
    const { nodes = [], links = [] } = this.get('data') || {};
    const { radialAccessor, radialScale } = this.getProperties('radialAccessor', 'radialScale');
    nodes.forEach(function(d) {
      d.r = radialScale(radialAccessor(d));
    });

    // Compute the link stroke widths and store in the data for future reference (e.g., DOM rendering).
    const { linkWidthAccessor, linkWidthScale } = this.getProperties('linkWidthAccessor', 'linkWidthScale');
    links.forEach(function(d) {
      d.stroke = linkWidthScale(linkWidthAccessor(d));
    });

    // Join the data to the DOM using configurable function.
    // Optimization: Cache the result (if any) so that tick handler can use it subsequently.
    this.joined = this.get('dataJoin').apply(this, []);

    this._filterDidChange(true);

    // Feed the data to the simulation.
    simulation
      .nodes(nodes)
      .force('link').links(links);

    if (nodes.length) {
      this.start(lastAlpha);
    }
  },

  /**
   * Applies filter to current `data` by updating `isHidden` property of all `data.nodes` & `data.links`.
   * Typically called after either `filter` changes or `data` changes.
   * @param {boolean} [skipCentering=false] If true, indicates that centering is not needed. Typically centering is
   * done here when `filter` did change but not when `data` did change, because a `filter` change is usually triggered
   * by a user action (e.g., click) whereas a `data` change can happen each time new data points stream in from server,
   * in which case the force-layout simulation will run & take care of the layout & centering.
   * @private
   */
  _filterDidChange(skipCentering) {
    if (this.isDestroyed || this.isDestroying) {
      return;
    }

    const data = this.get('data');
    if (!data) {
      return;
    }
    const { nodes = [], links = [] } = data;
    const filter = this.get('filter');
    if (!filter) {

      // Filter is not given, mark all nodes & links as not hidden.
      [ nodes, links ].forEach((arr) => arr.setEach('isHidden', false));
    } else {

      // Filter is given, let's apply it to data (if any).
      // Define a util function that will update `isHidden` for a given array of data objects & list of ids.
      const update = (arr, ids) => {
        const hash = arrayToHashKeys(ids);
        arr.forEach((d) => {
          d.isHidden = !(d.id in hash);
        });
      };

      // Use the util to update the nodes & links.
      update(nodes, filter.nodeIds);
      update(links, filter.linkIds);
    }

    // Update the DOM to sync with the updated data.
    this.get('filterJoin').apply(this, []);
    if (this.get('autoCenter') && !skipCentering) {
      this.center();
    }
  },

  /**
   * Starts the current simulation with its current data set, if any.
   * Responsible for respecting the `alphaInitial` & `skipFirstFrames` settings.
   * @public
   */
  start(lastAlpha) {
    const { simulation } = this;
    if (!simulation) {
      // component hasn't rendered yet, exit
      return;
    }

    const isResuming = lastAlpha > this.get('alphaStop');
    const alpha = !isResuming ? this.get('alphaInitial') : Math.max(lastAlpha, this.get('alphaResumeMin'));

    simulation
      .alpha(alpha)
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
      collideStrength,
      charge,
      chargeDistance,
      centerX,
      centerY,
      nodeMaxRadius,
      nodeMaxStrokeWidth
    } = this.getProperties(
      'linkDistance',
      'linkStrength',
      'collideStrength',
      'charge',
      'chargeDistance',
      'centerX',
      'centerY',
      'nodeMaxRadius',
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
    if ($.isNumeric(centerX) && $.isNumeric(centerY)) {
      const centerForce = forceCenter(centerX, centerY);
      simulation.force('center', centerForce);
    }

    // Define a force that prevents node collisions (overlap).
    if (collideStrength) {
      const collideForce = forceCollide()
        .radius(nodeMaxRadius + nodeMaxStrokeWidth * 2)
        .strength(collideStrength);
      simulation.force('collide', collideForce);
    }

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

  didInsertElement() {
    this._super(...arguments);
    run.schedule('afterRender', this, '_initSimulation');
  },

  willDestroyElement() {
    this._teardownSimulation();
    this._super(...arguments);
  }
});
