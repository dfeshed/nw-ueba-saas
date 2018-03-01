import Mixin from '@ember/object/mixin';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';
import arrayToHashKeys from 'component-lib/utils/array/to-hash-keys';
import { next } from '@ember/runloop';
import { isEmpty } from '@ember/utils';
import $ from 'jquery';
import rsvp from 'rsvp';
import {
  wireTriggerToClick,
  wireTriggerToHover,
  unwireTriggerToClick,
  unwireTriggerToHover
} from 'component-lib/utils/tooltip-trigger';

const CSS_CLASS_ENTITY_HAS_BEEN_VALIDATED = 'entity-has-been-validated';
const CSS_CLASS_IS_CONTEXT_ENABLED = 'is-context-enabled';
const CSS_CLASS_IS_NOT_CONTEXT_ENABLED = 'is-not-context-enabled';
const CSS_CLASS_HAS_CONTEXT_DATA = 'has-context-data';
const HTML_ATTR_ENTITY_TYPE = 'data-entity-type';
const HTML_ATTR_ENTITY_ID = 'data-entity-id';
const HTML_ATTR_META_KEY = 'data-meta-key';

let freeIdCounter = 0;

/**
 * @class HighlightsEntities Mixin
 * Enables a Component to highlight the entities mentioned in its DOM, and wire them to the context-tooltip Component.
 *
 * This Mixin equips a component with a method for finding the entities (if any) mentioned in its DOM, and then
 * wiring up those found DOM elements to the `{{context-tooltip}}` component.
 *
 * @assumes Any entities mentioned in the DOM should match a configurable querySelector (`entitySelector`). Any DOM
 * elements that don't match the selector will be ignored.  For example, if selector like `.entity` would match:
 * ```html
 * <span class="entity" ..>10.20.30.40</span>
 * ```
 * @assumes Each matching DOM element will have HTML string attribute `data-entity-id` to identify the value of the
 * entity (e.g., IP address, username, domain name, hostname, etc). Additionally, to identify the type of the entity
 * (e.g., "IP", "USER", "DOMAIN", "HOST", etc), the DOM element will have either:
 * (a) a `data-entity-type` HTML string attribute; or
 * (b) a `data-meta-key` HTML string attribute which identifies the meta key that the data value corresponds to. In
 * this latter scenario, the `entityEndpointId` attr must be set to the id of the endpoint (concentrator/broker) from
 * which the meta value was fetched.  Both the meta key & the endpoint id are required to validate the entity in this case.
 * ```html
 * <!-- This does not require an entityEndpointId: -->
 * <span class="entity" data-entity-id="10.20.30.40" data-entity-type="IP">10.20.30.40</span>
 * <!-- This requires an entityEndpointId (e.g., "CONCENTRATOR-1"): -->
 * <span class="entity" data-entity-id="10.20.30.40" data-meta-key="ip.src">10.20.30.40</span>
 * ```
 *
 * Note that any DOM elements found by this mixin and determined to be enabled for context-lookups will be checked for
 * an `id` attribute.  Those elements which don't have an `id` will be assigned an automatically generated id like
 * `rsa-entity-<#>` where `<#>` is an auto-generated number.  This is done for two reasons:
 * 1. The current implementation of rsa-content-tethered-panel uses the element id; and
 * 2. An id enables us to avoid caching a reference to the DOM node object itself, which is risky because it can lead to
 * memory leaks; instead, we can simply cache the DOM node is, which is not vulnerable to memory leaks.
 *
 * @public
 */
export default Mixin.create({
  context: service(),
  eventBus: service(),

  /**
   * Determines whether the consuming Component will automatically attempt to find & highlight entities after
   * it has initially rendered.
   *
   * This is effectively a setting that enables this Mixin's functionality.  If `autoHighlightEntities` is truthy
   * at the time that the consuming Component is rendered, then the `highlightEntities()` method will automatically be
   * invoked after `didInsertElement`. Otherwise if `autoHighlightEntities` is falsey, `highlightEntities()` will not
   * be invoked automatically, and it becomes the responsibility of the consuming Component to manually invoke it
   * if & when desired.
   *
   * Therefore if `autoHighlightEntities` is falsey (which it is by default) and the consuming Component does NOT
   * manually invoke `highlightEntities()` then this Mixin's functionality is essentially dormant and not used.
   * Thus, `autoHighlightEntities` enables us to harmlessly apply this Mixin to our Component subclasses without
   * significant overhead, leaving this Mixin's functionality dormant by default, and then enabling the Mixin's
   * functionality on a case-by-case basis for specific instances of that Component subclass.
   *
   * Note: `autoHighlightEntities` is only read once when the Component is initially rendered. After that it is no
   * longer used.
   *
   * Note: The consuming Component can still manually call `highlightEntities()` at any time, even if
   * `autoHighlightEntities` if falsey.
   *
   * @type {Boolean}
   * @public
   */
  autoHighlightEntities: false,

  /**
   * The querySelector for finding DOM nodes that represent entities.
   * @type {String}
   * @public
   */
  entitySelector: '.entity',

  /**
   * Configurable id of the concentrator/broker from which any found meta values were fetched.
   * This is required in order to map meta values to entity types. Without this, only values which are explicitly
   * labeled as entity types will be processed.
   * @type {String}
   * @public
   */
  entityEndpointId: null,

  /**
   * Configurable `panelId` of the `context-tooltip` to be used as a tooltip for entity DOM nodes.
   * Defaults to the same default `panelId` used by the `context-tooltip` component.  Typically does not need to be
   * changed, but can be used if needed to support a custom tooltip.
   * @see component-lib/components/rsa-content-tethered-panel#panelId
   * @type {String}
   * @public
   */
  entityTooltipPanelId: 'context-tooltip-1',

  /**
   * Configurable event that will trigger the display of the tooltip for entity DOM nodes.
   * Either 'hover' or 'click'.
   * @see component-lib/components/rsa-content-tethered-panel-trigger#triggerEvent
   * @type {String}
   * @public
   */
  entityTooltipTriggerEvent: 'hover',

  /**
   * Configurable pause (in millisec) before displaying tooltip for entity DOM nodes when user mouseenters one.
   * Only used when `entityTooltipTriggerEvent` is 'hover'; otherwise ignored.
   * @see component-lib/components/rsa-content-tethered-panel-trigger#displayDelay
   * @type {Number}
   * @public
   */
  entityTooltipDisplayDelay: 1000,

  /**
   * Configurable pause (in millisec) before hiding tooltip for entity DOM nodes when user mouseexists one.
   * Only used when `entityTooltipTriggerEvent` is 'hover'; otherwise ignored.
   * @see component-lib/components/rsa-content-tethered-panel-trigger#hideDelay
   * @type {Number}
   * @public
   */
  entityTooltipHideDelay: 1000,

  /**
   * Configurable callback to be invoked whenever context data is found for an individual entity DOM node.
   * When invoked, the callback will receive 4 arguments: `type`, `id`, `$element` and `records`.
   *
   * By default, this mixin will apply a CSS class ('.has-context-data') to the DOM node when its data is successfully
   * retrieved from context service.  The `onEntityContextFound` allows this Component to implement additional logic when
   * notified of a response from the context service.  For example, this callback could further decorate the DOM
   * according to the actual contents of the data that was retrieved; such as coloring an entity red if it is on
   * a blacklist or involved in an Incident.
   *
   * @type {Function}
   * @param {String} type The entity type (e.g., "IP").
   * @param {String} id The entity identifier (e.g., "10.20.30.40")
   * @param {jQuery} $element The entity DOM node, wrapped in a jQuery object.
   * @param {Object[]} records The array of data records retrieved for the entity. @see context/services/context#summary
   * @public
   */
  onEntityContextFound: null,

  /**
   * Promise to fetch the list of entity types from context service.
   *
   * As an optimization, will fulfill with a hash map of the types Array, rather than the types Array itself.
   * This will enable us to do fast lookups while avoiding excessive Array looping.  Thus, the fulfilled value of
   * this promise will be an Object whose hash keys are valid entity types, and whose hash values are `true`.
   *
   * @example
   * ```js
   * // Promise fulfills with this map of 4 enabled entity types:
   * {
   *  IP: true,
   *  USER: true,
   *  DOMAIN: true,
   *  HOST: true
   * }
   * ```
   * @type {Ember.RSVP.Promise}
   * @private
   */
  @computed()
  _entityTypesPromise() {
    return this.get('context').types().then(({ data }) => arrayToHashKeys(data));
  },

  /**
   * Promise to fetch the map of meta keys to entity types, if an endpoint is given.
   *
   * This promise will fulfill with a mapping of meta keys to entity types for the given `entityEndpointId`, if given;
   * otherwise if `entityEndpointId` is not given, the promise fulfills with an empty hash.
   *
   * @example
   * ```js
   * // Promise fulfills with this map of entity types to meta keys:
   * {
   *  IP: ['ip.src', 'ip.dst'],
   *  USER: ['username'],
   *  HOST: ['alias.host'],
   *  DOMAIN: ['domain.src', 'domain.dst']
   * }
   * ```
   * @type {Ember.RSVP.Promise}
   * @private
   */
  @computed('entityEndpointId')
  _entityMetasPromise(endpointId) {
    return !endpointId ?
      rsvp.resolve({}) :
      this.get('context').metas(endpointId).then(({ data }) => data);
  },

  /**
   * A promise that is fulfilled when both `_entityTypesPromise` & `_entityMetasPromise` are settled.
   *
   * This promise waits for both promises to settle, and then fulfills with a single POJO that combines the fulfilled
   * values of the 2 promises:
   * - `types`: the fulfilled value of `_entityTypesPromise`;
   * - `metas`: the fulfilled value of `_entityMetasPromise` (possibly null).
   *
   * @type {{ types: Object, metas: Object|null }}
   * @private
   */
  @computed('_entityTypesPromise', '_entityMetasPromise')
  _entityTypesAndMetasPromises(typesPromise, metasPromise) {
    return rsvp.allSettled([ typesPromise, metasPromise ])
      .then(([ typesState, metasState ]) => {

        // Parse out the fulfilled values from the promises.
        const { value: types } = typesState || {};
        const { value: metas } = metasState || {};
        return {
          types,
          metas
        };
      });
  },

  /**
   * Searches this component's DOM for entities, marks their CSS appropriately and wires them up to context tooltip.
   *
   * This method should be invoked whenever the DOM has changed and there are possibly some entities in the DOM that
   * need to be wired up. Therefore this mixin automatically invokes this method after `didInsertElement`. However,
   * the method is exposed publicly in order to support Components whose entity DOM nodes may change even after
   * `didInsertElement`.  Such Components may call this method again manually anytime after their DOM has changed.
   * @public
   */
  highlightEntities() {
    // If we waited too long and this component has been trashed, exit.
    if (this.get('isDestroying') || this.get('isDestroyed')) {
      return;
    }

    const entitySelector = this.get('entitySelector');
    if (!entitySelector) {
      return;
    }

    // Async fetch the necessary configs from context service.
    this.get('_entityTypesAndMetasPromises').then(({ types, metas }) => {

      // If we waited too long and this component's DOM has been trashed, exit.
      // If contexthub-server is throwing error still _entityTypesPromise promise is getting resolved but this time there will not be any types. Due to that java script error will come. In this scenario we suppose to display some error to analyst. Currently preventing java script error later will be displaying proper error message to analyst.
      if (!this.element || this.get('isDestroying') || this.get('isDestroyed') || !types) {
        return;
      }

      // Process all the DOM nodes that are entities, excluding any that we already processed.
      const found = [];
      this.$(entitySelector).not(`.${CSS_CLASS_ENTITY_HAS_BEEN_VALIDATED}`)
        .each((index, el) => {
          const $el = $(el);

          // Highlight the node with appropriate CSS classes.
          const isContextEnabled = this._highlightEntity($el, types, metas);
          if (isContextEnabled) {

            // Ensure the node has an id; auto-assign it one, if needed.
            if (isEmpty(el.id)) {
              el.id = `rsa-entity-${freeIdCounter++}`;
            }

            // Wire the node up to context tooltip.
            const { type, id } = isContextEnabled;
            this._wireEntityToTooltip($el, type, id);
            found.push({ entity: { type, id }, elementId: el.id });
          }
        });

      // Request summary data for all the context enabled nodes.
      this._fetchEntitiesData(found);
    });
  },

  /**
   * Requests summary data for a given set of entity DOM nodes, and then when that summary data arrives, decorates the
   * nodes with CSS classes to indicate that their data is available.
   *
   * This method uses the `summary()` method of the `context` service to stream records for a list of entities. It
   * passes `summary()` a callback, which will be invoked every time new records arrive from server.  The callback
   * is responsible for decorating the corresponding DOM node, so that the UI may visually indicate which DOM nodes
   * have data ready.
   *
   * @param {{ type: String, id: String, $element: jQuery }[]} requests The entities for which to fetch data.
   * @private
   */
  _fetchEntitiesData(requests = []) {
    let onEntityContextFound = this.get('onEntityContextFound');
    if (!$.isFunction(onEntityContextFound)) {
      onEntityContextFound = null;
    }

    // Cache the given requests for future reference.
    // If we have previous requests already cached, don't lose them; just append to them.
    this._requests = this._requests || [];
    this._requests.pushObjects(requests);

    // Define a callback that will update our DOM when entity records stream back from server.
    const callback = (type, id, status, records) => {

      // If we waited too long and this component's DOM has been trashed, exit.
      if (!this.element || this.get('isDestroying') || this.get('isDestroyed')) {
        return;
      }

      // Search for this entity in our lists of requests.
      this._requests
        .filter(({ entity }) => {
          return (entity.type === type) && (entity.id === id);
        })
        .forEach(({ elementId }) => {
          const $element = $(`#${elementId}`);

          // Don't decorate DOM if records are empty (e.g., if data sources are not configured).
          if (records && records.length) {
            $element.addClass(CSS_CLASS_HAS_CONTEXT_DATA);
          }
          if (onEntityContextFound) {
            onEntityContextFound(type, id, $element, status, records);
          }
        });
    };

    // Request the entity records from server.
    const entities = requests.map(({ entity }) => entity);
    this.get('context').summary(entities, callback);
  },

  /**
   * Determines if a given DOM node corresponds to an enabled entity type, and applies a set of corresponding CSS
   * classes to the node.
   * - `.entity-has-been-validated` (indicates that node has been processed, so we don't repeat in the future);
   * - `.is-context-enabled` (if entity type is enabled);
   * - `.is-not-context-enabled` (if entity type is not enabled).
   *
   * The DOM node is expected to have an HTML attribute `data-entity-id`, plus either `data-entity-type` or
   * `data-meta-key`.  The `data-meta-key` value is only used if `data-entity-type` is not given; in that case,
   * the `data-meta-key` is used to map the node to an entity type; and if successful, then that entity type will be
   * written in the DOM node's `data-entity-type` HTML attribute.
   *
   * If the given DOM node is mapped to an enabled entity type and a non-null entity id, then this method returns
   * an object with the entity type & id (e.g.,`{ type, id }`). Otherwise, the method returns `null`.
   *
   * @param {jQuery} $el The DOM node wrapped in a jQuery selection object.
   * @param {Object} types A hash that maps valid entity types to `true`.
   * @param {Object} metas A hash that maps each entity type to an array of meta keys.
   * @returns {null|{ type: String, id: String }} The type & id of the entity, if enabled; null otherwise.
   * @private
   */
  _highlightEntity($el, types, metas) {

    // Read the entity type from the DOM node.
    let type = $el.attr(HTML_ATTR_ENTITY_TYPE);
    if (!type) {

      // DOM doesn't say entity type. Try to compute entity type from meta key (if given).
      const metaKey = $el.attr(HTML_ATTR_META_KEY);
      type = Object.keys(metas).find((entity) => {
        const metaKeyNames = metas[entity] || [];
        return metaKeyNames.includes(metaKey);
      });
      $el.attr(HTML_ATTR_ENTITY_TYPE, type || '');
    }

    // Check if the type is context-enabled. If so, check for an entity id.
    let isContextEnabled = !!(type && types[type]);
    const id = isContextEnabled ? $el.attr(HTML_ATTR_ENTITY_ID) : null;
    isContextEnabled = !!(isContextEnabled && id);

    // Apply corresponding CSS classes.
    $el.addClass(isContextEnabled ? CSS_CLASS_IS_CONTEXT_ENABLED : CSS_CLASS_IS_NOT_CONTEXT_ENABLED);
    $el.addClass(CSS_CLASS_ENTITY_HAS_BEEN_VALIDATED);

    return isContextEnabled ? { type, id } : null;
  },

  // Wires up a given DOM node to the context tooltip.
  _wireEntityToTooltip($el, type, id) {
    const {
      entityTooltipPanelId,
      eventBus,
      entityTooltipDisplayDelay: displayDelay,
      entityTooltipHideDelay: hideDelay
    } = this.getProperties('entityTooltipPanelId', 'eventBus', 'entityTooltipDisplayDelay', 'entityTooltipHideDelay');

    const triggerEvent = this.get('entityTooltipTriggerEvent');
    const wireFn = (triggerEvent === 'hover') ?
      wireTriggerToHover : wireTriggerToClick;

    wireFn($el[0], entityTooltipPanelId, eventBus, {
      model: { type, id },
      displayDelay,
      hideDelay,
      rightClick: triggerEvent === 'contextmenu',
      trigger: this
    });
  },

  // Unwires a given DOM node from the context tooltip.
  _unwireEntityToTooltip($el) {
    const unwireFn = (this.get('entityTooltipTriggerEvent') === 'hover') ?
      unwireTriggerToHover : unwireTriggerToClick;

    unwireFn($el[0]);
  },

  /**
   * Releases internal cache of entity DOM node ids. Also unwires the entity DOM nodes from tooltips, unless otherwise
   * requested.
   *
   * This mixin will automatically call this method from `willDestroyElement`.  However, the method is
   * exposed publicly anyway so that Components which destroy/rebuild their DOM even before `willDestroyElement` can
   * manually call this method anytime they remove entity DOM nodes.
   *
   * @param {Boolean} [dontUnwireTooltips=false] If exactly `true`, tooltip event wirings will be left intact.
   * @public
   */
  teardownEntities(dontUnwireTooltips = false) {
    if (dontUnwireTooltips !== true) {
      (this._requests || []).forEach(({ elementId }) => {
        const $element = $(`#${elementId}`);
        this._unwireEntityToTooltip($element);
      });
    }
    this._requests = [];
  },

  didInsertElement() {
    this._super();
    if (this.get('autoHighlightEntities')) {
      next(this, 'highlightEntities');
    }
  },

  willDestroyElement() {
    this.teardownEntities();
    this._super();
  }
});
