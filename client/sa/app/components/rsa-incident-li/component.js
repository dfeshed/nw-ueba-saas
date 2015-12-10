/**
 * @file Incident List Item component.
 * Represents an Incident as an item in a list (rather than a detailed rendering of an incident's data).
 * @public
 */
import Ember from 'ember';

export default Ember.Component.extend({
  // Default tagName is "li" because this component is most often displayed in a list format.
  // Templates that use this component can overwrite tagName whenever needed (e.g., if only showing one incident,
  // the template may want to set tagName to "section").
  tagName: 'li',
  classNames: 'rsa-incident-li',
  attributeBindings: ['model.prioritySort:data-priority', 'selected'],

  /**
   * The incident data record to be rendered.
   * @type Object
   * @public
   */
  model: null,

  /**
   * Responds to clicks by firing this component's default action (if any), passing along the click event.
   * The default action is typically set externally by whatever template is using this component.
   * @param {Object} e The click event object.
   * @public
   */
  click(e) {
    this.sendAction('action', e);
  }
});
