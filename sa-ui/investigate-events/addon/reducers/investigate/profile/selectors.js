import reselect from 'reselect';
const { createSelector } = reselect;
import _ from 'lodash';
import { languageAndAliasesForParser } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { columnGroups } from 'investigate-events/reducers/investigate/column-group/selectors';
import { transformTextToPillData } from 'investigate-events/util/query-parsing';

// UTIL
const _isEditable = (item) => item?.contentType && item?.contentType !== 'OOTB';

/**
 * enriches and returns one profile with isEditable and preQueryPillsData
 * and column group set to SUMMARY if missing
 * @param {object} profile
 * @param {object} languageAndAliases { language, aliases }
 * @param {object[]} columnGroups
 */
export const enrichedProfile = (profile, languageAndAliases, columnGroups) => {
  const { language, aliases } = languageAndAliases;
  const enriched = _.cloneDeep(profile);
  const preQueryExists = !!profile.preQuery && profile.preQuery.length;
  enriched.isEditable = _isEditable(profile);
  enriched.preQuery = preQueryExists ? profile.preQuery : '';
  const pillsData = preQueryExists ? transformTextToPillData(profile.preQuery.trim(), { language, aliases, returnMany: true }) : [];
  enriched.preQueryPillsData = pillsData;

  // if profile was returned from API without columnGroup property
  // or columnGroupView is 'SUMMARY_VIEW' - because summary columnGroup is not saved
  // assign the summary column group
  if (!enriched.columnGroup || enriched.columnGroupView === 'SUMMARY_VIEW') {
    enriched.columnGroup = columnGroups?.find(({ id }) => id === 'SUMMARY');
  } else {
    // updated profile's column group using id
    // in case column group has changed since profile was created
    const profileColumnGroupId = enriched.columnGroup.id.trim();
    enriched.columnGroup = columnGroups?.find(({ id }) => id === profileColumnGroupId);
  }
  return enriched;
};

// ACCESSOR FUNCTIONS
export const languageAndAliases = (state) => languageAndAliasesForParser(state);
export const isProfileExpanded = (state) => state.listManagers?.profiles?.isExpanded;
const _originalProfiles = (state) => state.investigate?.profile?.profiles || undefined;
const _updatedProfiles = (state) => state.listManagers?.profiles?.list || undefined;

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
