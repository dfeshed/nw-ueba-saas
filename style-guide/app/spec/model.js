import Ember from 'ember';
import Model from 'ember-data/model';
import attr from 'ember-data/attr';
import computed, { or } from 'ember-computed-decorators';

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

  @or('testFilter', 'jsRepo', 'styleRepo', 'templateRepo') hasReferenceLinks,

  @computed('testFilter')
  testFilterURL(filter) {
    let prefix = '/tests?filter=';
    if (filter) {
      return prefix.concat(filter);
    }
  },

  @computed('dataType')
  isComponent(dataType) {
    return dataType === 'comp' || dataType === 'demoComp';
  }

});
