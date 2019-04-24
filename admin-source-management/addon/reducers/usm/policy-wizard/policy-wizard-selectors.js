import reselect from 'reselect';
import { isBlank, isEmpty } from '@ember/utils';
import { lookup } from 'ember-dependency-lookup';
import { isArray } from '@ember/array';
import _ from 'lodash';
import { exceedsLength, isNameInList } from '../util/selector-helpers';
import { ALL_RADIO_OPTIONS as edrPolicyRadioOptions } from './edrPolicy/edr-settings';
import { ALL_RADIO_OPTIONS as windowsLogPolicyRadioOptions } from './windowsLogPolicy/windowsLog-settings';
import { ALL_RADIO_OPTIONS as filePolicyRadioOptions } from './filePolicy/file-settings';
import { edrPolicyValidatorFnMap } from './edrPolicy/edr-selectors';
import { windowsLogPolicyValidatorFnMap } from './windowsLogPolicy/windowsLog-selectors';
import { filePolicyValidatorFnMap } from './filePolicy/file-selectors';

const { createSelector } = reselect;

const _policyWizardState = (state) => state.usm.policyWizard;
export const policy = (state) => _policyWizardState(state).policy;
const _policyOrig = (state) => _policyWizardState(state).policyOrig;
export const steps = (state) => _policyWizardState(state).steps;
export const visited = (state) => _policyWizardState(state).visited;

export const hasPolicyChanged = createSelector(
  _policyOrig, policy,
  (_policyOrig, policy) => {
    return !_.isEqual(_policyOrig, policy);
  }
);

/**
 * Determines if the needed API call(s) have completed loading the required data.
 * Currently we only check the policy, but we may also consider including other data such as
 * policyList, listOfEndpointServers, listOfLogServers, etc...
 */
export const isPolicyLoading = createSelector(
  _policyWizardState,
  (_policyWizardState) => {
    return _policyWizardState.policyFetchStatus === 'wait' || _policyWizardState.policyStatus === 'wait';
  }
);

export const isPolicyFetchError = createSelector(
  _policyWizardState,
  (_policyWizardState) => _policyWizardState.policyFetchStatus === 'error'
);

/**
 * source types (policy types) for the source type dropdown
 * @public
 */
export const sourceTypes = (state) => {
  const features = lookup('service:features');
  const isFilePolicyEnabled = features.isEnabled('rsa.usm.allowFilePolicyCreation');
  let enabledSourceTypes = _policyWizardState(state).sourceTypes;
  if (!isFilePolicyEnabled) {
    enabledSourceTypes = enabledSourceTypes.filter((sourceType) => sourceType.policyType !== 'filePolicy');
  }
  return enabledSourceTypes;
};

/**
 * we need the selected policy sourceType object, but policy.type only has the type string value,
 * so we'll find the policy sourceType object by the type string value
 * @public
 */
export const selectedSourceType = createSelector(
  policy, sourceTypes,
  (policy, sourceTypes) => {
    let selected = null;
    for (let s = 0; s < sourceTypes.length; s++) {
      const sourceType = sourceTypes[s];
      if (policy.policyType === sourceType.policyType) {
        selected = sourceType;
        break;
      }
    }
    return selected;
  }
);

/**
 * the summary list of policies objects
 * @public
 */
export const policyList = (state) => _policyWizardState(state).policyList;

// Validation related selectors
// ----------------------------------------

export const identifyPolicyStepShowErrors = createSelector(
  steps,
  (steps) => {
    return steps[0].showErrors;
  }
);

export const definePolicyStepShowErrors = createSelector(
  steps,
  (steps) => {
    return steps[1].showErrors;
  }
);

/**
 * returns a name validator object with values set for
 * - isError, errorMessage
 * @public
 */
export const nameValidator = createSelector(
  policyList, policy, visited, identifyPolicyStepShowErrors,
  (policyList, policy, visited, stepShowErrors) => {
    let error = false;
    let enableMessage = false;
    let message = '';
    if (isBlank(policy.name)) {
      error = true;
      // only blank value requires visited
      if (stepShowErrors || visited.includes('policy.name')) {
        enableMessage = true;
        message = 'adminUsm.policyWizard.nameRequired';
      }
    } else if (exceedsLength(policy.name, 256)) {
      error = true;
      enableMessage = true;
      message = 'adminUsm.policyWizard.nameExceedsMaxLength';
    } else if (isNameInList(policyList, policy.id, policy.name)) {
      error = true;
      enableMessage = true;
      message = 'adminUsm.policyWizard.nameExists';
    }
    return {
      isError: error,
      showError: enableMessage,
      errorMessage: message
    };
  }
);

/**
 * returns a description validator object with values set for
 * - isError, errorMessage
 * @public
 */
export const descriptionValidator = createSelector(
  policy,
  (policy) => {
    let error = false;
    let enableMessage = false;
    let message = '';
    if (exceedsLength(policy.description, 8000)) {
      error = true;
      enableMessage = true;
      message = 'adminUsm.policyWizard.descriptionExceedsMaxLength';
    }
    return {
      isError: error,
      showError: enableMessage,
      errorMessage: message
    };
  }
);

/**
 * checks each identify-policy-step validator to determine if the whole step is valid
 * @public
 */
export const isIdentifyPolicyStepValid = createSelector(
  nameValidator, descriptionValidator,
  (nameValidator, descriptionValidator) => {
    return nameValidator.isError === false && descriptionValidator.isError === false;
  }
);

// all possible policy settings
const availableSettings = (state) => _policyWizardState(state).availableSettings || {};
// settings selected/added/applied to the right col
const selectedSettings = (state) => _policyWizardState(state).selectedSettings || {};

/**
 * check to see if at least one is selected, used in flash messages
 * @public
 */
export const isPolicySettingsEmpty = createSelector(
  selectedSettings,
  (selectedSettings) => {
    return isEmpty(selectedSettings);
  }
);

/**
 * settings that are available/visible in the left col
 * @public
 */
export const enabledAvailableSettings = createSelector(
  availableSettings,
  (availableSettings) => {
    return availableSettings.filter((el) => el.isEnabled);
  }
);

/**
 * settings (ordered by section) selected/added/applied to the right col
 * @public
 */
export const sortedSelectedSettings = createSelector(
  selectedSettings,
  (selectedSettings) => {
    return _.sortBy(selectedSettings, 'index');
  }
);

/**
 * Map to hold policyType specific radio button settings.
 * @private
 */
const radioOptionsMap = {
  'edrPolicy': edrPolicyRadioOptions,
  'windowsLogPolicy': windowsLogPolicyRadioOptions,
  'filePolicy': filePolicyRadioOptions
};

/**
 * It returns the appropriate radio button option based on the policyType & selectedSettingId
 * @public
 */
export const radioButtonOption = (state, selectedSettingId) => {
  const { policyType } = policy(state);
  const getOptionsById = radioOptionsMap[policyType].find((d) => d.id === selectedSettingId);
  const { options } = getOptionsById;
  return options;
};

/**
 * It returns the appropriate radio button value based on the selectedSettingId
 * @public
 */
export const radioButtonValue = (state, selectedSettingId) => _policyWizardState(state).policy[selectedSettingId];

const _state = (state) => state;

/**
 * Map to hold policyType specific validator functions for settings.
 * If a setting is selected on the right side, its validator is invoked.
 * @private
 */
const validatorFnMap = {
  'edrPolicy': edrPolicyValidatorFnMap,
  'windowsLogPolicy': windowsLogPolicyValidatorFnMap,
  'filePolicy': filePolicyValidatorFnMap
};

export const isDefinePolicyStepValid = createSelector(
  _state, policy, selectedSettings,
  (_state, policy, selectedSettings) => {
    // at least one setting required to save a policy
    let isValid = selectedSettings.length > 0;
    for (let i = 0; i < selectedSettings.length; i++) {
      const el = selectedSettings[i];
      const selectedSettingId = el.id;
      const validator = validatorFnMap[policy.policyType][selectedSettingId];
      if (!el.isHeader && validator) {
        if (isArray(validator)) {
          // call validator for each validator fn in the array
          validator.forEach((entry) => {
            isValid = isValid && entry(_state, selectedSettingId).isError === false;
          });
        } else {
          // call validator function
          isValid = isValid && validator(_state, selectedSettingId).isError === false;
        }
      }
    }
    return isValid;
  }
);

export const isWizardValid = createSelector(
  isIdentifyPolicyStepValid, isDefinePolicyStepValid,
  (isIdentifyPolicyStepValid, isDefinePolicyStepValid) => {
    return isIdentifyPolicyStepValid && isDefinePolicyStepValid;
  }
);
