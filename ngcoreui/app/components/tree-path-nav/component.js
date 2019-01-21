import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { changeDirectory } from 'ngcoreui/actions/actions';
import pathParent from 'ngcoreui/reducers/selectors/path-parent';

const pathToNameObject = (path) => {
  if (path === '/') {
    return {
      path,
      name: '/'
    };
  } else if (path.indexOf('/') === path.lastIndexOf('/')) {
    return {
      path,
      name: path.substring(1)
    };
  } else {
    return {
      path,
      name: path.substring(path.lastIndexOf('/') + 1),
      includeLeadingSlash: true
    };
  }
};

const dispatchToActions = {
  changeDirectory
};

const treePathNav = Component.extend({
  path: null,

  tagName: 'span',

  // Returns an array containing each parent by their full path,
  // up until that path. e.g. "/sys/stats" => [
  //   { path: "/", name: "/" },
  //   { path: "/sys", name: "sys" },
  //   { path: "/sys/stats", name: "stats", includeLeadingSlash: true }
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

export default connect(undefined, dispatchToActions)(treePathNav);
