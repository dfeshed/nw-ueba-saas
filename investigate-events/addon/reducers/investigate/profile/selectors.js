import reselect from 'reselect';
const { createSelector } = reselect;
import { languageAndAliasesForParser } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { columnGroups } from 'investigate-events/reducers/investigate/column-group/selectors';
import { transformTextToPillData } from 'investigate-events/util/query-parsing';

// UTIL
const _profileWithIsEditable = (item) => ({
  ...item,
  isEditable: item.contentType && item.contentType !== 'OOTB'
});

const _isEnriched = (profile) => {
  return profile.hasOwnProperty('preQueryPillsData') && profile.hasOwnProperty('isEditable');
};

/**
 * enriches and returns one profile with isEditable and preQueryPillsData
 * and column group set to SUMMARY if missing
 * @param {object} profile
 * @param {object} languageAndAliases { language, aliases }
 * @param {object[]} columnGroups
 */
export const enrichedProfile = (profile, languageAndAliases, columnGroups) => {
  const { language, aliases } = languageAndAliases;
  let enriched = profile;

  if (!_isEnriched(profile)) {
    enriched = {
      ..._profileWithIsEditable(profile),
      preQueryPillsData: profile.preQuery ? transformTextToPillData(profile.preQuery, { language, aliases, returnMany: true }) : []
    };
  }

  // if profile was returned from API without columnGroup property
  // assign the summary column group
  if (!enriched.columnGroup) {
    enriched.columnGroup = columnGroups?.find(({ id }) => id === 'SUMMARY');
  }

  return enriched;
};

// ACCESSOR FUNCTIONS
const _originalProfiles = (state) => state.investigate?.profile?.profiles || undefined;
const _updatedProfiles = (state) => state.listManagers?.profiles?.list || undefined;
export const languageAndAliases = (state) => languageAndAliasesForParser(state);
export const isProfileViewActive = (state) => state.listManagers?.profiles?.isExpanded;

// SELECTORS
export const profiles = createSelector(
  [_originalProfiles, _updatedProfiles],
  (originalProfiles, updatedProfiles) => {
    // refer to listManager's profiles for up to date list
    if (updatedProfiles) {
      return updatedProfiles;
    }
    // return original only if listManager state doesn't have updated profiles
    return originalProfiles;
  }
);

export const enrichedProfiles = createSelector(
  [profiles, languageAndAliases, columnGroups],
  (profiles, languageAndAliases, columnGroups) => {
    if (profiles) {
      return profiles.map((profile) => {
        return enrichedProfile(profile, languageAndAliases, columnGroups);
      });
    }
  }
);
