import DS from 'ember-data';

export default DS.Model.extend({

  grid: DS.attr(),

  palette: DS.attr(),

  dataType: DS.attr(),

  title: DS.attr(),

  testFilter: DS.attr(),

  subtitle: DS.attr(),

  description: DS.attr(),

  categories: DS.attr(),

  jsRepo: DS.attr(),

  styleRepo: DS.attr(),

  templateRepo: DS.attr(),

  code: DS.attr(),

  testFilterURL: (function() {
    let prefix = '/tests?filter=',
        filter = this.get('testFilter');

    if (filter) {
      return prefix.concat(filter);
    }
  }).property('testFilter'),

  hasReferenceLinks: (function() {
    return this.get('testFilter') || this.get('jsRepo') || this.get('styleRepo') || this.get('templateRepo');
  }).property('testFilter', 'jsRepo', 'styleRepo', 'templateRepo'),

  isComponent: (function() {
    return this.get('dataType') === 'comp' || this.get('dataType') === 'demoComp';
  }).property('dataType')

});
