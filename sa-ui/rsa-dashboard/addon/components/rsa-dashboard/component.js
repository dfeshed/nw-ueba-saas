import classic from 'ember-classic-decorator';
import { classNames, layout as templateLayout } from '@ember-decorators/component';
import { computed } from '@ember/object';
import Component from '@ember/component';
import layout from './template';

@classic
@templateLayout(layout)
@classNames('rsa-dashboard')
export default class RsaDashboard extends Component {
  config = null;

  @computed('layoutStyle')
  get layoutComponent() {
    return `layout/${this.layoutStyle}-layout`;
  }
}
