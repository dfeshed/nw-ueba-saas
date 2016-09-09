import Model from 'ember-data/model';
import attr from 'ember-data/attr';

export default Model.extend({
  journalId: attr(),
  incidentId: attr(),
  journalMap: attr()
});
