/**
 * @file Field component.
 * Represents a field (property) of some data object; for example, the created date of an Incident.
 * @public
 */
import Ember from 'ember';

export default Ember.Component.extend({
  // Default tagName is "li" because fields are most often displayed in a list format.
  // Templates that use this component can overwrite tagName whenever needed (e.g., if only showing one field,
  // the template may want to set tagName to "p" or "section").
  tagName: 'li',
  classNames: 'rsa-field',
  attributeBindings: ['data-field', 'data-value']
});
