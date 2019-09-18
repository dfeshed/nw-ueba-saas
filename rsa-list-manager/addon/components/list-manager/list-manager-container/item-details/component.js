import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import { itemType, helpId, hasContextualHelp } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  itemType: itemType(state, attrs.listLocation),
  helpId: helpId(state, attrs.listLocation),
  hasContextualHelp: hasContextualHelp(state, attrs.listLocation)
});

const ItemDetails = Component.extend({
  layout,
  classNames: ['item-details'],
  listLocation: undefined,
  editItem: null,
  item: null,
  contextualHelp: service(),

  actions: {
    goToHelp() {
      const { moduleId, topicId } = this.get('helpId');
      this.get('contextualHelp').goToHelp(moduleId, topicId);
    }
  }
});

export default connect(stateToComputed)(ItemDetails);
