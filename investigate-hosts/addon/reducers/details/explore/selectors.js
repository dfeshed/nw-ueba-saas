import reselect from 'reselect';
import moment from 'moment';

const { createSelector } = reselect;
const _searchResults = (state) => state.endpoint.explore.fileSearchResults;
const _searchStatus = (state) => state.endpoint.explore.searchStatus;
const _hostDetails = (state) => state.endpoint.overview.hostDetails;

export const searchResultLoading = createSelector(
  _searchStatus,
  (searchStatus) => searchStatus === 'wait'
);

export const searchResultNotFound = createSelector(
  _searchStatus,
  (searchStatus) => searchStatus === 'complete'
);

export const enahancedSearchResult = createSelector(
  _searchResults,
  (searchResults) => {
    if (!searchResults || !searchResults.length) {
      return [];
    }
    return searchResults.map((result) => {
      const { scanStartTime, files } = result;
      const date = moment(scanStartTime);
      const dateForm = date.utc().format('YYYY-MM-DD');
      const timeForm = date.utc().format('HH:mm:ss');
      const val = `${ dateForm } ${ timeForm }`;
      return { ...result, title: `${ val } (${ files.length })` };
    });
  }
);

export const hasValidID = createSelector(
  _hostDetails,
  (hostDetails) => hostDetails ? hostDetails.id : null
);

