// Returns new array that appends an item to an existing array of items.
const push = (arr, item) => {
  return [ ...arr, item ];
};

// Returns new array that has given array's items minus the first instance of a given item, if found;
// otherwise if given item is not found, returns same array.
const remove = (arr, item) => {
  const index = arr.indexOf(item);
  if (index > -1) {
    return removeAt(arr, index);
  } else {
    return arr;
  }
};

// Returns new array from a given array after removing its item at a given index.
const removeAt = (arr, index) => {
  return arr.slice(0, index).concat(arr.slice(index + 1));
};

// If a given array contains the given item, performs a `remove` of that item; otherwise performs a `push` of the item.
const toggle = (arr, item) => {
  const index = arr.indexOf(item);
  if (index > -1) {
    return removeAt(arr, index);
  } else {
    return push(arr, item);
  }
};

export {
  push,
  remove,
  removeAt,
  toggle
};