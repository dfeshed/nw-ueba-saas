import reselect from 'reselect';
import { lookup } from 'ember-dependency-lookup';

const { createSelector } = reselect;

export const focusedGroup = (state) => state.usm.groups.focusedItem;

export const focusedGroupCriteria = createSelector(
  focusedGroup,
  (focusedGroup) => {
    const _i18n = lookup('service:i18n');
    const focusedGroupCriteria = [];
    if (focusedGroup) {
      for (let i = 0; i < focusedGroup.groupCriteria.criteria.length; i++) {
        const crit = focusedGroup.groupCriteria.criteria[i];
        const [, op] = crit;
        let [, , val] = crit;

        if (op === 'BETWEEN' || op === 'NOT_BETWEEN') {
          const joiner = ` ${_i18n.t('adminUsm.groupCriteria.and')} `;
          val = val.join(',').replace(/,/g, joiner);
        } else {
          val = val.join('').replace(/,/g, ', ');
        }
        focusedGroupCriteria.push([crit[0], crit[1], val]);
      }
    }
    return focusedGroupCriteria;
  }
);