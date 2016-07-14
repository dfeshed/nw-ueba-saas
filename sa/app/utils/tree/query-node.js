/**
 * @file Query Tree Node class
 * Subclass of Tree Node with query-specific logic.
 * @public
 */
import Ember from 'ember';
import TreeNode from './node';

const { get } = Ember;

export default TreeNode.extend({
  isEqual(params) {
    if (params) {
      let value = this.get('value');
      if (value) {
        return (get(params, 'serviceId') === get(value, 'serviceId')) &&
          (get(params, 'startTime') === get(value, 'startTime')) &&
          (get(params, 'endTime') === get(value, 'endTime')) &&
          (get(params, 'metaFilter') === get(value, 'metaFilter'));
      }
    }
    return false;
  }
});
