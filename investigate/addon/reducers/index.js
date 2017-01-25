/* eslint no-unused-vars: 0 */

// Why? https://github.com/ef4/ember-browserify#the-workaround
import thunk from 'npm:redux-thunk';
import reduxPackMiddleware from 'npm:redux-pack';

import recon from 'recon/reducers/index';

export default {
  ...recon
};