import { createSelector } from 'reselect';
import { transform } from 'investigate-shared/utils/meta-util';

const _events = (state) => state.files.fileDetail.eventsData || [];

export const events = createSelector(
  _events,
  (events) => {
    return events.map(transform);
  }
);