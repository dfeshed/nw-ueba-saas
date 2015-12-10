/**
 * @file Journal Entry component.
 * Represents an entry (with a date, author & notes) of some journal object; for example, an entry in the journal of
 * an incident.
 * @public
 */
import Ember from 'ember';

export default Ember.Component.extend({
  // Default tagName is 'li' because entries are most often displayed in a list format.
  // Templates that use this component can overwrite tagName whenever needed (e.g., if only showing one entry,
  // the template may want to set tagName to 'p' or 'section').
  tagName: 'li',
  classNames: 'rsa-journal-entry'
});
