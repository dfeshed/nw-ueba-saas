const _visitNode = function(node, hashMap, array) {
  if (!hashMap[node.pid]) {
    hashMap[node.pid] = true;
    const data = _extractData(node);
    data.visible = true;
    array.push(data);
  }
};
const _extractData = function({ id, parentPid, pid, name, level, checksumSha256 }) {
  return { id, parentPid, pid, name, level, checksumSha256 };
};
/**
 * Converts the tree json into flat list. Set the depth the tree node as the row level, level is used to
 * align the text in the table
 * @param tree
 * @returns {Array}
 * @public
 */
export const convertTreeToList = function(item) {
  const tree = { ...item };
  const stack = [];
  const newList = [];
  const level = [];
  const depth = -1;
  const hashMap = {};
  stack.push(tree);
  level.push(depth);
  while (stack.length !== 0) {
    const node = stack.pop();
    const dpt = level.pop() + 1;
    node.level = dpt;
    if (!node.childProcesses || !node.childProcesses.length) {
      _visitNode(node, hashMap, newList);
    } else {
      const data = _extractData(node);
      data.expanded = true; // If node has child set expanded = true;
      data.visible = true; // Set all the child visible property
      data.hasChild = true;
      newList.push(data);
      // Iterate all the children
      const length = node.childProcesses.length - 1;
      for (let index = length; index >= 0; index--) {
        level.push(dpt);
        stack.push({ ...node.childProcesses[index] });
      }
    }
  }
  return newList;
};
