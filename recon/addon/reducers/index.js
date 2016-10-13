import redux from 'npm:redux';

import visuals from './visuals-reducer';
import data from './data-reducer';

export default {
  recon: redux.combineReducers({
    visuals,
    data
  })
};