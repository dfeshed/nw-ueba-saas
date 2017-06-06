import reselect from 'reselect';

const { createSelector } = reselect;

const headerItems = (state) => state.header.headerItems || [];

export const packetTotal = createSelector(
  headerItems,
  (headerItems) => {
    const found = headerItems.findBy('name', 'packetCount');
    if (found) {
      return found.value;
    }
  }
);

