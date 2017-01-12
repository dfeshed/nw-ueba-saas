/* eslint no-unused-vars: 0 */

// Why? https://github.com/ef4/ember-browserify#the-workaround
import thunk from 'npm:redux-thunk';
import reduxPackMiddleware from 'npm:redux-pack';

import testing from './test';
import live from './live-content/index';
import recon from 'recon/reducers/index';

export default {
  testing,
  ...live,
  ...recon
};