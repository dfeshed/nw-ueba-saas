import reselect from 'reselect';

const HTTP_DATA = 80;

const { createSelector } = reselect;

const meta = ({ data: { meta = [] } }) => meta;

export const isHttpData = createSelector(
  meta,
  (meta) => {
    const service = meta.find((d) => d[0] === 'service');
    return (service && service[1] === HTTP_DATA) ? true : false;
  }
);

export const isNotHttpData = createSelector(
  isHttpData,
  (isHttpData) => !isHttpData
);

