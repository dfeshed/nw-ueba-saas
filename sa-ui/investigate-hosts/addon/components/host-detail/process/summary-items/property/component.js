import classic from 'ember-classic-decorator';
import { classNames, classNameBindings, tagName } from '@ember-decorators/component';
import { computed } from '@ember/object';
import Component from '@ember/component';

/**
 * Display key and value and also shows the tool tip if text content are not visible fully
 * @public
 */
@classic
@tagName('box')
@classNames('header-item')
@classNameBindings('cssClass')
export default class Property extends Component {
  item = null;

  @computed('item.cssClass')
  get cssClass() {
    return this.item?.cssClass || 'col-xs-4 col-md-3';
  }

  init() {
    super.init(...arguments);
    if (!this.cssClass) {
      this.set('cssClass', 'col-xs-4 col-md-3');
    }
  }
}
