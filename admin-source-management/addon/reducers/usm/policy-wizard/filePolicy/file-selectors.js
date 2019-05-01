import {
  windowsLogDestinationValidator
} from 'admin-source-management/reducers/usm/policy-wizard/windowsLogPolicy/windowsLog-selectors';
/**
 * Map to hold all File Policy validator functions for settings
 * @public
 */
export const filePolicyValidatorFnMap = {
  'primaryDestination': windowsLogDestinationValidator,
  'secondaryDestination': windowsLogDestinationValidator
};