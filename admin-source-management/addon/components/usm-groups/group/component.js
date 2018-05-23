import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  group,
  isGroupLoading,
  hasMissingRequiredData
} from 'admin-source-management/selectors/group-selectors';
import {
  editGroup,
  saveGroup
} from 'admin-source-management/actions/data-creators/group-creators';

const stateToComputed = (state) => ({
  group: group(state),
  isGroupLoading: isGroupLoading(state),
  hasMissingRequiredData: hasMissingRequiredData(state)
});

const dispatchToActions = (dispatch) => ({
  // edit the group using fully qualified field name (e.g., 'group.name')
  edit(field, value) {
    if (field && value !== undefined) {
      dispatch(editGroup(field, value));
    }
  },
  // save changes to the group
  save() {
    // if the save is succesful, redirect the user to the groups list route
    const onSuccess = () => {
      const transitionToGroups = this.get('transitionToGroups');
      transitionToGroups();
    };
    dispatch(saveGroup(this.get('group'), { onSuccess }));
  }
});

const UsmGroup = Component.extend({
  tagName: 'hbox',
  classNames: ['usm-group', 'flexi-fit'],

  actions: {
    handleNameChange(value) {
      this.send('edit', 'group.name', value);
    },
    handleDescriptionChange(value) {
      this.send('edit', 'group.description', value);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmGroup);
