export default (treePath) => {
  if (treePath === null) {
    return null;
  } else if (treePath === '/') {
    return 'tree';
  } else {
    return treePath.substring(1);
  }
};