import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  group,
  osTypes,
  selectedOsTypes,
  osDescriptions,
  selectedOsDescriptions,
  policies,
  selectedPolicy,
  isGroupLoading,
  hasMissingRequiredData
} from 'admin-source-management/selectors/group-selectors';
import {
  editGroup,
  saveGroup
} from 'admin-source-management/actions/data-creators/group-creators';

const stateToComputed = (state) => ({
  group: group(state),
  osTypes: osTypes(state),
  selectedOsTypes: selectedOsTypes(state),
  osDescriptions: osDescriptions(state),
  selectedOsDescriptions: selectedOsDescriptions(state),
  policies: policies(state),
  selectedPolicy: selectedPolicy(state),
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
  },
  // cancel changes to the group
  cancel() {
    const transitionToGroups = this.get('transitionToGroups');
    transitionToGroups();
  }
});

const UsmGroup = Component.extend({
  tagName: 'hbox',
  classNames: ['usm-group', 'scroll-box'],

  actions: {
    handleNameChange(value) {
      this.send('edit', 'group.name', value);
    },
    handleDescriptionChange(value) {
      this.send('edit', 'group.description', value);
    },
    handleiIpRangeStartChange(value) {
      this.send('edit', 'group.ipRangeStart', value);
    },
    handleiIpRangeEndChange(value) {
      this.send('edit', 'group.ipRangeEnd', value);
    },
    handleOsTypeChange(value) {
      // power-select-multiple passes an array of objects, we only want the ID's
      this.send('edit', 'group.osTypes', value.map((osType) => osType.id));
      this.send('handleOsDescriptionChange', this.get('selectedOsDescriptions'));
    },
    handleOsDescriptionChange(value) {
      // power-select-multiple passes an array of objects, we only want the ID's
      this.send('edit', 'group.osDescriptions', value.map((osDescription) => osDescription.id));
    },
    handlePolicyChange(value) {
      // power-select passes the whole object, we want a map of { 'type': 'policyID' }
      const policyMap = {};
      policyMap[value.type] = value.id; // ex. { 'edrPolicy': 'id_abc123' }
      this.send('edit', 'group.policy', policyMap);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(UsmGroup);
