/**
 * @file Incidents Sort component
 * Displays a set of options for sotring the data in a IncidentsCube instance.
 */
import Ember from "ember";

export default Ember.Component.extend({
    tagName: "article",
    classNames: "rsa-incidents-sort",

    /**
     * IncidentsCube instance which this component targets.
     * @type Object
     */
    cube: null,

    /**
     * Indicates where Assignee should be included in the list of options in this UI.
     * @type Boolean
     * @default true
     */
    includeAssignee: true
});
