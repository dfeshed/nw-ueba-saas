import set from 'ember-metal/set';
import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'recon/actions/types';
import { augmentResult, handleSetTo } from 'recon/reducers/util';

const textInitialState = {
  decode: true,
  // The string to look for in the text
  metaToHighlight: null,
  textContent: null
};

const textReducer = handleActions({
  [ACTION_TYPES.INITIALIZE]: (state) => ({
    ...textInitialState,
    decode: state.decode // let whatever decode is remain
  }),

  [ACTION_TYPES.TEXT_HIGHLIGHT_META]: (state, { payload }) => ({
    ...state,
    metaToHighlight: payload
  }),

  // If meta is toggled, need to clear metaToHighlight
  [ACTION_TYPES.TOGGLE_META]: (state) => ({
    ...state,
    metaToHighlight: null
  }),

  [ACTION_TYPES.TEXT_DECODE_PAGE]: (state, { payload }) => {
    const newContent = generateHTMLSafeText(augmentResult(payload));
    return {
      ...state,
      textContent: state.textContent ? [...state.textContent, ...newContent] : newContent
    };
  },

  [ACTION_TYPES.TOGGLE_TEXT_DECODE]: (state, { payload = {} }) => ({
    ...state,
    decode: handleSetTo(payload, state.decode),
    textContent: []
  })

}, textInitialState);

/*
 * Processes an array of text entries and normalizes their content
 * for use in the browser. Results in an html-ified array of lines
 * stored in each text entries. Subsequent views will decide what to
 * do with the lines.
 */
const generateHTMLSafeText = (data) => {
  data = Array.isArray(data) ? data : [];
  return data.map((d) => {
    if (typeof(d.text) === 'string') {
      const htmlified = d.text
        .replace(/\</g, '&lt;')
        .replace(/\>/g, '&gt;')
        .replace(/(?:\r\n|\r|\n)/g, '<br>')
        .replace(/\t/g, '&nbsp;&nbsp;')
        .replace(/[\x00-\x1F]/g, '.');
      set(d, 'text', htmlified.split('<br>'));
    }
    return d;
  });
};

export default textReducer;
