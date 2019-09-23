import Component from '@ember/component';
import layout from './template';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { beginCreateItem } from 'rsa-list-manager/actions/creators/creators';
import { newItemButtonTitle, helpId, hasContextualHelp, editItem } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  newItemButtonTitle: newItemButtonTitle(state, attrs.stateLocation),
  helpId: helpId(state, attrs.stateLocation),
  hasContextualHelp: hasContextualHelp(state, attrs.stateLocation),
  editItem: editItem(state, attrs.stateLocation)
});

const dispatchToActions = {
  beginCreateItem
};

const ListFooter = Component.extend({
  tagName: 'footer',
  layout,
  classNames: ['list-footer'],
  stateLocation: undefined,
  contextualHelp: service(),

  actions: {
    goToHelp() {
      const { moduleId, topicId } = this.get('helpId');
      this.get('contextualHelp').goToHelp(moduleId, topicId);
    },

    createItem() {
      this.send('beginCreateItem', this.get('stateLocation'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ListFooter);
