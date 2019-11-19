import {
  CLOSE_PAREN,
  COMPLEX_FILTER,
  OPEN_PAREN,
  QUERY_FILTER,
  TEXT_FILTER
} from 'investigate-events/constants/pill';

/**
 * The pill has to be QF or text or complex
 * as well as selected
 */
const _isFilterApplicable = (pill) => {
  return (
    pill.type ===
    QUERY_FILTER ||
    pill.type === TEXT_FILTER ||
    pill.type === COMPLEX_FILTER
  ) && pill.isSelected;
};

/**
 * Given a paren pill, returns startIndex and endIndex of their paren set.
 */
const _findParensIndexes = (pill, pillsData) => {
  const st = pillsData.findIndex((p) => p.twinId === pill.twinId && p.type === OPEN_PAREN);
  const en = pillsData.findIndex((p) => p.twinId === pill.twinId && p.type === CLOSE_PAREN);
  return { st, en };
};

/**
 * Looks for an open paren immediately preceding a closed paren, based off of
 * the close paren's position.
 * @param {Object[]} arr Array of filters
 * @param {number} closeParenIndex Index of closed paren
 */
const _hasEmptyParensAt = (arr, closeParenIndex) => {
  const op = arr[closeParenIndex - 1];
  const cp = arr[closeParenIndex];
  return op && op.type === OPEN_PAREN && cp && cp.type === CLOSE_PAREN;
};


/**
 * Given an array of pills, pick the first and last selected applicable pill
 * We do not need to include parens or operators, which are not applicable
 */
const selectedPillIndexes = (pillsData) => {
  const pills = pillsData.asMutable({ deep: true });
  const lastPillIndex = pills.length - 1;

  const startIndex = pills.findIndex(_isFilterApplicable);
  // Num of pills - index from start -> index from back
  const endIndex = lastPillIndex - pills.reverse().findIndex(_isFilterApplicable);
  return { startIndex, endIndex };
};

// Check if a given set a pills contains any orphaned
// twins. If it does, return the matching twins from state
const findMissingTwins = (pills, pillsFromState) => {
  // filter to those that are missing twins in the input
  const twins = pills.filter((pD) => {
    if (pD.twinId) {
      const twinPresent = pills
        // filter out the pill being processed as it'll
        // obviously have a matching twin id
        .filter((p) => p.id !== pD.id)
        // find twin
        .some((potentialTwinPill) => {
          return pD.twinId === potentialTwinPill.twinId;
        });
      return !twinPresent;
    }
    return false;
  }).map((twinsie) => {
    // now find the twins
    return pillsFromState.find((pill) => {
      // want a matching twin id, but not the exact same pill
      return pill.twinId === twinsie.twinId && pill.id !== twinsie.id;
    });
  });

  return twins;
};

/**
 * Given an array of pills, checks if the array is paren balanced
 */
const isValidToWrapWithParens = (pillsData, startIn, endIn) => {
  const pills = pillsData.slice(startIn, endIn + 1);
  const stack = [];
  let count = 0;
  while (count !== pills.length) {
    const currPill = pills[count];
    if (currPill.type === OPEN_PAREN) {
      stack.push(currPill);
    } else if (currPill.type === CLOSE_PAREN) {
      if (stack.length === 0) {
        return false;
      } else {
        stack.pop();
      }
    }
    count++;
  }

  return stack.length === 0;
};

const selectPillsFromPosition = (pills, position, direction) => {
  let newPills = [];
  if (direction === 'right') {
    newPills = pills.filter((pill) => pills.indexOf(pill) >= position);
  } else if (direction === 'left') {
    newPills = pills.filter((pill) => pills.indexOf(pill) <= position);
  }
  return newPills;
};

/**
 * Find empty paren sets within the array of filters. This is a recursive
 * function, so if you have nested parens like ( ( ( ) ) ), it will find all
 * paren sets if you start from the inner empty paren set.
 * @param {Object[]} pillsData Array of filters
 * @param {number} position Index within `pillsData` to look for empty parens
 */
const findEmptyParensAtPosition = (pillsData, position) => {
  const pillsDataCopy = [...pillsData];
  let emptyParenSets = [];
  let currentPosition = position;
  while (currentPosition >= 0 && _hasEmptyParensAt(pillsDataCopy, currentPosition)) {
    emptyParenSets = emptyParenSets.concat(pillsDataCopy.splice(currentPosition - 1, 2));
    currentPosition--;
  }
  return emptyParenSets;
};

/**
 * Find final position after empty paren sets within the query are removed. This is a recursive
 * function, so if you have nested parens like ( ( ( ) ) ), it will iterate over all
 * empty paren sets if you start from the inner empty paren set.
 * @param {Object[]} pillsData Array of filters
 * @param {number} position Index within `pillsData` to look for empty parens
 */
const findPositionAfterEmptyParensDeleted = (pillsData, position) => {
  const pillsDataCopy = [...pillsData];
  let currentPosition = position;
  while (currentPosition >= 0 && _hasEmptyParensAt(pillsDataCopy, currentPosition)) {
    pillsDataCopy.splice(currentPosition - 1, 2);
    currentPosition--;
  }
  return currentPosition;
};

const isEmptyParenSetAt = (arr, i) => {
  const op = arr[i];
  const cp = arr[i + 1];
  return op && op.type === OPEN_PAREN && cp && cp.type === CLOSE_PAREN;
};

const findAllEmptyParens = (pillsData) => {
  const emptyParenSets = [];
  let i = pillsData.length - 1;
  for (i; i >= 0; i--) {
    if (isEmptyParenSetAt(pillsData, i)) {
      if (pillsData.length === 2) {
        return pillsData;
      } else {
        // remove empty paren set by mutating the pillsData array
        emptyParenSets.push(...pillsData.splice(i, 2));
        return emptyParenSets.concat(findAllEmptyParens(pillsData));
      }
    }
  }
  return emptyParenSets;
};

// Get all the stuff between sets of parens
const contentBetweenParens = (openParensSelected, pillsData) => {
  const result = openParensSelected.reduce((acc, openParen) => {
    const { st, en } = _findParensIndexes(openParen, pillsData);
    return acc.concat(pillsData.slice(st, en + 1));
  }, []);
  // remove duplicates
  return [...new Set(result)];
};

/**
 * Given an array of selected pills and parens, it will return all
 * contents within selected parens + any selected pills outside selected parens
 */
const findSelectedPills = (pillsData) => {
  let count = 0;
  const selectedFilters = [];
  while (count !== pillsData.length) {
    const pill = pillsData[count];
    if (pill.type === OPEN_PAREN && pill.isSelected) {
      const { st, en } = _findParensIndexes(pill, pillsData);
      selectedFilters.push(...pillsData.slice(st, en + 1));
      // start next iteration after it's matching close paren
      count = en + 1;
      continue;
    } else if (pill.isSelected) {
      selectedFilters.push(pill);
    }
    count++;
  }
  return selectedFilters;
};

export {
  contentBetweenParens,
  isEmptyParenSetAt,
  isValidToWrapWithParens,
  findAllEmptyParens,
  findMissingTwins,
  findSelectedPills,
  findEmptyParensAtPosition,
  findPositionAfterEmptyParensDeleted,
  _hasEmptyParensAt, // exported for test
  selectPillsFromPosition,
  selectedPillIndexes
};
