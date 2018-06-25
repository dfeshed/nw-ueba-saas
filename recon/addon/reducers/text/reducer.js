import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'recon/actions/types';
import { augmentResult, handleSetTo } from 'recon/reducers/util';

const textInitialState = Immutable.from({
  decode: true,
  // storing page number in the state, so that it can be displayed in the recon-pager footer
  textPageNumber: 1,
  // once lastpage is encountered, this gets set to the page number of the last page
  textLastPage: null,
  // properties on the cursor object that indicate what actions we can take for pagination controls
  canFirst: false,
  canPrevious: false,
  canNext: false,
  canLast: false,
  // This flag will be set to true when the user is navigating between pages and the data hasn't retrieved yet.
  // As soon as we receive the response (aka TEXT_RECEIVE_PAGE), it is set to false
  isTextPageLoading: null,
  // If the single message is too large to render
  // this flag will be true and we show a truncated message warning on the UI
  itemTooLarge: false,
  // The string representing meta to search for in the text
  metaToHighlight: null,

  renderIds: null,
  textContent: null
});

const textReducer = handleActions({
  [ACTION_TYPES.REHYDRATE]: (state, { payload }) => {
    let reducerState = {};
    if (payload && payload.recon && payload.recon.text) {
      reducerState = payload.recon.text;
    }
    return state.merge(reducerState);
  },

  [ACTION_TYPES.INITIALIZE]: ({ decode }) => {
    // let whatever decode is remain, otherwise, start over
    return textInitialState.merge({ decode });
  },

  [ACTION_TYPES.TEXT_HIGHLIGHT_META]: (state, { payload }) => {
    return state.set('metaToHighlight', payload);
  },

  // If meta is toggled, need to clear metaToHighlight
  [ACTION_TYPES.TOGGLE_META]: (state) => {
    return state.set('metaToHighlight', null);
  },

  [ACTION_TYPES.TEXT_RECEIVE_PAGE]: (state, { payload }) => {
    const augmentedEntries = augmentResult(payload.data);
    const textContent = (state.textContent || Immutable.from([])).concat(augmentedEntries);
    let { textLastPage } = state;
    // once we get complete=true from MT, there is no more data to paginate.
    // Save the last page to state
    if (payload.meta && payload.meta.complete) {
      textLastPage = state.textPageNumber;
    }
    return state.merge({ textContent, textLastPage, isTextPageLoading: false });
  },

  [ACTION_TYPES.TEXT_UPDATE_CURSOR]: (state, { payload }) => {
    return state.merge({
      canFirst: payload.canFirst,
      canPrevious: payload.canPrevious,
      canNext: payload.canNext,
      canLast: payload.canLast,
      itemTooLarge: payload.itemTooLarge
    });
  },

  [ACTION_TYPES.TEXT_CHANGE_PAGE_NUMBER]: (state, { payload }) => {
    return state.merge({
      textContent: [],
      textPageNumber: payload,
      isTextPageLoading: true
    });
  },

  [ACTION_TYPES.CHANGE_RECON_VIEW]: (state) => state.set('renderIds', []),

  [ACTION_TYPES.TEXT_RENDER_NEXT]: (state, { payload }) => {
    const newIds = payload.map((text) => text.firstPacketId);
    const renderIds = (state.renderIds || Immutable.from([])).concat(newIds);
    return state.merge({ renderIds });
  },

  [ACTION_TYPES.TOGGLE_TEXT_DECODE]: (state, { payload = {} }) => {
    return state.merge({
      decode: handleSetTo(payload, state.decode),
      textContent: [],
      renderIds: [],
      // reset all pagination flags to initial state when decode is toggled
      textPageNumber: 1,
      textLastPage: null,
      canFirst: false,
      canPrevious: false,
      canNext: false,
      canLast: false
    });
  }

}, textInitialState);

export default textReducer;

