import Component from '@ember/component';
import layout from './template';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { newItemButtonTitle, helpId, hasContextualHelp } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  newItemButtonTitle: newItemButtonTitle(state, attrs.stateLocation),
  helpId: helpId(state, attrs.stateLocation),
  hasContextualHelp: hasContextualHelp(state, attrs.stateLocation)
});

const ListFooter = Component.extend({
  tagName: 'footer',
  layout,
  classNames: ['list-footer'],
  stateLocation: undefined,
  createItem: null,
  contextualHelp: service(),

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
