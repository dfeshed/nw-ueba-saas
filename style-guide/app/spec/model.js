import Ember from 'ember';
import Model from 'ember-data/model';
import attr from 'ember-data/attr';

const { computed } = Ember;

export default Model.extend({

  grid: attr(),

  palette: attr(),

  dataType: attr(),

  title: attr(),

  testFilter: attr(),

  subtitle: attr(),

  description: attr(),

  categories: attr(),

  jsRepo: attr(),

  styleRepo: attr(),

  templateRepo: attr(),

  code: attr(),

  testFilterURL: computed('testFilter', function() {
    let prefix = '/tests?filter=';
    let filter = this.get('testFilter');

    if (filter) {
      return prefix.concat(filter);
    }
  }),

  hasReferenceLinks: computed('testFilter', 'jsRepo', 'styleRepo', 'templateRepo', function() {
    return this.get('testFilter') || this.get('jsRepo') || this.get('styleRepo') || this.get('templateRepo');
  }),

  isComponent: computed('dataType', function() {
    return this.get('dataType') === 'comp' || this.get('dataType') === 'demoComp';
  })

});
