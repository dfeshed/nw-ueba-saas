/**
 * @file Incident Info component
 * Displays a summary of data about a given incident model.
 */
import Ember from "ember";

export default Ember.Component.extend({
    tagName: "article",
    classNames: "rsa-incident-info",

    /**
     * The Incident record to be displayed by this component.
     * @type Object
     * @default null
     */
    incident: null,

    /**
     * User input for a new entry in the incident's journal.  This attribute will be updated automatically by bindings
     * as the user types text into the UI.
     * @type String
     * @default ""
     */
    journalEntry: ""
});
