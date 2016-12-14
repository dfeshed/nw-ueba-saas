import redux from 'npm:redux';

import selections from './live-search-selections';
import search from './live-search';

export default {
  live: redux.combineReducers({
    selections,
    search
  })
};