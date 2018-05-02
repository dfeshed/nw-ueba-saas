import thunk from 'redux-thunk';
import { middleware } from 'redux-pack';

const setup = () => {};

export default {
  middleware: [thunk, middleware],
  setup
};