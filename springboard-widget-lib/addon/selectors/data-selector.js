import { createSelector } from 'reselect';
import _ from 'lodash';

const _data = (state) => state.data;
const _widget = (state) => state.widget;

export const mashUpData = createSelector(
  [ _data, _widget],
  (data, widget) => {
    if (widget.content.length) {
      const chartContent = widget.content.filter((cont) => cont.type === 'chart');
      if (chartContent.length) {
        let aggregate = null;
        chartContent.forEach((config) => {
          if (config.aggregate) {
            const [column] = config.aggregate.columns;
            aggregate = _.chain(data.items)
              .groupBy((item) => _.get(item, column))
              .map((value, key) => ({ name: key, count: value.length }))
              .value();
          }
        });
        return {
          items: data.items,
          aggregate: {
            data: aggregate
          }
        };
      }
    } else {
      return data;
    }
  }
);