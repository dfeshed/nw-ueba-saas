import {
  findAllEmptyParens,
  findContiguousOperators,
  findUnnecessaryOperators
} from 'investigate-events/actions/pill-utils';

import {
  OPERATOR_AND,
  OPERATOR_OR,
  TEXT_FILTER
} from 'investigate-events/constants/pill';

const isKeyPressedOnSelectedParens = (pills, isKeyPress = false) => {
  return isKeyPress && // used delete or backspace key on the parens
    pills.every((d) => d.isSelected); // all parens are selected
};

const isNonSelectedSingleParenSet = (pills) => {
  return pills.length === 2 && pills.every((d) => !d.isSelected); // none are selected
};

const removeContiguousOperators = (pillsData = []) => {
  const contiguousOperatorSets = findContiguousOperators(pillsData);
  if (contiguousOperatorSets.length > 0) {
    let duplicatesToRemove = [];
    contiguousOperatorSets.forEach((set) => {
      // preserve first operator in each set of contiguous operators
      duplicatesToRemove = [
        ...duplicatesToRemove,
        ...set.slice(1)
      ];
    });
    const duplicateIdsToRemove = duplicatesToRemove.map((d) => d.id);
    return removePills(pillsData, duplicateIdsToRemove);
  } else {
    return pillsData;
  }
};

const removeEmptyParens = (pillsData = []) => {
  const emptyParens = findAllEmptyParens(pillsData);
  if (emptyParens.length > 0) {
    const pillIdsToDelete = emptyParens.map((pD) => pD.id);
    return removePills(pillsData, pillIdsToDelete);
  } else {
    return pillsData;
  }
};

const removePills = (pillsData, pillIdsToDelete) => {
  return pillsData.filter((pill) => {
    return !pillIdsToDelete.includes(pill.id);
  });
};

const removeUnnecessaryOperators = (pillsData = []) => {
  const unnecessaryOperators = findUnnecessaryOperators(pillsData);
  if (unnecessaryOperators.length > 0) {
    const pillIdsToDelete = unnecessaryOperators.map((pD) => pD.id);
    return removePills(pillsData, pillIdsToDelete);
  } else {
    return pillsData;
  }
};

// Returns a pills array where an operator after the text pill is replaced
// with an AND if the text pill is the first pill
const replaceOrAfterFirstTextPill = (pillsData) => {
  return pillsData.map((pill, idx, arr) => {
    if (idx === 1 && arr[0].type === TEXT_FILTER && pill.type === OPERATOR_OR) {
      return {
        ...pill,
        type: OPERATOR_AND
      };
    }
    return pill;
  });
};

export {
  isKeyPressedOnSelectedParens,
  isNonSelectedSingleParenSet,
  removeContiguousOperators,
  removeEmptyParens,
  removePills,
  removeUnnecessaryOperators,
  replaceOrAfterFirstTextPill
};