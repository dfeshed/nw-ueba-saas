import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import {
  group,
  nameValidator,
  descriptionValidator
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

import {
  editGroup
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  groupName: group(state).name,
  groupDescription: group(state).description,
  nameValidator: nameValidator(state),
  descriptionValidator: descriptionValidator(state)
});

const dispatchToActions = {
  editGroup
};

const IdentifyGroupStep = Component.extend({
  tagName: 'hbox',
  classNames: ['identify-group-step', 'scroll-box', 'rsa-wizard-step'],

  // Computed properties for name and description
  // This handles the issue with focus and loss of first key input
  // Couldn't find any other binding type that worked correctly
  @computed('groupName')
  name(groupName) {
    if (groupName) {
      return groupName;
    }
  },

  @computed('groupDescription')
  description(groupDescription) {
    if (groupDescription) {
      return groupDescription;
    }
  },

  // edit the group using fully qualified field name (e.g., 'group.name')
  edit(field, value) {
    if (field && value !== undefined) {
      this.send('editGroup', field, value);
    }
  },

  actions: {
    handleNameChange(value) {
      this.edit('group.name', value.trim());
    },

    handleDescriptionChange(value) {
      this.edit('group.description', value.trim());
    }

  }
});

export default connect(stateToComputed, dispatchToActions)(IdentifyGroupStep);
