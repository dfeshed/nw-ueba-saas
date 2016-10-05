import Ember from 'ember';
import computed from 'ember-computed-decorators';
import StreamState from './stream-state';

const { isEmpty } = Ember;

export default StreamState.extend({
  @computed('status', 'data')
  isEmpty: ((status, data) => (status === 'complete') && isEmpty(data))
});
