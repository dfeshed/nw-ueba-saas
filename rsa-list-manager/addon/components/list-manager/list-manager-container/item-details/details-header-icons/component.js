import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import {
  helpId,
  hasContextualHelp
} from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  hasContextualHelp: hasContextualHelp(state, attrs.stateLocation),
  helpId: helpId(state, attrs.stateLocation)
});

const DetailsHeaderIcons = Component.extend({
  layout,
  classNames: ['details-header-icons'],
  contextualHelp: service(),
  stateLocation: undefined,

  actions: {
    goToHelp() {
      const { moduleId, topicId } = this.get('helpId');
      this.get('contextualHelp').goToHelp(moduleId, topicId);
    }
  }
});

export default connect(stateToComputed)(DetailsHeaderIcons);
