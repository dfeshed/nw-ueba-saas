import set from 'ember-metal/set';
import get, { getProperties } from 'ember-metal/get';


const _visitNode = function(node, hashMap, array) {
  if (!hashMap[get(node, 'pid')]) {
    hashMap[get(node, 'pid')] = true;
    const data = _extractData(node);
    set(data, 'visible', true);
    array.push(data);
  }
};
const _extractData = function(node) {
  return getProperties(node, 'id', 'parentPid', 'pid', 'name', 'level');
};
/**
 * Converts the tree json into flat list. Set the depth the tree node as the row level, level is used to
 * align the text in the table
 * @param tree
 * @returns {Array}
 * @public
 */
export const convertTreeToList = function(tree) {
  const stack = [];
  const array = [];
  const level = [];
  const depth = -1;
  const hashMap = {};
  stack.push(tree);
  level.push(depth);
  while (stack.length !== 0) {
    const node = stack.pop();
    const dpt = level.pop() + 1;
    set(node, 'level', dpt);
    if (!get(node, 'childProcesses') || !get(node, 'childProcesses.length')) {
      _visitNode(node, hashMap, array);
    } else {
      const data = _extractData(node);
      set(data, 'expanded', true); // If node has child set expanded = true;
      set(data, 'visible', true); // Set all the child visible property
      set(data, 'hasChild', true);
      array.push(data);
      // Iterate all the children
      const length = get(node, 'childProcesses.length') - 1;
      for (let index = length; index >= 0; index--) {
        level.push(dpt);
        stack.push(node.childProcesses[index]);
      }
    }
  }
  return array;
};


/**
 * Displaying the process information as a tree format in the UI. On clicking on the parent process, need to show/hide
 * the child rows based on parent expanded/collapsed. For the given row find out all the child using recursion and
 * set the visible property of child based the parent is expanded or not. IF expanded set `visible` property of the
 * item to true else false
 * @param data
 * @param id
 * @param expand
 * @public
 */
export const updateRowVisibility = function(data, processId, expand) {
  for (let i = 0; i < data.length; i++) {
    const { pid, parentPid } = data[i];
    if (parentPid === processId) { // check item is child of process , if so  toggle the visible property
      set(data[i], 'visible', expand);
      if (get(data[i], 'expanded')) { // Need to maintain the state, if parent is expanded, then show the child also
        updateRowVisibility(data, pid, expand);
      }
    }
  }
};
