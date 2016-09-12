import Ember from 'ember';
import computed from 'ember-computed-decorators';
const { Component, isNone } = Ember;

export default Component.extend({
  classNames: ['rsa-form-tag-manager'],
  contentTreeIsHidden: true,

  /**
   * @name displayedTags
   * @description An array containing the string representations of the tags selected.
   * @public
   */
  @computed('selectedTags')
  displayedTags: (selectedTags) => {
    let displayedTagsArray = [];
    let foundSelectedTags = selectedTags || [];

    foundSelectedTags.forEach(function(tagObject) {
      displayedTagsArray.push(`${tagObject.parent}: ${tagObject.name}`);
    });
    return displayedTagsArray;
  },

  actions: {
    /**
     * @name toggleTreeVisibility
     * @description Hides and shows the rsa-content-tree component.
     * @public
     */
    toggleTreeVisibility() {
      this.toggleProperty('contentTreeIsHidden');
    },

    /**
     * @name addTag
     * @description Adds a selected tag to both the array containing the visually displayed tags as well as the model containing the chosen tags.
     * @public
     */
    addTag(parentNode, childNode) {
      let nodeObject = {
        'parent': parentNode,
        'name': childNode
      };

      if (!this.get('selectedTags').find((item) => {
        return item.parent === parentNode && item.name === childNode;
      })) {
        let newArray = this.get('selectedTags').slice(0);
        newArray.pushObject(nodeObject);
        this.set('selectedTags', newArray);
      }
    },

    /**
     * @name removeTag
     * @description Removes a tag from the displayedTags array which is displayed visually on the page and updates the model.categories parameter with the currently selected tags.
     * @public
     */
    removeTag(displayedTagsString) {
      let deletedTagArray = displayedTagsString.split(': ');
      let updatedArray = this.get('selectedTags').slice(0);

      updatedArray.find(function(item) {
        if (!isNone(item) && item.parent === deletedTagArray[0] && item.name === deletedTagArray[1]) {
          updatedArray.removeObject(item);
        }
      });

      this.set('selectedTags', updatedArray);
    }
  }
});
