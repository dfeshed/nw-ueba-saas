import reselect from 'reselect';
import { isEmpty } from '@ember/utils';
import { lookup } from 'ember-dependency-lookup';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _originalColumnGroups = (state) => state.investigate.columnGroup.columnGroups;
const _updatedColumnGroups = (state) => state.listManagers?.columnGroups?.list;
const _profiles = (state) => state.investigate.profile?.profiles;

/**
 * Takes a list of column groups and looks to see which profiles, if any, the
 * group belongs to. A column group can not be deleted if it belongs to a
 * profile. A column group could belong to more than one profile.
 * @param {*} columns
 * @param {*} profiles
 * @return {Object[]} List of column with two new properties (isDeletable and
 * undeletableReason).
 */
const _markDeletable = (columnGroups, profiles) => {
  const i18n = lookup('service:i18n');
  return columnGroups.map((columnGroup) => {
    const associatedProfiles = profiles.filter((profile) => columnGroup.id === profile?.columnGroup?.id);
    const isDeletable = associatedProfiles.length === 0;
    let reason;
    if (!isDeletable) {
      const profileNames = associatedProfiles.map((d) => d.name).join(', ');
      reason = i18n.t('investigate.events.columnGroups.disabled.delete', { profileNames }).toString();
    }
    return {
      ...columnGroup,
      isDeletable,
      undeletableReason: isDeletable ? undefined : reason
    };
  });
};

export const columnGroups = createSelector(
  [_originalColumnGroups, _updatedColumnGroups, _profiles],
  (originalColumnGroups = [], updatedColumnGroups, profiles = []) => {
    // Refer to listManager columnGroups for the most updated list. Return
    // originalColumnGroups only if listManager state is not updated with
    // columnGroups yet
    const columnGroups = updatedColumnGroups || originalColumnGroups;
    return _markDeletable(columnGroups, profiles);
  }
);

// SELECTOR FUNCTIONS
export const hasColumnGroups = createSelector(
  columnGroups, (columnGroups = []) => !isEmpty(columnGroups)
);
