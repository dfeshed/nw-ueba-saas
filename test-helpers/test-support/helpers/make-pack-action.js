import { KEY } from 'redux-pack';

// this utility method will make an action that redux pack understands
export default function makePackAction(lifecycle, { type, payload, meta = {} }) {
  return {
    type,
    payload,
    meta: {
      ...meta,
      [KEY.LIFECYCLE]: lifecycle
    }
  };
}