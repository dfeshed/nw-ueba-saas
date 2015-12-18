/**
 * @file IncidentsCube class.
 * Subclass of Base Cube class that includes properties specifically for an Incidents list.
 * @public
 */
import Base from './base';

export default Base.extend({

  // Default fields
  fieldsConfig: {
    'id': {
      dataType: 'string'
    },
    'idNumber': {
      dataType: 'string',
      propertyName: 'id',
      getter(d) {
        let match = (d.id || '').match(/\-(\d+)$/);
        return match ? parseInt(match[1], 10) : d.id;
      }
    },
    'name': {
      dataType: 'string'
    },
    'priority': {
      dataType: 'number',
      propertyName: 'prioritySort',
      getter(d) {
        return d.prioritySort || 0;
      }
    },
    'assignee': {
      getter(d) {
        return (d.assignee && d.assignee.login) || '';
      }
    },
    'status': {
      dataType: 'number',
      propertyName: 'statusSort',
      getter(d) {
        return d.statusSort || 0;
      }
    },
    'priorityRiskScore': {
      dataType: 'number',
      getter(d) {
        return (d.prioritySort || 0) * 1000 + (d.riskScore || 0);
      }
    }
  },

  // Default sort
  sortField: 'priorityRiskScore',
  sortDesc: true
});
