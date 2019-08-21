import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import {
  VALID_HOSTNAME_REGEX,
  VALID_IPV4_REGEX,
  VALID_IPV6_REGEX
} from '../../util/selector-helpers';
import { getFileSourceTypeDisplayName } from 'admin-source-management/reducers/usm/policy-details/file-policy/file-selectors';
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
  policy, _listOfFileSourceTypes,
  (policy, _listOfFileSourceTypes) => {
    const sources = policy.sources.map((source) => {
      return { ...source, fileTypePrettyName: getFileSourceTypeDisplayName(source.fileType, _listOfFileSourceTypes) };
    });
    return sources;
  }
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
        fileEncoding: 'UTF-8 / ASCII',
        enabled: true,
        startOfEvents: true,
        sourceName: '',
        exclusionFilters: [],
        paths: selectedFileSource.paths
      };
      return defaults;
    }
  }
);

export const sourceConfig = () => SOURCE_CONFIG;

export const sources = createSelector(
  policy,
  (policy) => policy.sources
);

/**
 * validates the array elments in the sources array.
 * Sources is an array of objects
 * [ { fileType: 'apache', fileEncoding: 'UTF-8', enabled: true, startOfEvents: false, sourceName: 'name', exclusionFilters:
 * ['filter-1', 'filter-2'] },  { fileType: 'exchange', fileEncoding: 'UTF-8', enabled: true, startOfEvents: false, sourceName:
 * 'name', exclusionFilters: ['filter-1', 'filter-2'] }];
 * @public
 */
export const sourceNameValidator = (state) => {
  let error = false;
  let message = '';
  let invalidEntry = 'invalid';
  const value = sources(state);
  let invalidDirPath = 'invalid';
  let dirPathEmptyMsg = '';
  let dirPathLength = '';

  if (value) {
    // sources is an array of objects, loop through each obj and validate the sourceName within
    value.every((obj) => {
      const { sourceName, paths } = obj;
      // sourceName cannot be an invalid hostname or IPv4 or IPv6, it can be blank since it is optional
      if (!isBlank(sourceName) && !(VALID_HOSTNAME_REGEX.test(sourceName) || VALID_IPV4_REGEX.test(sourceName) || VALID_IPV6_REGEX.test(sourceName))) {
        error = true;
        invalidEntry = sourceName;
        message = 'adminUsm.policyWizard.filePolicy.invalidSourceName';
        return false;
      }
      // If paths is an empty array, show an error
      if (paths && paths.length === 0) {
        error = true;
        dirPathEmptyMsg = 'adminUsm.policyWizard.filePolicy.dirPathEmpty';
        dirPathLength = 0;
        return false;
      }
      if (paths) {
        for (let s = 0; s < paths.length; s++) {
          const path = paths[s];
          if (isBlank(path)) {
            error = true;
            invalidDirPath = path;
            message = 'adminUsm.policyWizard.filePolicy.invalidDirPath';
            return false;
          }
        }
      }
      return true;
    });
  }
  return {
    isError: error,
    errorMessage: message,
    invalidTableItem: invalidEntry,
    invalidPath: invalidDirPath,
    dirPathEmptyMsg,
    dirPathLength
  };
};

/**
 * validates the exclusion Filters array in the sources array.
 * exclusionFilters is an array of strings
 * exclusionFilters: ['filter-1', 'filter-2']
 * @public
 */
export const exFilterValidator = (state) => {
  let error = false;
  let enableMessage = false;
  let message = '';
  const value = sources(state);

  if (value) {
    // sources is an array of objects, loop through each obj and validate the sourceName within
    value.every((obj) => {
      const { exclusionFilters } = obj;
      if (exclusionFilters && exclusionFilters.length > 16) {
        error = true;
        enableMessage = true;
        message = 'adminUsm.policyWizard.filePolicy.exclusionFiltersLengthError';
        return false;
      }
      if (exclusionFilters && exclusionFilters.length > 0) {
        for (let s = 0; s < exclusionFilters.length; s++) {
          const filter = exclusionFilters[s];
          try {
            new RegExp(filter);
          } catch (e) {
            error = true;
            enableMessage = true;
            message = 'adminUsm.policyWizard.filePolicy.exclusionFiltersSyntaxError';
            return false;
          }
        }
      }
      return true;
    });
  }
  return {
    isError: error,
    showError: enableMessage,
    errorMessage: message
  };
};

/**
 * Map to hold all File Policy validator functions for settings
 * @public
 */
export const filePolicyValidatorFnMap = {
  'primaryDestination': windowsLogDestinationValidator,
  'secondaryDestination': windowsLogDestinationValidator
};
