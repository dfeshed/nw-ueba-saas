import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
// import { handle } from 'redux-pack';
import _ from 'lodash';
// import moment from 'moment';
import * as ACTION_TYPES from 'admin-source-management/actions/types';

export const initialState = {
  // the policy object to be created/updated/saved
  policy: {
    type: 'edrPolicy',
    name: '',
    description: '',
    scheduleConfig: {
      enabledScheduledScan: false,
      scheduleOptions: {
        scanStartDate: null, // TODO is this YYYY-MM-DD string?
        scanStartTime: '10:00',
        recurrenceInterval: 5,
        recurrenceIntervalUnit: 'DAYS',
        runOnDaysOfWeek: []
      },
      scanOptions: {
        cpuMaximum: 75,
        cpuMaximumOnVirtualMachine: 85
      }
    }
  },

  // TODO if the reducer doesn't need to modify these, and the selectors aren't doing anything special,
  //   then we may want to extract these to a steps.js and add them directly to the policy-wizard component,
  //   but keep in mind that we may want to dynamically add steps...
  steps: [
    {
      id: 'identifyPolicyStep',
      nextStepId: 'definePolicyStep',
      prevStepId: '',
      title: 'adminUsm.policyWizard.identifyPolicy',
      stepComponent: 'usm-policies/policy-wizard/identify-policy-step',
      toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar'
    },
    {
      id: 'definePolicyStep',
      prevStepId: 'identifyPolicyStep',
      nextStepId: 'applyToGroupStep',
      title: 'adminUsm.policyWizard.definePolicy',
      stepComponent: 'usm-policies/policy-wizard/define-policy-step',
      toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar'
    },
    {
      id: 'applyToGroupStep',
      prevStepId: 'definePolicyStep',
      nextStepId: 'reviewPolicyStep',
      title: 'adminUsm.policyWizard.applyToGroup',
      stepComponent: 'usm-policies/policy-wizard/apply-to-group-step',
      toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar'
    },
    {
      id: 'reviewPolicyStep',
      prevStepId: 'applyToGroupStep',
      nextStepId: '',
      title: 'adminUsm.policyWizard.reviewPolicy',
      stepComponent: 'usm-policies/policy-wizard/review-policy-step',
      toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar'
    }
  ],

  // the policy sourceType objects to fill the select/dropdown
  sourceTypes: [
    { id: 'edrPolicy', type: 'edrPolicy', name: 'EndpointScan', label: 'adminUsm.policyWizard.edrSourceType' }
    // { id: 'fileLogPolicy', type: 'fileLogPolicy', name: 'EndpointFile', label: 'adminUsm.policyWizard.fileLogSourceType' },
    // { id: 'windowsLogPolicy', type: 'windowsLogPolicy', name: 'EndpointWL', label: 'adminUsm.policyWizard.windowsLogSourceType' }
  ],

  policyStatus: null, // wait, complete, error

  // keeps track of the form fields visited by the user
  visited: []
};

export default reduxActions.handleActions({

  [ACTION_TYPES.EDIT_POLICY]: (state, action) => {
    const { field, value } = action.payload;
    const fields = field.split('.');
    // Edit the value in the policy, and keep track of the field as having been visited
    return state.setIn(fields, value).set('visited', _.uniq([...state.visited, field]));
  }

}, Immutable.from(initialState));
