import Ember from 'ember';

const {
  Component,
  inject: {
    service
  },
  computed
} = Ember;

export default Component.extend({
  layoutService: service('layout'),
  tagName: 'vbox',

  normalizedTreeData: computed('categoryTags', function() {
    let categoryTags = this.get('categoryTags');
    let data = [];

    categoryTags.forEach((obj) => {
      let objParent = data.findBy('name', obj.get('parent'));

      if (!objParent) {
        objParent = {
          'name': obj.get('parent'),
          'children': []
        };
        data.pushObject(objParent);
      }

      objParent.children.pushObject({
        id: obj.get('id'),
        name: obj.get('name')
      });
    });
    return data;
  }),

  chosenIncidentTags: computed('model.categories', {
    get() {
      return this.get('model.categories');
    },
    set(key, value) {
      let flatArray = value.map(function(tagObject) {
        return {
          'parent': tagObject.parent,
          'name': tagObject.name
        };
      });

      this.sendAction('saveAction', 'categories', flatArray);
      return value;
    }
  }),

  actions: {
    toggleFullWidthPanel(panel) {
      this.get('layoutService').toggleFullWidthPanel(panel);
    }
  }
});
