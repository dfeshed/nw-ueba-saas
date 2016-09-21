import Ember from 'ember';
import computed from 'ember-computed-decorators';
import IncidentHelper from 'sa/incident/helpers';

const {
  Component,
  inject: {
    service
  }
} = Ember;

export default Component.extend({
  layoutService: service('layout'),

  tagName: 'vbox',

  @computed('categoryTags.[]')
  normalizedTreeData(categoryTags) {
    return IncidentHelper.normalizeCategoryTags(categoryTags);
  },

  users: null,

  @computed('model.categories.[]')
  chosenIncidentTags: {
    get: (categories) => (categories),

    set(value) {
      this.sendAction('saveAction', 'categories', value);
      return value;
    }
  },

  actions: {
    toggleFullWidthPanel(panel) {
      this.get('layoutService').toggleFullWidthPanel(panel);
    }
  }
});
