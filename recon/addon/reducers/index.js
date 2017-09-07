import { combineReducers } from 'redux';
import data from './data-reducer';
import dictionaries from './dictionaries/reducer';
import files from './files/reducer';
import header from './header/reducer';
import meta from './meta/reducer';
import notifications from './notifications/reducer';
import packets from './packets/reducer';
import text from './text/reducer';
import visuals from './visuals/reducer';

export default {
  recon: combineReducers({
    data,
    dictionaries,
    files,
    header,
    meta,
    notifications,
    packets,
    text,
    visuals
  })
};
