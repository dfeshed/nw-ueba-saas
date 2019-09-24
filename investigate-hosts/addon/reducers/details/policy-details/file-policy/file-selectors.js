import reselect from 'reselect';
import { isBlank } from '@ember/utils';
import { lookup } from 'ember-dependency-lookup';
import { policyAdminUsm } from 'investigate-hosts/reducers/details/overview/selectors';

const { createSelector } = reselect;

// to do
const _listOfLogServers = () => [];
const _listOfFileSourceTypes = () => [];

// const _listOfLogServers = (state) => state.usm.policyWizard.listOfLogServers || [];
// const _listOfFileSourceTypes = (state) => state.usm.policyWizard.listOfFileSourceTypes || [];

/**
 * formats the policy in focus to return an array of sections
 * to the file policy details template
 * Each section has a header and a list of properties and their values
 * [settingSection[0], settingSection[1] ...]
 * settingSection: {header: '', props: [{name: '', value:''}]}
 * the values are formatted based on other properties in the policy and
 * concatenating translations if needed
 * @public
*/
export const selectedFilePolicy = createSelector(
  policyAdminUsm, _listOfLogServers, _listOfFileSourceTypes,
  (policyAdminUsm, _listOfLogServers, _listOfFileSourceTypes) => {
    const focusedPolicy = policyAdminUsm?.filePolicy;
    const policyDetails = [];
    const sourceSections = [];
    const basicSettings = [];
    const advancedConfigSettings = [];
    const filePolicyEnabled = focusedPolicy ? focusedPolicy.enabled : '';
    let noSources = false;
    const _i18n = lookup('service:i18n');
    for (const prop in focusedPolicy) {
      if (prop === 'sources' && filePolicyEnabled) {
        if (focusedPolicy.sources.length === 0) {
          noSources = true;
          sourceSections.push(_getSourceSection({ fileType: _i18n.t('adminUsm.policies.detail.noSources') }, _listOfFileSourceTypes, false, noSources));
        } else {
          noSources = false;
          for (let i = 0; i < focusedPolicy.sources.length; i++) {
            const sourceEnabled = focusedPolicy.sources[i].enabled;
            sourceSections.push(_getSourceSection(focusedPolicy.sources[i], _listOfFileSourceTypes, sourceEnabled, noSources));
          }
        }
      } else {
        if (!isBlank(focusedPolicy[prop])) {
          const basicSetting = _getBasicSetting(prop, focusedPolicy, _listOfLogServers, filePolicyEnabled);
          const advancedConfigSetting = _getAdvancedConfigSetting(prop, focusedPolicy, filePolicyEnabled);
          if (basicSetting) {
            basicSettings.push(basicSetting);
          } else if (advancedConfigSetting) {
            advancedConfigSettings.push(advancedConfigSetting);
          }
        }
      }
    }
    if (basicSettings.length > 0) {
      policyDetails.push({
        header: 'adminUsm.policies.detail.fileSettings',
        props: basicSettings
      });
    }
    if (advancedConfigSettings.length > 0) {
      policyDetails.push({
        header: 'adminUsm.policyWizard.filePolicy.advancedConfig',
        props: advancedConfigSettings
      });
    }
    // each source is rendered as separate section
    sourceSections.forEach((sourceSection) => {
      policyDetails.push(sourceSection);
    });
    return policyDetails;
  }
);

const _getBasicSetting = (prop, focusedPolicy, _listOfLogServers, filePolicyEnabled) => {
  const _i18n = lookup('service:i18n');
  let basicSettings = {};
  if (filePolicyEnabled) {
    basicSettings = {
      enabled: {
        name: 'adminUsm.policies.detail.filePolicyEnabled',
        value: _i18n.t('adminUsm.policies.detail.enabled')
      },
      primaryDestination: {
        name: 'adminUsm.policies.detail.primaryDestination',
        value: _getDisplayName(prop, focusedPolicy[prop], _listOfLogServers)
      },
      secondaryDestination: {
        name: 'adminUsm.policies.detail.secondaryDestination',
        value: _getDisplayName(prop, focusedPolicy[prop], _listOfLogServers)
      },
      protocol: {
        name: 'adminUsm.policies.detail.protocol',
        value: focusedPolicy[prop]
      },
      sendTestLog: {
        name: 'adminUsm.policies.detail.sendTestLog',
        value: focusedPolicy[prop] ? _i18n.t('adminUsm.policies.detail.enabled') : _i18n.t('adminUsm.policies.detail.disabled')
      }
    };
  } else {
    basicSettings = {
      enabled: {
        name: 'adminUsm.policies.detail.filePolicyEnabled',
        value: _i18n.t('adminUsm.policies.detail.disabled')
      }
    };
  }
  return basicSettings[prop];
};

const _getAdvancedConfigSetting = (prop, focusedPolicy, filePolicyEnabled) => {
  let advancedConfigSettings = {};
  if (filePolicyEnabled) {
    advancedConfigSettings = {
      customConfig: {
        name: 'adminUsm.policyWizard.edrPolicy.customConfig',
        value: focusedPolicy[prop]
      }
    };
  }
  return advancedConfigSettings[prop];
};

const _getDisplayName = (prop, destAddress, listOfLogServers) => {
  if (prop !== 'primaryDestination' && prop !== 'secondaryDestination') {
    return null;
  }
  let focusedPolicyDestinationName = destAddress;

  const logServer = listOfLogServers.filter((obj) => obj.host === destAddress);
  if (logServer != null && logServer.length === 1) {
    focusedPolicyDestinationName = logServer[0].displayName;
  }
  return focusedPolicyDestinationName;
};

const _getSourceSection = (source, _listOfFileSourceTypes, sourceEnabled, noSources) => {
  const sourceSettings = [];
  for (const prop in source) {
    if (!isBlank(source[prop])) {
      const sourceSetting = _getSourceSetting(prop, source, sourceEnabled, noSources);
      if (sourceSetting) {
        sourceSettings.push(sourceSetting);
      }
    }
  }
  const sourceSection = {
    header: 'adminUsm.policies.detail.sourceSettings',
    headerVars: { fileType: _getFileSourceTypeDisplayName(source.fileType, _listOfFileSourceTypes) },
    props: sourceSettings
  };
  return sourceSection;
};

const _getSourceSetting = (prop, source, sourceEnabled, noSources) => {
  const _i18n = lookup('service:i18n');
  let sourceSettings = {
    enabled: {
      name: 'adminUsm.policyWizard.filePolicy.enableOnAgent',
      value: _i18n.t('adminUsm.policies.detail.disabled')
    }
  };
  if (sourceEnabled) {
    sourceSettings = {
      enabled: {
        name: 'adminUsm.policyWizard.filePolicy.enableOnAgent',
        value: _i18n.t('adminUsm.policies.detail.enabled')
      },
      startOfEvents: {
        name: 'adminUsm.policyWizard.filePolicy.dataCollection',
        value: (source[prop] === true) ? _i18n.t('adminUsm.policyWizard.filePolicy.collectAll') : _i18n.t('adminUsm.policyWizard.filePolicy.collectNew')
      },
      fileEncoding: {
        name: 'adminUsm.policyWizard.filePolicy.fileEncoding',
        value: source[prop]
      },
      paths: {
        name: 'adminUsm.policyWizard.filePolicy.paths',
        value: source[prop] && source[prop].join ? source[prop].join(', ') : ''
      },
      sourceName: {
        name: 'adminUsm.policyWizard.filePolicy.sourceName',
        value: source[prop]
      },
      exclusionFilters: {
        name: 'adminUsm.policyWizard.filePolicy.exclusionFilters',
        value: source[prop] && source[prop].join ? source[prop].join(', ') : ''
      }
    };
  } else if (noSources) {
    sourceSettings = {};
  }
  return sourceSettings[prop];
};

const _getFileSourceTypeDisplayName = (sourceFileType, _listOfFileSourceTypes) => {
  let displayName = sourceFileType;
  const fileSourceType = _listOfFileSourceTypes.filter((obj) => obj.name === sourceFileType);
  if (fileSourceType != null && fileSourceType.length === 1) {
    displayName = fileSourceType[0].prettyName;
  }
  return displayName;
};
