import reselect from 'reselect';
const { createSelector } = reselect;
import { languageAndAliasesForParser } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { transformTextToPillData } from 'investigate-events/util/query-parsing';

// UTIL
const _profileWithIsEditable = (item) => ({
  ...item,
  isEditable: item.contentType && item.contentType !== 'OOTB'
});

/**
 * enriches and returns one profile with isEditable and preQueryPillsData
 * @param {object} profile
 * @param {object} languageAndAliases { language, aliases }
 */
export const enrichedProfile = (profile, languageAndAliases) => {
  const { language, aliases } = languageAndAliases;
  return {
    ..._profileWithIsEditable(profile),
    preQueryPillsData: profile.preQuery ? transformTextToPillData(profile.preQuery, { language, aliases, returnMany: true }) : []
  };
};

// ACCESSOR FUNCTIONS
const _originalProfiles = (state) => state.investigate.profile.profiles || undefined;
const _updatedProfiles = (state) => state.listManagers?.profiles?.list;
export const languageAndAliases = (state) => languageAndAliasesForParser(state);
export const isProfileViewActive = (state) => state.listManagers?.profiles?.isExpanded;

// SELECTORS
export const profiles = createSelector(
  [_originalProfiles, _updatedProfiles],
  (originalProfiles = [], updatedProfiles) => {
    // refer to listManager's profiles for up to date list
    if (updatedProfiles) {
      return updatedProfiles;
    }
    // return original only if listManager state doesn't have updated profiles
    return originalProfiles;
  }
);

export const enrichedProfiles = createSelector(
  [profiles, languageAndAliases],
  (profiles = [], languageAndAliases) => {
    return profiles.map((profile) => {
      return enrichedProfile(profile, languageAndAliases);
    });
  }
);
