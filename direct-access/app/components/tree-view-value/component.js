import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { isFlag, FLAGS } from 'direct-access/services/transport/flag-helper';

export default Component.extend({
  // The node passed by the parent
  node: null,

  tagName: 'span',
  classNames: ['tree-view-value'],
  classNameBindings: ['hasValue::description-nonexistent'],

  @computed('node')
  isSizeValue: (node) => {
    // A stats node that includes the word "memory", "size", or "bytes"
    return isFlag(node.nodeType, FLAGS.STAT_NODE) &&
      (node.name.includes('memory') ||
      node.name.includes('size') ||
      node.name.includes('bytes')
      );
  },

  @computed('node')
  hasValue: (node) => 'value' in node
});
