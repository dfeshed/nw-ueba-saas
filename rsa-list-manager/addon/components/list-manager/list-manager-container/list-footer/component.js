import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { newItemButtonTitle } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  newItemButtonTitle: newItemButtonTitle(state, attrs.listLocation)
});

const ListFooter = Component.extend({
  tagName: 'footer',
  layout,
  classNames: ['list-footer'],
  listLocation: undefined,
  createItem: null,
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
    handleCreateItem() {
      this.get('createItem')();
    },

    goToHelp() {
      const { moduleId, topicId } = this.get('helpId');
      this.get('contextualHelp').goToHelp(moduleId, topicId);
    }
  }
});

export default connect(stateToComputed)(ListFooter);
