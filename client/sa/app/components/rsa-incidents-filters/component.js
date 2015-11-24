/**
 * @file Incidents Filters component
 * Displays a set of options for filtering the data in a IncidentsCube instance.
 */
import Ember from "ember";

export default Ember.Component.extend({
    tagName: "article",
    classNames: "rsa-incidents-filters",

    /**
     * IncidentsCube instance which this component targets.
     * @type Object
     */
    cube: null,

    /**
     * Indicates where Assignee should be included in the list of options in this UI.
     * Typically set at run-time as the user toggles between viewing his incident queue or the list of all incidents.
     * When viewing their own queue, displaying Assignees is superfluous.
     * @type Boolean
     * @default true
     */
    includeAssignee: true,

    /**
     * Indicates where counts should be included in the list of options in this UI.
     * Typically set at run-time as the user toggles between viewing his incident queue or the list of all incidents.
     * When viewing their own queue, displaying counts is misleading because the counts are computed on the entire set
     * of incidents, not just the user's own incidents.
     * @type Boolean
     * @default true
     */
    includeCounts: true,

    /**
     * Configurable action to be triggered when user clicks a time range from the UI.
     * @type Function
     */
    timeRangeAction: null,

    /**
     * Responds to clicks on the filter options in the UI by updating the cube's filter.
     * Supports SHIFT clicking for multiple selections.  Note that in order to support
     * such modifier keys, we must implement this behavior in the click handler rather than in an action.
     * @param {Object} e The click event object.
     */
    click: function(e) {
        var shiftKey = !!(e && e.shiftKey);
        Ember.run(this, "_selectFilter", e.target, shiftKey);
        return !shiftKey;
    },

    /**
     * Applies a filter to this component's cube, depending on which HTML element was selected.
     * @param {HTMLElement} target The DOM element that was selected.
     * @param {Boolean} append If true, indicates that the selection should be appended to the current filter, rather
     * than overwriting it.
     * @private
     */
    _selectFilter: function(target, append) {
        var cube = this.get("cube");
        if (!cube) {
            return;
        }

        // looks for data-field and data-group HTML attributes
        var $target = Ember.$(target).closest("[data-group]", this.element);
        if (!$target[0]) {
            return;
        }
        var fieldKey = $target.attr("data-field"),
            groupKey = $target.attr("data-group"),
            groupDataType = $target.attr("data-group-type");
        if (!fieldKey) {    // we do support groupKey === "" (e.g., Assignee is empty)
            return;
        }
        switch(groupDataType) {
            case "number":
                groupKey = Number(groupKey);
                break;
            case "null":
                groupKey = null;
                break;
        }

        cube.filter(fieldKey, groupKey, append ? {add: true} : null);
    }
});
