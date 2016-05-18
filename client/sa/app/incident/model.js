/**
 * @file Incident model.
 * The Ember Data representation of an Incident.
 * @public
 */
import DS from 'ember-data';

export default DS.Model.extend({
  name: DS.attr(),
  alertCount: DS.attr(),
  averageAlertRiskScore: DS.attr(),
  riskScore: DS.attr(),
  createdBy: DS.attr(),
  prioritySort: DS.attr(),
  summary: DS.attr(),
  statusSort: DS.attr(),
  assignee: DS.attr(),
  lastUpdated: DS.attr(),
  lastUpdatedByUser: DS.attr(),
  alerts: DS.attr(),
  created: DS.attr(),
  sources: DS.attr(),
  categories: DS.attr(),
  notes: DS.attr()
});
