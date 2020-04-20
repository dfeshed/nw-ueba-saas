import thunk from 'redux-thunk';
import { middleware } from 'redux-pack';

export default {
  middleware: [thunk, middleware]
};
