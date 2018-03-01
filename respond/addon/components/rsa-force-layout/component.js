/* global addResizeListener, removeResizeListener */

import { setProperties, set, get } from '@ember/object';
import Component from 'ember-component';
import run from 'ember-runloop';
import computed from 'ember-computed-decorators';
import boundingBox from 'respond/utils/force-layout/bounding-box';
import {
  forceSimulation,
  forceLink,
  forceManyBody,
  forceCollide
} from 'd3-force';
import { transition } from 'd3-transition';
import { easeCubicInOut } from 'd3-ease';
import { select } from 'd3-selection';

/**
 * @class d3 Force Layout component
 * A d3 force layout for rendering nodes and links, wrapped as an Ember component.
 * See d3 Force Layout docs for more details: https://github.com/mbostock/d3/wiki/Force-Layout
 *
 * @assumes javascript detect-element-resize library has imported globals addResizeListener & removeResizeListener
 * @see https://github.com/sdecima/javascript-detect-element-resize
 *
 * @public
 */
export default Component.extend({

  // Wrap the SVG canvas in a div. The div can be set to any size, while the SVG element by default fills the div
  // with its width & height set to 100%.
  tagName: 'div',
  classNames: 'rsa-force-layout',
  attributeBindings: ['zoom:data-zoom'],

  // d3 force layout configuration properties.
  // For details, see d3 API docs: https://github.com/mbostock/d3/wiki/Force-Layout
  charge: -1000,
  chargeDistance: 250,
  gravity: 0.1,
  friction: 0.5,
  alpha: 0.5,
  linkStrength: 0.5,
  linkDistance: 150,

  /**
   * Configurable count of initial animation frames that will be iterated without rendering DOM.
   *
   * Sometimes the initial layout is so chaotic that it can be annoying. To workaround that, we can execute those
   * layout computations manually (i.e., in a for loop without updating DOM).  But don't skip too many frames
   * or you risk locking up the browser.
   *
   * @type number
   * @public
   */
  skipFirstFrames: 2,

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

      if (!value.nodes) {
        set(value, 'nodes', []);
      }
      if (!value.links) {
        set(value, 'links', []);
      }

      // Ensure all the given nodes (if any) have a radius.
      // Doing this here, rather than later, ensures our template doesn't initially render them without a radius.
      const radius = this.get('nodeRadius');
      value.nodes.forEach(function(node) {
        if (!get(node, 'r')) {
          set(node, 'r', radius);
        }
      });

      this._data = value;
      this._dataDidChange();
      return this._data;
    }
  },

  /**
   * If true, indicates that the viz should automatically pan & zoom so that all the currently selected nodes fit within
   * the component's viewport. If no nodes are selected, then all rendered nodes are fitted within the viewport.
   *
   * When `autoCenter` is falsey, the `center()` method must be explicitly invoked whenever we wish to fit the nodes
   * within the component.  But when `autoCenter` is truthy, `center()` will be invoked automatically whenever the
   * component resizes or its data changes.
   *
   * @type boolean
   * @default false
   * @public
   */
  autoCenter: false,

  /**
   * The current SVG scale factor that is being applied. Read-only.
   *
   * When the `center()` method is called, a scale factor will be applied in order to fit all the nodes within the
   * component's viewport. This is exposed here as an attr in order to use it in an HTML attribute binding, so
   * we can write conditional CSS. For example, we can choose to enlarge/shrink font sizes based on the zoom level.
   *
   * @type number
   * @readonly
   * @private
   */
  zoom: 1,

  /**
   * Optional width & height limit for the bounding box of the nodes.
   *
   * When the `center()` method is called, a scale factor will be applied in order to fit all the nodes within the
   * component's viewport. However, in some cases, we may want to fit all the data to some custom area different
   * from the component's viewport.  This attr allows us to specify the size of a rectangle that the data should be
   * fit to. If unspecified, the component's current width & height is assumed.
   *
   * If some nodes are currently selected, then the bounding box limit will be applied only to those subset of nodes
   * which are selected; otherwise it is applied to the entire set of nodes.
   *
   * @type {{ width: number, height: number }}
   * @public
   */
  fitToSize: {
    get() {
      return this._fitToSize;
    },
    set(value) {
      value = !!value;
      this._fitToSize = value;
      if (this.get('autoCenter')) {
        this.center();
      }
      return value;
    }
  },

  /**
   * Hash table of the ids of the currently selected nodes or links (if any).
   * A node or link data object is considered selected if its `.id` can be found in the hash keys of the `selections` hash.
   *
   * Note: `selections` is a hash, rather than array. This is efficient, because we can check for a given id in
   * `selections` directly, without having to loop thru an array.
   *
   * Note: `selections` does not contain the actual selected objects, only their ids. This allows to support scenarios
   * where we only know the id of the data we want to select without requiring a handle to the data object iself.
   *
   * Note: We don't keep 2 separate hashes of ids (nodes vs. links). Thus we are implicitly assuming that
   * node ids & link ids are unique across nodes & links.  In other words, a given id should only belong to
   * either a node or a link, but not both. Typically this should be an easy assumption to abide by, as long as your code
   * for generating data uses naming conventions for node ids vs. link ids that don't overlap. (For example, you
   * could prefix every node id with 'node:' and every link id with 'link:'.)
   *
   * @type {object}
   * @public
   */
  @computed
  selections: {
    get() {
      return this._selections || {};
    },
    set(value = {}) {
      this._selections = value;

      // Using run.next ensures that whenever our callback calls this.get('selections') will return the latest value.
      run.next(this, this._applySelectionsToData);
      return value;
    }
  },

  // Evaluates to true if the `selections` hash has at least one id.
  @computed('selections')
  hasSelections(selections) {
    return !!Object.keys(selections).length;
  },

  /**
   * Configurable action to be invoked when user does a simple click on a node or link.
   *
   * The action will receive 2 arguments:
   * `type`: {string} either 'node' or 'link', depending on which was clicked;
   * `value`: {object} the data object corresponding to the clicked node (or link).
   *
   * @type {function}
   * @public
   */
  singleSelectAction() {},

  /**
   * Configurable action to be invoked when user does a Ctrl+click on a node or link.
   *
   * The action will receive 2 arguments:
   * `type`: {string} either 'node' or 'link', depending on which was clicked;
   * `value`: {object} the data object corresponding to the clicked node (or link).
   *
   * @type {function}
   * @public
   */
  toggleSelectAction() {},

  // Updates the `isSelected` property of every node & link currently in `data` to reflect the current `selections`.
  // If the id of a node/link is found in `selections`, then that node's/link's `isSelected` will be set to `true`;
  // otherwise it will be set to `false`.
  // For each link found in `selections`, the link's source & target nodes will both automatically have their
  // `isSelected` property set to `true` as well.
  _applySelectionsToData() {
    const { nodes, links } = this.get('data');
    const selections = this.get('selections');
    const extraSelections = {};

    // First update the `isSelected` attr of every link.
    links.forEach((link) => {
      const isSelected = link.id in selections;
      set(link, 'isSelected', isSelected);

      // Record the source & target nodes of a selected link, in order to mark those nodes as selected too.
      if (isSelected) {
        extraSelections[link.source.id] = true;
        extraSelections[link.target.id] = true;
      }
    });

    // Now update the `isSelected` attr of every node.
    nodes.forEach((node) => {
      const isSelected = (node.id in selections) || (node.id in extraSelections);
      set(node, 'isSelected', isSelected);
    });
  },

  // Triggers a restart of the layout algorithm when new data arrives.
  _dataDidChange() {
    this._applySelectionsToData();
    run.schedule('render', this, this._restartSimulation);
  },

  // Restart the d3 force layout algorithm to re-position the new DOM.
  _restartSimulation() {
    const { simulation } = this;
    if (!simulation) {
      // Component DOM has not been inserted yet; exit.
      return;
    }

    // Stop any ongoing force layout animation.
    simulation.stop();


    // Performance optimization: cache attrs that will be frequently read during animation.
    this._cache = this.getProperties(
      'alpha',
      'autoCenter',
      'nodeMaxStrokeWidth'
    );

    // Feed the layout algorithm the newest data set.
    simulation.nodes(this.get('data.nodes'));
    simulation.force('link').links(this.get('data.links'));

    this._tickCounter = 0;

    // Start new animation with latest data.
    simulation.restart();

    // Skip some of the initial d3 force animation by executing the first frames' computations now, manually.
    for (let i = this.get('skipFirstFrames') || 0; i > 0; i--) {
      simulation.tick();
    }
  },

  /**
   * Configurable d3 callback to be invoked each time a single frame of the simulation if computed.
   * See d3 Force Layout docs for more details: https://github.com/mbostock/d3/wiki/Force-Layout
   * Responsible for copying the latest node positions to observable attrs that can be detected by Ember bindings.
   * Also responsible for re-entering the SVG viewBox around the bounding-box containing all the highlighted nodes.
   * @assumes This component's template has bindings to certain properties in the 'nodes' and 'links' data;
   * this method will update those properties' values, thus triggering DOM updates.
   * @public
   */
  tick() {
    const { simulation } = this;
    if (!simulation) {
      return;
    }

    // Update the node positions in an observable (KVO-friendly) way, so that Ember will detect any changes
    // and update any template bindings accordingly.
    const nodes = simulation.nodes();
    nodes.forEach((d) => {
      const { x, y } = d;
      setProperties(d, {
        xObservable: x,
        yObservable: y
      });
    });

    // If autoCenter is enabled, re-center the bounding box every few ticks.
    if (this._cache.autoCenter) {

      // Each tick is ~17ms, so 3 ticks ~= 50ms, which is approx sufficient for reasonably smooth animation.
      if (this._tickCounter % 3 === 0) {

        // If the visualization has cooled down (alpha close to 0) use a transition, otherwise skip the transition.
        const alpha = this.simulation.alpha();
        const lowAlpha = alpha <= (this._cache.alpha * .1);
        this.center(lowAlpha ? easeCubicInOut : 'none');
      }
    }

    this._tickCounter++;
  },

  /**
   * Visually centers the rendered nodes within the SVG container, using an optional transition.
   * Computes a center using all the nodes in 'this._data.nodes' which is assumed to be cached & up to date.
   * Implements the centering by applying an SVG transform to an SVG element (<g>) that contains all the SVG
   * nodes and links. Assumes that element is cached in the 'centeringElement' property; otherwise if property value
   * is not defined, this method does nothing.
   * @param {function|string} [easing=d3.easeCubicInOut] Specifies the easing to use when applying the transform. If set explicitly to
   * 'none', no easing is used and the transform is applied immediately.
   * @param {Number} [duration=300] Specifies duration of transition to use when applying the transform. If set
   * explicitly to 0, no transition is used and the transform is applied immediately.
   * @public
   */
  center(easing = easeCubicInOut, duration = 300) {
    let el = this.centeringElement;
    if (!el) {
      return;
    }


    // Compute a transform that can be applied to the SVG in order to center its nodes.
    const width = this.element.clientWidth || 0;
    const height = this.element.clientHeight || 0;
    if (!width || !height) {
      return;
    }
    const fitToSize = this.get('fitToSize') || {};
    const fitToWidth = fitToSize.width || width;
    const fitToHeight = fitToSize.height || height;
    const fitToCenter = (fitToSize.left + fitToSize.width / 2) || (width / 2);
    const fitToMiddle = (fitToSize.top + fitToSize.height / 2) || (height / 2);

    // The transform can also include a scale needed to make the nodes' bounding box fit in the optional properties
    // 'fitToWidth' and 'fitToSize' (if any). Don't go larger scale than 1.5, for cosmetic reasons.
    const MAX_ZOOM = 1.5;  // 1.25;
    const box = boundingBox(
      this.get('hasHighlights') ? this.get('highlights.nodes') : this.get('data.nodes'),
      this._cache.nodeMaxStrokeWidth,
      width,
      height
    );
    const k = (fitToWidth && fitToHeight) ? Math.min(MAX_ZOOM, fitToWidth / box.width, fitToHeight / box.height) : 1;

    // Cache the resulting zoom scale for future reference.
    this.set('zoom', Number(k).toFixed(1));

    // Compose an SVG transform that will:
    const transform = [
      // (1) move the center of the bounding box to the origin (0,0)
      `translate(${[(-1 * box.center).toFixed(3), (-1 * box.middle).toFixed(3)]})`,
      // (2) apply the scale factor
      `scale(${k.toFixed(3)})`,
      // (3) move the bounding box to the center of the `fitToSize` rectangle (or, if undefined, center of component).
      `translate(${[ fitToCenter.toFixed(3), fitToMiddle.toFixed(3)]})`
    ].reverse().join(' ');  // Reverse the array order because SVG transforms are applied right to left.

    // Apply the transform to SVG, possibly with a transition.
    if (transition && ((easing === 'none') || !duration)) {
      el.interrupt();     // stops any previous transition that may still be on-going
    } else {
      el = el.transition()  // overwrites any previous transition that may still be on-going
        .ease(easing)
        .duration(duration);
    }
    el.attr('transform', transform);
  },

  // Initializes the force layout configuration, based on various properties of this component instance.
  didInsertElement() {
    this._super(...arguments);

    const linkForce = forceLink()
      .distance(this.get('linkDistance'))
      .strength(this.get('linkStrength'));

    const gravityForce = forceManyBody()
      .strength(this.get('gravity'));

    const chargeForce = forceManyBody()
      .strength(this.get('charge'))
      .distanceMax(this.get('chargeDistance'));

    const collideForce = forceCollide()
      .radius(this.get('nodeRadius') * 1.5 + this.get('nodeMaxStrokeWidth') * 2);

    this.simulation = forceSimulation()
      .force('link', linkForce)
      .force('gravity', gravityForce)
      .force('charge', chargeForce)
      .force('collide', collideForce)
      .velocityDecay(this.get('friction'))
      .alpha(this.get('alpha'))
      .on('tick', this.get('tick').bind(this));

    // Optimization: cache handle to important DOM node for future reference.
    this.centeringElement = select(this.element).select('.centering-element');

    // Manually kick off the layout algorithm of the initial data set, if any.
    this._dataDidChange();

    run.schedule('afterRender', this, this.afterRender);
  },

  afterRender() {
    // Attach resize event listener.
    this._resizeCallback = () => {
      run.throttle(this, 'center', 'none', 250, false);
    };
    addResizeListener(this.element, this._resizeCallback);
  },

  willDestroyElement() {
    // Teardown resize event listener.
    removeResizeListener(this.element, this._resizeCallback);

    // Stops any on-going force layout animation algorithm.
    delete this.centeringElement;

    if (this.force) {
      this.force.stop();
    }
    this._super(...arguments);
  }
});
