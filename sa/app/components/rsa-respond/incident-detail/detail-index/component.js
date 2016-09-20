import Ember from 'ember';
import computed from 'ember-computed-decorators';

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
    if (categoryTags === null) {
      return [];
    }
    let data = [];

    categoryTags.forEach((obj) => {
      let objParent = data.findBy('name', obj.parent);

      if (!objParent) {
        objParent = {
          'name': obj.parent,
          'children': []
        };
        data.pushObject(objParent);
      }

      objParent.children.pushObject({
        id: obj.id,
        name: obj.name
      });
    });
    return data;
  },

  users: null,

  @computed('model.categories.[]')
  chosenIncidentTags: {
    get: (categories) => (categories),

    set(value) {
      let flatArray = value.map(function(tagObject) {
        return {
          'parent': tagObject.parent,
          'name': tagObject.name
        };
      });

      this.sendAction('saveAction', 'categories', flatArray);
      return value;
    }
  },

  actions: {
    toggleFullWidthPanel(panel) {
      this.get('layoutService').toggleFullWidthPanel(panel);
    }
  }
});
