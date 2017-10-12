import reselect from 'reselect';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _reconSize = (state) => state.data.reconSize;
const _isReconOpen = (state) => state.data.isReconOpen;
const _metaPanelSize = (state) => state.data.metaPanelSize;

// SELECTOR FUNCTIONS
export const queryBodyClass = createSelector(
  [_reconSize, _isReconOpen, _metaPanelSize],
  (reconSize, isReconOpen, panelSize) => {
    let recon = 'open';
    if (!isReconOpen) {
      recon = 'closed';
    } else if (reconSize === 'max') {
      recon = 'expanded';
    } else if (reconSize === 'full') {
      recon = 'full';
    }
    return `rsa-investigate-query__body recon-is-${recon} meta-panel-size-${panelSize}`;
  }
);
