// import { _policyWizardState, policy } from '../policy-wizard-selectors';
import { ALL_RADIO_OPTIONS } from './windowsLog-settings';

// TODO imports from policy-wizard-selectors are undefined...
const _policyWizardState = (state) => state.usm.policyWizard;
// const policy = (state) => _policyWizardState(state).policy;

/**
 * It returns the appropriate radio button option based on the selectedSettingId
 * @public
 */
export const radioButtonOption = (selectedSettingId) => {
  const getOptionsById = ALL_RADIO_OPTIONS.find((d) => d.id === selectedSettingId);
  const { options } = getOptionsById;
  return options;
};

/**
 * It returns the appropriate radio button value based on the selectedSettingId
 * @public
 */
export const radioButtonValue = (state, selectedSettingId) => _policyWizardState(state).policy[selectedSettingId];

/**
 * Map to hold all Windows Log Policy validator functions for settings
 * @public
 */
export const windowsLogPolicyValidatorFnMap = {
};
