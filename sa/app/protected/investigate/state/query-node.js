import Ember from 'ember';
import Query from './query';
import TreeNode from 'sa/utils/tree/node';

const {
  computed
} = Ember;

export default TreeNode.extend({
  value: computed(() => Query.create())
});
