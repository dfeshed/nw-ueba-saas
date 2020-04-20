import Helper from '@ember/component/helper';
import formatUtil from 'investigate-events/components/events-table-container/row-container/format-util';

export function metaValueAlias([ key, raw, opts ]/* , hash */) {
  return formatUtil.text(key, raw, opts);
}

export default Helper.helper(metaValueAlias);
