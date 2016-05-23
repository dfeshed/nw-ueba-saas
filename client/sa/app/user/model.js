import DS from 'ember-data';

export default DS.Model.extend({
  email: DS.attr(),
  firstName: DS.attr(),
  lastName: DS.attr(),
  friendlyName: DS.attr()
});
