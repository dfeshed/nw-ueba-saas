/**
 * @file IncidentsCube class.
 * Subclass of Base Cube class that includes properties specifically for an Incidents list.
 */
import Base from "./base";

export default Base.extend({

    // Default fields
    fieldsConfig: {
        "id": {
            dataType: "string"
        },
        "idNumber": {
            dataType: "string",
            propertyName: "id",
            getter: function (d) {
                var match = (d.id || "").match(/\-(\d+)$/);
                return match ? parseInt(match[1], 10) : d.id;
            }
        },
        "name": {
            dataType: "string"
        },
        "priority": {
            dataType: "number",
            propertyName: "prioritySort",
            getter: function (d) {
                return d.prioritySort || 0;
            }
        },
        "assignee": {
            getter: function (d) {
                return (d.assignee && d.assignee.login) || "";
            }
        },
        "status": {
            dataType: "number",
            propertyName: "statusSort",
            getter: function (d) {
                return d.statusSort || 0;
            }
        },
        "priorityRiskScore": {
            dataType: "number",
            getter: function (d) {
                return (d.prioritySort || 0) * 1000 + (d.riskScore || 0);
            }
        }
    },

    // Default sort
    sortField: "priorityRiskScore",
    sortDesc: true
});

