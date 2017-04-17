import redux from 'redux';

import visuals from './visuals/reducer';
import data from './data-reducer';
import packets from './packets/reducer';
import meta from './meta/reducer';
import text from './text/reducer';

export default {
  recon: redux.combineReducers({
    data,
    visuals,
    packets,
    meta,
    text
  })
};