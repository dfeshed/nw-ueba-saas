import Component from 'ember-component';
import { debounce } from 'ember-runloop';
import { connect } from 'ember-redux';
import { notEmpty } from 'ember-computed-decorators';
import { setQueryString } from 'investigate-events/actions/interaction-creators';

const dispatchToActions = { setQueryString };

const QueryInputComponent = Component.extend({
  classNames: ['rsa-investigate-query-input'],
  classNameBindings: ['hasValue'],
  placeholder: 'Enter a Meta Key and Value (optional)',
  queryString: '',

  @notEmpty('queryString')
  hasValue: true,

  _onChange(value) {
    this.send('setQueryString', value);
  },

  actions: {
    clear() {
      this.set('queryString', '');
      this._onChange('');
    },
    valueChanged(e) {
      debounce(this, this._onChange, e.target.value, 150);
    }
  }
});

export default connect(null, dispatchToActions)(QueryInputComponent);