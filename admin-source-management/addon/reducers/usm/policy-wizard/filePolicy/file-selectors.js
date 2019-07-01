import reselect from 'reselect';
import {
  SOURCE_CONFIG
} from './file-settings';
import {
  windowsLogDestinationValidator
} from 'admin-source-management/reducers/usm/policy-wizard/windowsLogPolicy/windowsLog-selectors';

const { createSelector } = reselect;

const _policyWizardState = (state) => state.usm.policyWizard;
const policy = (state) => _policyWizardState(state).policy;

const _listOfFileSourceTypes = (state) => _policyWizardState(state).listOfFileSourceTypes || [];

export const fileSources = createSelector(
  policy,
  (policy) => policy.sources
);

export const fileSourcesList = createSelector(
  [_listOfFileSourceTypes],
  (listOfFileSourceTypes) => {
    const fileTypes = [];
    for (let i = 0; i < listOfFileSourceTypes.length; i++) {
      const fileType = {
        prettyName: listOfFileSourceTypes[i].prettyName,
        name: listOfFileSourceTypes[i].name,
        paths: listOfFileSourceTypes[i].sourceDefaults.filePaths
      };
      fileTypes.push(fileType);
    }
    return fileTypes;
  }
);

/**
 * Returns the selected file source
 * @public
 * @return  {obj} {name: "apache", prettyName: "apache"}
 */
export const selectedFileSource = createSelector(
  policy, fileSourcesList,
  (policy, fileSourcesList) => {
    let selected = null;

    for (let s = 0; s < fileSourcesList.length; s++) {
      const source = fileSourcesList[s];
      if (policy.selectedFileSource === source.name) {
        selected = source;
        break;
      }
    }
    return selected;
  }
);

/**
 * Returns the default settings for a selected file source
 * @public
 * @return  {obj} {fileType: "apache", fileEncoding: "UTF-8", ..., paths: ["/c/apache", "/c/apache/logs"]}
 */
export const selectedFileSourceDefaults = createSelector(
  selectedFileSource,
  (selectedFileSource) => {
    if (selectedFileSource) {
      const defaults = {
        fileType: selectedFileSource.name,
        fileEncoding: 'UTF-8',
        enabled: true,
        startOfEvents: false,
        sourceName: '',
        exclusionFilters: [],
        paths: selectedFileSource.paths
      };
      return defaults;
    }
  }
);

export const sourceConfig = () => SOURCE_CONFIG;

/**
 * Map to hold all File Policy validator functions for settings
 * @public
 */
export const filePolicyValidatorFnMap = {
  'primaryDestination': windowsLogDestinationValidator,
  'secondaryDestination': windowsLogDestinationValidator
};
