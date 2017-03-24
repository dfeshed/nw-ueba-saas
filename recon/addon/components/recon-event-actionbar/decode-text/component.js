import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import computed, { alias } from 'ember-computed-decorators';
import { decodeText } from 'recon/actions/data-creators';
import { isNotHttpData } from 'recon/selectors/meta-selectors';
import layout from './template';

const stateToComputed = ({ recon }) => ({
  isNotHttpData: isNotHttpData(recon)
});

const dispatchToActions = (dispatch) => ({
  decodeText: (decode) => dispatch(decodeText(decode))
});

const DecodeTextComponent = Component.extend({
  layout,
  isDecoded: true,

  @alias('isNotHttpData')
  isDisabled: null,

  @computed('isDecoded')
  caption: (isDecoded) => isDecoded ? 'Compress' : 'Decompress',

  actions: {
    toggleDecode() {
      this.toggleProperty('isDecoded');
      this.send('decodeText', this.get('isDecoded'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DecodeTextComponent);
