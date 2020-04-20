import {
  COMPLEX_FILTER,
  QUERY_FILTER
} from 'investigate-events/constants/pill';
import { isValidatablePill } from 'investigate-events/actions/pill-utils';

/**
 * Creates a HashMap where every key is the queryPill string and
 * value represents an array of positions where that pill is supposed
 * to appear in our Query Bar.
 * Ex -> { 'medium = 1', [1, 5] }
 */
export const createPillPositionMap = (pills, initialPosition) => {
  const map = new Map();
  pills.forEach((pill, i) => {
    // Excludes pill types like text, open/close paren, etc.
    if (isValidatablePill(pill)) {
      const pillString = pillAsString(pill);
      if (map.has(pillString)) {
        const positionArr = map.get(pillString);
        positionArr.push(initialPosition + i);
        map.set(pillString, positionArr);
      } else {
        map.set(pillString, [initialPosition + i]);
      }
    }
  });
  return map;
};

export const pillAsString = (pill) => {
  let ret = '';
  if (pill.type === QUERY_FILTER) {
    const m = pill.meta ? pill.meta.trim() : '';
    const o = pill.operator ? pill.operator.trim() : '';
    const v = pill.value ? pill.value.trim() : '';
    ret = `${m} ${o} ${v}`.trim();
  } else if (pill.type === COMPLEX_FILTER) {
    ret = pill.complexFilterText ? pill.complexFilterText.trim() : '';
  }
  return ret;
};

/**
 * Based on the initialPosition, for each pill inside pillsData, contruct
 * an object that stores its position, along with its pill data.
 * Useful when trying to modify pills in state.
 */
export const validatablePillsWithPositions = (pillsData, initialPosition) => {
  return pillsData.map((pill, i) => {
    if (isValidatablePill(pill)) {
      return {
        pillData: pill,
        position: initialPosition + i
      };
    }
  }).filter((p) => !!p);
};