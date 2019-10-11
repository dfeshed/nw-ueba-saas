import reselect from 'reselect';
const { createSelector } = reselect;
import { languageAndAliasesForParser } from 'investigate-events/reducers/investigate/dictionaries/selectors';
import { transformTextToPillData } from 'investigate-events/util/query-parsing';

// ACCESSOR FUNCTIONS
export const profiles = (state) => state.investigate.profile.profiles || undefined;
const _languageAndAliasesForParser = (state) => languageAndAliasesForParser(state);

// UTIL
const _profileWithIsEditable = (item) => ({
  ...item,
  isEditable: item.contentType && item.contentType !== 'OOTB'
});

// SELECTORS
export const enrichedProfiles = createSelector(
  [profiles, _languageAndAliasesForParser],
  (profiles = [], languageAndAliases) => {
    const { language, aliases } = languageAndAliases;
    return profiles.map((profile) => {
      const enriched = {
        ..._profileWithIsEditable(profile),
        preQueryPillsData: transformTextToPillData(profile.preQuery, { language, aliases, returnMany: true })
      };
      return enriched;
    });
  }
);
