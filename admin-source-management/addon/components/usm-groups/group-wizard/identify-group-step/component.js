import Component from '@ember/component';
import { connect } from 'ember-redux';
import Notifications from 'component-lib/mixins/notifications';
import {
  group,
  nameValidator,
  descriptionValidator
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

import {
  editGroup
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  group: group(state),
  nameValidator: nameValidator(state),
  descriptionValidator: descriptionValidator(state)
});

const dispatchToActions = {
  editGroup
};

const IdentifyGroupStep = Component.extend(Notifications, {
  tagName: 'hbox',
  classNames: ['identify-group-step', 'scroll-box', 'rsa-wizard-step'],

  actions: {
    handleNameChange(value) {
      if (value !== undefined) {
        this.send('editGroup', 'group.name', value.trim());
      }
    },
    handleDescriptionChange(value) {
      if (value !== undefined) {
        this.send('editGroup', 'group.description', value.trim());
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(IdentifyGroupStep);
