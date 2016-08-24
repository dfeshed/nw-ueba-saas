import Ember from 'ember';

const { Component, isNone } = Ember;

export default Component.extend({
  classNames: ['rsa-form-tag-manager'],
  contentTreeIsHidden: true,
  selectedTags: null,

  actions: {
    toggleTreeVisibility() {
      this.toggleProperty('contentTreeIsHidden');
    },

    addTag(parentNode, childNode) {
      if (isNone(this.get('selectedTags'))) {
        this.set('selectedTags', []);
      }

      let fullNodeName = `${parentNode}: ${childNode}`;

      if (!this.get('selectedTags').contains(fullNodeName)) {
        this.get('selectedTags').pushObject(fullNodeName);
      }
    },

    removeTag(record) {
      this.get('selectedTags').removeObject(record);
    }
  }
});
