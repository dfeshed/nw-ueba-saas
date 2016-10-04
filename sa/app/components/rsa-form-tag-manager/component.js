import Ember from 'ember';
import computed from 'ember-computed-decorators';

const {
  Component,
  inject: {
    service
  },
  get,
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
   * @description Return the entire list of available tags with an adition attribute `selected` which indicates
   * if the tag should be displayed as selected in the list.
   * This is only executed ONE TIME when the component is loaded.
   * @public
   */
  @computed('availableTags.[]')
  tags(availableTags) {
    let selectedTags = this.get('selectedTags');
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
     * @name toggleTag
     * @description Adds or Removes a selected tag from the array containing the chosen tags.
     * @public
     */
    toggleTag(tag) {
      let selectedTags = this.get('selectedTags').slice(0);
      let availableTags = this.get('availableTags');
      let tagFromAvailableTags = null;

      // searching the selected tag in the list of all tags.
      // using for loop instead of forEach to be able to break it once we found the inner element.
      const len = availableTags.length;
      for (let i = 0; i < len; i++) {
        const parent = availableTags.objectAt(i);
        if (parent.name === tag.parent) {
          tagFromAvailableTags = parent.children.find((child) => child.name === tag.name);
          break;
        }
      }

      let isSelected = get(tagFromAvailableTags, 'selected');
      if (isSelected) {
        let existingTag = this.get('selectedTags').find((item) => {
          return item.parent === tag.parent && item.name === tag.name;
        });
        selectedTags.removeObject(existingTag);
      } else {
        selectedTags.pushObject(tag);
      }
      set(tagFromAvailableTags, 'selected', !isSelected);

      this.set('selectedTags', selectedTags);
    }
  }
});
