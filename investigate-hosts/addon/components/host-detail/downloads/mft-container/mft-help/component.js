import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { inject as service } from '@ember/service';
import Component from '@ember/component';

@classic
@tagName('section')
@classNames('mft-help')
export default class MftHelp extends Component {
  @service
  accessControl;
}

