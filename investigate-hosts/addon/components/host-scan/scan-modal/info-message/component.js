import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import Component from '@ember/component';

@classic
@tagName('hbox')
@classNames('info-message', 'alert-info')
export default class InfoMessage extends Component {}
