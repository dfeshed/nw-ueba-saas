import {
  findAllEmptyParens,
  findContiguousOperators,
  findUnnecessaryOperators
} from 'investigate-events/actions/pill-utils';

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

export {
  isKeyPressedOnSelectedParens,
  isNonSelectedSingleParenSet,
  removeContiguousOperators,
  removeEmptyParens,
  removePills,
  removeUnnecessaryOperators
};