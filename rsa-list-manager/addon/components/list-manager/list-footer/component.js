import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

export default Component.extend({
  tagName: 'footer',
  layout,
  classNames: ['list-footer'],
  listName: null,
  updateView: null,
  contextualHelp: service(),

  // object with parameters topicId & moduleId for contextual help
  helpId: null,

  @computed('listName')
  newItemButtonTitle(listName) {
    // For a listName e.g My Items (string ending with s(plural)),
    // the button title dispalyed will be New My Item
    return `New ${listName.slice(0, -1)}`;
  },

  actions: {
    handleCreateItem() {
      this.get('updateView')('create-view');
    },

    goToHelp() {
      const { moduleId, topicId } = this.get('helpId');
      this.get('contextualHelp').goToHelp(moduleId, topicId);
    }
  }

});
