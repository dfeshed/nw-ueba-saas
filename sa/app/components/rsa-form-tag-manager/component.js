import Ember from 'ember';
import computed from 'ember-computed-decorators';

const {
  Component,
  inject: {
    service
  },
  set,
  $
} = Ember;

export default Component.extend({
  classNames: ['rsa-form-tag-manager'],
  classNameBindings: ['contentTreeIsHidden'],
  contentTreeIsHidden: true,
  eventBus: service(),

  // sub set of availableTags selected by the user
  selectedTags: [],

  didInsertElement() {
    this.get('eventBus').on('rsa-application-click', (targetEl) => {
      if (this.$()) {
        // hide the panel when clicking outside the component
        if (!this.get('contentTreeIsHidden') && $.contains(document, targetEl) && !$.contains(this.$()[0], targetEl)) {
          this.toggleProperty('contentTreeIsHidden');
        }
      }
    });
  },

  /**
   * @name tags
   * @description The entire list of available tags. Those that are already selected have `selected` property
   * set to true.
   * @public
   */
  @computed('availableTags', 'selectedTags')
  tags: (availableTags, selectedTags) => {
    availableTags.forEach((tag) => {
      tag.children.forEach((childNode) => {
        set(childNode, 'selected', selectedTags.any((selectedTag) => {
          return selectedTag.name === childNode.name && selectedTag.parent === tag.name;
        }));
      });
    });
    return availableTags;
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
     * @name tagSelected
     * @description Adds or Removes a selected tag from the array containing the chosen tags.
     * @public
     */
    tagSelected(tag) {

      let newArray = this.get('selectedTags').slice(0);

      let existingTag = this.get('selectedTags').find((item) => {
        return item.parent === tag.parent && item.name === tag.name;
      });

      if (!existingTag) {
        newArray.pushObject(tag);
      } else {
        newArray.removeObject(existingTag);
      }
      this.set('selectedTags', newArray);
    }
  }
});
