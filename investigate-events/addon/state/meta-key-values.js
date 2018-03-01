import { isEmpty } from '@ember/utils';
import computed from 'ember-computed-decorators';
import StreamState from './stream-state';

export default StreamState.extend({
  @computed('status', 'data')
  isEmpty: ((status, data) => (status === 'complete' || status === 'error') && isEmpty(data))
});
