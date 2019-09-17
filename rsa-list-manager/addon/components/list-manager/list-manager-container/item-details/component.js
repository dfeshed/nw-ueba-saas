import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import { itemType } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  itemType: itemType(state, attrs.listLocation)
});

const ItemDetails = Component.extend({
  layout,

  classNames: ['item-details'],

  listLocation: undefined,

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

export default connect(stateToComputed)(ItemDetails);
