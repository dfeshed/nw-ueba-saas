import { createSelector } from 'reselect';
import _ from 'lodash';

const _data = (state) => state.data;
const _widget = (state) => state.widget;

export const mashUpData = createSelector(
  [ _data, _widget],
  (data, widget) => {
    if (widget.aggregate) {
      let aggregate = null;
      const [column] = widget.aggregate.columns;
      aggregate = _.chain(data.items)
        .groupBy((item) => _.get(item, column))
        .map((value, key) => ({ name: key, count: value.length }))
        .value();
      return aggregate;
    }
    return [];
  }
);