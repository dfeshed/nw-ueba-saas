import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

export default Component.extend({
  layout,

  classNames: ['item-details'],

  editItem: null,

  item: null,

  contextualHelp: service(),

  // object with parameters topicId & moduleId for contextual help
  helpId: null,


  @computed('helpId')
  hasContextualHelp(helpId) {
    if (helpId) {
      const { moduleId, topicId } = helpId;
      return !!(moduleId && topicId);
    }
    return false;
  },

  actions: {

    goToHelp() {
      const { moduleId, topicId } = this.get('helpId');
      this.get('contextualHelp').goToHelp(moduleId, topicId);
    }
  }

});
