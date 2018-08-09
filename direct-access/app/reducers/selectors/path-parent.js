export default (treePath) => {
  if (treePath === '/') {
    return null;
  }
  const lastIndexOf = treePath.lastIndexOf('/');
  if (treePath.indexOf('/') === lastIndexOf) {
    return '/';
  } else {
    return treePath.substring(0, lastIndexOf);
  }
};