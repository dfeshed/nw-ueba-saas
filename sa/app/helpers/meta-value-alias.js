import Ember from 'ember';
import formatUtil from 'sa/components/rsa-investigate/events-table-row/format-util';

const { Helper } = Ember;

export function metaValueAlias([ key, raw, opts ]/* , hash */) {
  return formatUtil.text(key, raw, opts);
}

export default Helper.helper(metaValueAlias);
