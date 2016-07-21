/**
 * @file Incident model.
 * The Ember Data representation of an Incident.
 * @public
 */
import Model from 'ember-data/model';
import attr from 'ember-data/attr';

export default Model.extend({
  name: attr(),
  alertCount: attr(),
  averageAlertRiskScore: attr(),
  riskScore: attr(),
  createdBy: attr(),
  prioritySort: attr(),
  priority: attr(),
  summary: attr(),
  statusSort: attr(),
  status: attr(),
  assignee: attr(),
  lastUpdated: attr(),
  lastUpdatedByUser: attr(),
  alerts: attr(),
  created: attr(),
  sources: attr(),
  categories: attr(),
  notes: attr()
});
