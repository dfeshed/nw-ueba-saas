import DS from 'ember-data';

export default DS.Model.extend({
  dataType: DS.attr(),
  title: DS.attr(),
  subtitle: DS.attr(),
  description: DS.attr(),
  categories: DS.attr()
});
