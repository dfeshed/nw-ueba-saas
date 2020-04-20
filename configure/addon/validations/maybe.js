export const maybeValidate = function(validator, condition) {
  return function(key, newValue, oldValue, changes, content) {
    if (condition.call(null, changes, content)) {
      return validator(key, newValue, oldValue, changes, content);
    }
    return true;
  };
};
