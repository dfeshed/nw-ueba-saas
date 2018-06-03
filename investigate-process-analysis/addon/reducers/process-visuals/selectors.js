import reselect from 'reselect';

const { createSelector } = reselect;

const _selectedTab = (state) => state.processAnalysis.processVisuals.detailsTabSelected;

export const isEventsSelected = createSelector(
  [_selectedTab],
  (selectedTab) => {
    return selectedTab === 'Events';
  }
);