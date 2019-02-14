import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import pathParent from 'ngcoreui/reducers/selectors/path-parent';

const pathToNameObject = (path) => {
  if (path === '/') {
    return {
      path: 'tree',
      name: '/'
    };
  } else if (path.indexOf('/') === path.lastIndexOf('/')) {
    return {
      path: path.substring(1),
      name: path.substring(1)
    };
  } else {
    return {
      path: path.substring(1),
      name: path.substring(path.lastIndexOf('/') + 1),
      includeLeadingSlash: true
    };
  }
};

const treePathNav = Component.extend({
  path: null,

  tagName: 'span',

  // Returns an array containing each parent by their full path,
  // up until that path, but modified to handle the root and without
  // the leading slash for handling by the router. e.g. "/sys/stats" => [
  //   { path: "tree", name: "/" },
  //   { path: "sys", name: "sys" },
  //   { path: "sys/stats", name: "stats", includeLeadingSlash: true }
  // ]
  @computed('path')
  pathArray: (path) => {
    let arr = [pathToNameObject(path)];
    let parent = pathParent(path);
    while (parent) {
      arr = [pathToNameObject(parent)].concat(arr);
      parent = pathParent(parent);
    }
    return arr;
  }
});

export default treePathNav;
