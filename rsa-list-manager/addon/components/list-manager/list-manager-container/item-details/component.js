import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import { itemType, helpId, hasContextualHelp, editItem } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  itemType: itemType(state, attrs.stateLocation),
  helpId: helpId(state, attrs.stateLocation),
  hasContextualHelp: hasContextualHelp(state, attrs.stateLocation),
  item: editItem(state, attrs.stateLocation)
});

const ItemDetails = Component.extend({
  layout,
  classNames: ['item-details'],
  stateLocation: undefined,
  contextualHelp: service(),

  actions: {
    goToHelp() {
      const { moduleId, topicId } = this.get('helpId');
      this.get('contextualHelp').goToHelp(moduleId, topicId);
    }
  }
});

export default connect(stateToComputed)(ItemDetails);
