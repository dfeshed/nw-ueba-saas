import Ember from 'ember';
import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'recon/actions/types';
import { augmentResult } from 'recon/reducers/util';

const { set } = Ember;

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

  [ACTION_TYPES.TEXT_DECODE_PAGE]: (state, { payload }) => {
    const newContent = generateHTMLSafeText(augmentResult(payload));
    return {
      ...state,
      textContent: state.textContent ? [...state.textContent, ...newContent] : newContent
    };
  },

  [ACTION_TYPES.TOGGLE_TEXT_DECODE]: (state, { payload = {} }) => ({
    ...state,
    decode: payload.setTo !== undefined ? payload.setTo : !state.decode,
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
