import reselect from 'reselect';

const { createSelector } = reselect;

export const selectedTab = (state) => state.processAnalysis.processVisuals.detailsTabSelected;

export const isEventsSelected = createSelector(
  [selectedTab],
  (selectedTab) => {
    if (selectedTab) {
      return selectedTab.name === 'events';
    }
    return false;
  }
);
