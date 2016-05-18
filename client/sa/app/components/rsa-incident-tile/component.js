/**
 * @file Incident List Tile Item component.
 * Represents an Incident as an item in a list (rather than a detailed rendering of an incident's data).
 * @public
 */
import Ember from 'ember';

export default Ember.Component.extend({
  tagName: 'li',
  classNames: 'rsa-incident-tile',
  attributeBindings: ['model.id:data-inc-id', 'model.prioritySort:data-priority'],

  /**
   * @description When clicking on the incident, take the user to
   * incident detail page by triggering the actions defined in the route
   * @event
   * @public
   */
  click() {
    this.sendAction('clickAction', this.get('model'));
  }
});
