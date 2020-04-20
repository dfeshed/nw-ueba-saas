import { get } from '@ember/object';
import Component from '@ember/component';

export default Component.extend({
  tagName: '',

  actions: {
    radioChange(value) {
      this.props.changeset.set(this.attrs.property, value);
      const passiveChange = get(this, 'passiveChange');
      if (passiveChange) {
        passiveChange(value);
      }
    }
  }
});
