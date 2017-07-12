import Component from 'ember-component';
import layout from './template';

/**
 * @class Event Details Body Value component
 * Renders a given scalar value for a given property name, presumably from a normalized event object.
 *
 * @example
 * ```hbs
 * <span class="scalar-value" data-meta-key="{{property.key}}" data-entity-id="{{property.value}}">{{property.value}}</span>
 * ```
 * @public
 */
export default Component.extend({
  tagName: 'span',
  layout,
  classNames: ['rsa-event-details-body-value', 'entity'],
  attributeBindings: ['key:data-meta-key', 'value:data-entity-id'],
  key: null,
  fullPath: null,
  value: null
});
