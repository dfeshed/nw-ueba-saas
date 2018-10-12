import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import Notifications from 'component-lib/mixins/notifications';
import { inject } from '@ember/service';

import {
  saveGroup,
  savePublishGroup
} from 'admin-source-management/actions/creators/group-wizard-creators';

const dispatchToActions = {
  saveGroup,
  savePublishGroup
};

const GroupWizardToolbar = Component.extend(Notifications, {
  tagName: 'hbox',
  classNames: ['group-wizard-toolbar'],
  i18n: inject(),

  // step object required to be passed in
  step: undefined,
  // closure action required to be passed in
  transitionToStep: undefined,

  @computed()
  isStepValid() {
    return true;
  },

  actions: {

    transitionToPrevStep() {
      this.get('transitionToStep')(this.get('step').prevStepId);
    },

    transitionToNextStep() {
      this.get('transitionToStep')(this.get('step').nextStepId);
    },

    save() {

    },

    cancel() {
      this.get('transitionToClose')();
    }
  }

});

export default connect(undefined, dispatchToActions)(GroupWizardToolbar);