import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

export default Component.extend({
  tagName: 'footer',
  layout,
  classNames: ['list-footer'],
  itemType: null,
  createItem: null,
  contextualHelp: service(),

  // object with parameters topicId & moduleId for contextual help
  helpId: null,

  @computed('itemType')
  newItemButtonTitle(itemType) {
    return `New ${itemType}`;
  },

  @computed('helpId')
  hasContextualHelp(helpId) {
    if (helpId) {
      const { moduleId, topicId } = helpId;
      return !!(moduleId && topicId);
    }
    return false;
  },

  actions: {
    handleCreateItem() {
      this.get('createItem')();
    },

    goToHelp() {
      const { moduleId, topicId } = this.get('helpId');
      this.get('contextualHelp').goToHelp(moduleId, topicId);
    }
  }

});
