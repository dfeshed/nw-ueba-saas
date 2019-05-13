import { warn } from '@ember/debug';

export default function safeCallback(fn) {
  if (typeof fn !== 'function') {
    warn(`Invalid callback invoked in ${this}. Ignoring request.`, { id: 'component-lib.utils.safe-callback' });
  } else {
    const args = [...arguments].slice(1);
    fn(...args);
  }
}
